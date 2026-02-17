package com.sportygroup.aviation.api.wrapper.exception;

/**
 * Thrown when the airport is not found for the given ICAO code.
 */
public class AirportNotFoundException extends RuntimeException {

    public AirportNotFoundException(String icao) {
        super("Airport not found for ICAO: " + icao);
    }
}
