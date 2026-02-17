package com.sportygroup.aviation.api.wrapper.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility for structured logging and optional correlation ID.
 */
public final class LogUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";

    public static void putCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
    }

    public static String getOrCreateCorrelationId() {
        String id = MDC.get(CORRELATION_ID_KEY);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID_KEY, id);
        }
        return id;
    }

    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    /** Structured log: message + key=value pairs */
    public static void logInfo(org.slf4j.Logger log, String message, Object... keyValues) {
        if (!log.isInfoEnabled()) return;
        String structured = toStructured(keyValues);
        log.info("{} {}", message, structured);
    }

    public static void logError(org.slf4j.Logger log, String message, Object... keyValues) {
        Throwable t = null;
        Object[] kv = keyValues;
        for (int i = 0; i < keyValues.length; i++) {
            if (keyValues[i] instanceof Throwable throwable) {
                t = throwable;
                Object[] rest = new Object[keyValues.length - 1];
                System.arraycopy(keyValues, 0, rest, 0, i);
                System.arraycopy(keyValues, i + 1, rest, i, keyValues.length - i - 1);
                kv = rest;
                break;
            }
        }
        String structured = toStructured(kv);
        if (t != null) {
            log.error("{} {}", message, structured, t);
        } else {
            log.error("{} {}", message, structured);
        }
    }

    private static String toStructured(Object[] keyValues) {
        if (keyValues == null || keyValues.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            if (i > 0) sb.append(" ");
            sb.append(keyValues[i]).append("=").append(keyValues[i + 1]);
        }
        return sb.toString();
    }
}
