# Aviation API Wrapper

Backend microservice (Java + Spring Boot) that integrates with a public aviation data API to return airport information by ICAO code.

---

## Objective (Overview)

This project demonstrates the ability to design and implement a **production-ready backend microservice**, with focus on:

- **Scalability** – clear service layers, stateless, ready for load
- **Resilience** – retry, circuit breaker, fallback
- **Security** – configuration prepared for Keycloak integration
- **Correctness** – documented and stable response contract

### Scope

- Accept HTTP requests to fetch airport details by **ICAO code**.
- Query the public API at **https://aviationapi.com** (or equivalent) for data.
- Expose a **clean, documented** response (name, location, ICAO/IATA, etc.).
- Handle upstream API failures with **timeouts, retries and fallback**.

### What Is Evaluated

- **Scalability**: clear service layers, stateless.
- **Resilience**: retry, circuit breaker, fallback.
- **Extensibility**: code not tightly coupled to a single provider.
- **Observability**: logging, error transparency, metrics (Actuator/Micrometer).

### Assumptions

- No frontend is required.
- The third-party API may be unstable.
- Only ICAO lookup is in scope.
- User management is not required (Keycloak in docker-compose is optional).

### Deliverables

- Executable project (Gradle or Docker).
- README with: setup, run instructions, tests, assumptions, architecture decisions and error handling.
- At least one integration test.

---

## Setup and Run

### Prerequisites

- **Java 21+** (or 25 as per `build.gradle`)
- **Gradle** (wrapper included)

### Run locally

```bash
./gradlew bootRun
```

API is available at **http://localhost:8080**.

### Run with Docker

```bash
docker compose up --build
```

- **API**: http://localhost:8080  
- **Keycloak** (optional): http://localhost:8180 (admin / admin)

### Environment variables (optional)

| Variable | Description | Default |
|----------|-------------|---------|
| `AVIATION_API_BASE_URL` | Base URL of the aviation API | `https://api.aviationapi.com` |
| `SERVER_PORT` | Application port | `8080` |

---

## Tests

### Run all tests

```bash
./gradlew test
```

### Unit tests

- **`AirportServiceTest`**: ICAO validation, mapping, not found, invalid argument.

### Integration tests

- **`AirportIntegrationTest`** (profile `test`):
  - Invalid ICAO → **400**
  - Valid ICAO with unreachable provider (localhost:31999) → **503**

Scenarios covered: success path, provider failure (503), input validation (400).

---

## Architecture and project layout

Package structure (base: `com.sportygroup.aviation.api.wrapper`):

```
aviation-api-wrapper/
├── src/main/java/.../
│   ├── AviationApiWrapperApplication.java
│   ├── config/
│   │   ├── RestClientConfig.java      # HTTP timeouts
│   │   ├── ResilienceConfig.java      # Circuit Breaker + Retry
│   │   ├── OpenApiConfig.java         # Swagger/OpenAPI
│   │   ├── SecurityConfig.java        # Security (Keycloak-ready)
│   │   └── CacheConfig.java          # Caffeine
│   ├── controller/
│   │   └── AirportController.java     # GET /api/v1/airports/{icao}
│   ├── service/
│   │   ├── AirportService.java        # Rules + orchestration + fallback
│   │   └── provider/
│   │       ├── AviationProvider.java       # Interface
│   │       └── AviationApiProvider.java    # aviationapi.com
│   ├── client/
│   │   └── AviationApiClient.java     # Plain HTTP client for external API
│   ├── dto/
│   │   ├── response/
│   │   │   └── AirportResponse.java   # Our API contract
│   │   └── external/
│   │       └── AviationApiAirportDto.java  # Provider format
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ExternalServiceException.java
│   │   └── AirportNotFoundException.java
│   ├── mapper/
│   │   └── AirportMapper.java
│   └── util/
│       └── LogUtil.java               # Structured logging / CorrelationId
├── src/test/.../
│   ├── integration/
│   │   └── AirportIntegrationTest.java
│   └── unit/
│       └── AirportServiceTest.java
├── Dockerfile
├── docker-compose.yml                 # App + Keycloak (latest)
├── build.gradle
└── README.md
```

### Layers

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP only. `GET /api/v1/airports/{icao}`. No business logic. |
| **Service** | Rules and orchestration: validate ICAO, call provider, fallback, map response. |
| **Provider** | Interface `AviationProvider` → `AviationApiProvider`. Swap provider = new implementation, no impact elsewhere. |
| **Client** | External REST call, timeouts. Retry/Circuit Breaker applied in Service (Resilience4j). |
| **DTOs** | `external/` = aviationapi.com format; `response/` = our API contract (low coupling). |

### Resilience (Resilience4j)

- **Retry**: up to 3 attempts with configurable interval.
- **Circuit Breaker**: opens after failures, avoids overloading the provider.
- **Timeout**: connect/read on `RestClient`.
- **Fallback**: on failure, log "Fallback triggered" and return friendly 503 (no stack trace).

Configuration in `application.yaml` and `ResilienceConfig.java`.

### Observability

- **Structured logs**: `LogUtil` (e.g. "Request received", "Calling external API", "External API failed", "Fallback triggered").
- **CorrelationId**: support in `LogUtil` (MDC).
- **Actuator**: health, info, metrics (ready for Prometheus).
- **Swagger UI**: `/swagger-ui.html`, OpenAPI docs at `/v3/api-docs`.

### Implemented extras

- **Cache (Caffeine)**: airport cache by ICAO (configurable TTL and size).
- **Healthcheck**: `GET /actuator/health` (includes circuit breaker indicators).
- **Swagger UI**: API documentation.
- **Docker Compose**: app + Keycloak (latest) for optional authentication.

---

## Architecture decisions and error handling

- **Abstracted provider**: `AviationProvider` interface allows changing the data source (e.g. another site) without changing Controller/Service.
- **Separate DTOs**: external format (`AviationApiAirportDto`) vs. API contract (`AirportResponse`) for independent evolution.
- **Errors as Problem Detail (RFC 7807)**: `GlobalExceptionHandler` returns `ProblemDetail` for 400, 404, 503 and 500.
- **No stack leak in production**: fallback and `ExternalServiceException` with generic message "Service temporarily unavailable".
- **ICAO validation**: 4 letters (e.g. SBSP); invalid → 400.

---

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/airports/{icao}` | Get airport by ICAO |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | Interactive documentation |

### Swagger / OpenAPI links (local, port 8080)

- **Swagger UI (interactive):** http://localhost:8080/swagger-ui.html  
- **Swagger UI index:** http://localhost:8080/swagger-ui/index.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### cURL example

**Get airport by ICAO with verbose output:**
```bash
curl --location 'http://localhost:8080/api/v1/airports/KSPG' \
--header 'accept: application/json'
```

**Health check:**
```bash
curl -s http://localhost:8080/actuator/health | jq
```

---

## AI usage disclaimer

Specifically:

AI was used to help refine and structure the README to make it clearer, more consistent, and easier to follow.

AI assistance was also used in the implementation of logging improvements, particularly the CorrelationId support via LogUtil using MDC (Mapped Diagnostic Context).

All AI-assisted outputs were manually reviewed, tested, and adjusted to ensure correctness, maintainability, and alignment with the project's design principles.
