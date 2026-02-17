package com.sportygroup.aviation.api.wrapper.config;

import com.sportygroup.aviation.api.wrapper.exception.AirportNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    public static final String AIRPORT_CIRCUIT_BREAKER = "airport";
    public static final String AIRPORT_RETRY = "airport";

    @Bean
    public CircuitBreaker airportCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build();
        return registry.circuitBreaker(AIRPORT_CIRCUIT_BREAKER, config);
    }

    @Bean
    public Retry airportRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception.class)
                .ignoreExceptions(
                        AirportNotFoundException.class,
                        IllegalArgumentException.class)
                .build();
        return registry.retry(AIRPORT_RETRY, config);
    }
}
