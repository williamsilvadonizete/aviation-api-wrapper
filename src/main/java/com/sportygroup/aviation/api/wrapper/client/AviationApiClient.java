package com.sportygroup.aviation.api.wrapper.client;

import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;
import com.sportygroup.aviation.api.wrapper.exception.ExternalServiceException;
import com.sportygroup.aviation.api.wrapper.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Plain HTTP client for the external API. Responsible for timeouts; retry and circuit breaker
 * are applied at the service layer via Resilience4j.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AviationApiClient {

    private static final ParameterizedTypeReference<Map<String, List<AviationApiAirportDto>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient aviationRestClient;

    @Value("${aviation.api.base-url}")
    private String baseUrl;

    /**
     * Fetches airport by ICAO from api.aviationapi.com.
     * API returns { "ICAO": [ { ... } ] }; we return the first airport in the list for that code.
     */
    public Optional<AviationApiAirportDto> fetchByIcao(String icao) {
        String key = icao.toUpperCase();
        LogUtil.logInfo(log, "Calling external API", "icao", key, "baseUrl", baseUrl);
        try {
            var response = aviationRestClient
                    .get()
                    .uri(baseUrl + "/v1/airports?apt={apt}", key)
                    .retrieve()
                    .body(RESPONSE_TYPE);

            if (response == null || !response.containsKey(key)) {
                return Optional.empty();
            }
            var list = response.get(key);
            return list != null && !list.isEmpty() ? Optional.of(list.getFirst()) : Optional.empty();
        } catch (Exception e) {
            LogUtil.logError(log, "External API failed", "icao", key, e);
            throw new ExternalServiceException("Aviation API error: " + e.getMessage(), e);
        }
    }
}
