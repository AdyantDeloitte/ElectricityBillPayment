Electricity Bill Payment API — Quick Start

Prerequisites

- JDK 17+
- Maven 3.9+

Run the application

```bash
mvn -DskipTests spring-boot:run
```

App starts on http://localhost:8080
API base path: `/electricity-bill-payment/v1`

Run on a different port (example 8081)

```bash
mvn -DskipTests spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

API Docs (Swagger)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Health and tools

- Actuator health: `http://localhost:8080/actuator/health`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `ev`)

Response shape

```json
{
  "status": true,
  "message": "...",
  "data": {},
  "errors": [{ "field": "...", "message": "..." }],
  "timestamp": "UTC-ISO"
}
```

On error, `status=false`. Validation errors include an `errors[]` list. 404 returns a clean message.

Run tests

```bash
mvn test
```

Common cURL examples

```bash
# Categories (success)
curl -s http://localhost:8080/electricity-bill-payment/v1/categories | jq .

# Invalid bill USN (validation error)
curl -s http://localhost:8080/electricity-bill-payment/v1/bills/getBillByUSN/INVALID | jq .

# Malformed JSON (400)
curl -s -H 'Content-Type: application/json' --data-binary '{bad' \
  http://localhost:8080/electricity-bill-payment/v1/payments | jq .
```

Notes

- Local development is open (no auth). Error traces are hidden.
- The in-memory H2 database is recreated on each run.
