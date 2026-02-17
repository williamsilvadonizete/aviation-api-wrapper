package com.sportygroup.aviation.api.wrapper.service;

import com.sportygroup.aviation.api.wrapper.dto.response.AirportResponse;
import com.sportygroup.aviation.api.wrapper.exception.AirportNotFoundException;
import com.sportygroup.aviation.api.wrapper.exception.ExternalServiceException;
import com.sportygroup.aviation.api.wrapper.mapper.AirportMapper;
import com.sportygroup.aviation.api.wrapper.service.provider.AviationProvider;
import com.sportygroup.aviation.api.wrapper.util.LogUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Orchestrates business rules: ICAO validation, provider call, fallback and mapping.
 * Retry, Circuit Breaker and Timeout are applied here (Resilience4j).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

    private static final String ICAO_PATTERN = "^[A-Za-z]{4}$";

    private final AviationProvider aviationProvider;
    private final AirportMapper airportMapper;
    private final CircuitBreaker airportCircuitBreaker;
    private final Retry airportRetry;

    /**
     * Fetches airport by ICAO with retry, circuit breaker and fallback.
     */
    @Cacheable(value = "airports", key = "#icao.toUpperCase()", unless = "#result == null")
    public AirportResponse getByIcao(String icao) {
        String normalized = normalizeIcao(icao);
        validateIcao(normalized);

        LogUtil.logInfo(log, "Request received", "icao", normalized);

        Supplier<AirportResponse> supplier = () ->
                aviationProvider.getByIcao(normalized)
                        .map(airportMapper::toResponse)
                        .orElseThrow(() -> new AirportNotFoundException(normalized));

        var withResilience = Retry.decorateSupplier(airportRetry,
                CircuitBreaker.decorateSupplier(airportCircuitBreaker, supplier));

        try {
            return withResilience.get();
        } catch (Exception e) {
            return getByIcaoFallback(normalized, e);
        }
    }

    /**
     * Fallback: log and friendly failure. In production could return cached value.
     */
    private AirportResponse getByIcaoFallback(String icao, Throwable t) {
        LogUtil.logError(log, "Fallback triggered", "icao", icao, t);
        if (t instanceof AirportNotFoundException e) {
            throw e;
        }
        throw new ExternalServiceException("Service temporarily unavailable. Try again later.", t);
    }

    private String normalizeIcao(String icao) {
        return icao == null ? "" : icao.trim().toUpperCase();
    }

    private void validateIcao(String icao) {
        if (icao == null || !icao.matches(ICAO_PATTERN)) {
            throw new IllegalArgumentException("Invalid ICAO code: must be 4 letters (e.g. SBSP)");
        }
    }
}
