# Loan Application Processor

A production-shaped **Spring Batch 5 / Spring Boot 3** mortgage loan processing system designed to mirror Freddie Mac's batch data processing workflows at scale.

## Overview

This system ingests bulk CSV loan application files, validates each record, computes a risk score using LTV/income/credit underwriting logic, classifies the application, and persists results to a relational database — with a full REST API for job triggering and monitoring.

## Architecture

```
CSV Upload → REST API → Spring Batch Pipeline → PostgreSQL
                              │
              ┌───────────────┼───────────────────┐
              ▼               ▼                   ▼
    LoanValidationProcessor  LoanScoringProcessor  LoanClassificationProcessor
    (field validation,       (LTV/income/credit    (APPROVED / REVIEW /
     type coercion)           risk scoring)         REJECTED)
```

### Spring Batch Pipeline

| Step | Processor | Logic |
|------|-----------|-------|
| 1 | `LoanValidationProcessor` | Null checks, creditScore 300–850, amounts > 0 |
| 2 | `LoanScoringProcessor` | Base 100 + 50 (credit>750) − 30 (LTV>80%) + 20 (income/loan>3) |
| 3 | `LoanClassificationProcessor` | ≥150 → APPROVED, 100–149 → REVIEW, <100 → REJECTED |

**Freddie Mac relevance:** The LTV threshold (80%) and income-to-loan ratio (3×) directly reflect standard conforming loan underwriting guidelines used in Freddie Mac's automated underwriting system (AUS).

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3, Java 17 |
| Batch | Spring Batch 5 |
| Persistence | Spring Data JPA, Flyway |
| Database | H2 (dev) / PostgreSQL (prod) |
| Security | Spring Security + JWT (jjwt 0.12) |
| API Docs | SpringDoc OpenAPI 2 / Swagger UI |
| Build | Maven, Docker, Jenkins |

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login → JWT |
| POST | `/api/jobs/trigger` | Upload CSV, trigger batch job |
| GET | `/api/jobs` | List all batch jobs |
| GET | `/api/jobs/{batchId}` | Job status + counts |
| GET | `/api/applications` | Paginated list (filter by status/type) |
| GET | `/api/applications/{id}` | Single application |
| GET | `/api/dashboard/stats` | Approval rate, avg risk score, counts |

## Quick Start

### Dev (H2 in-memory)

```bash
mvn spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console:  http://localhost:8080/h2-console
```

### Docker Compose (PostgreSQL)

```bash
docker-compose up -d
```

### Demo — trigger a batch job

```bash
# 1. Register
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst","password":"password1"}' | jq .

# 2. Login → token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst","password":"password1"}' | jq -r .token)

# 3. Upload sample CSV
curl -s -X POST http://localhost:8080/api/jobs/trigger \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@src/main/resources/sample-applications.csv" | jq .

# 4. Check dashboard
curl -s http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer $TOKEN" | jq .
```

## Sample CSV Format

```csv
applicationId,borrowerName,loanAmount,propertyValue,annualIncome,creditScore,loanType
APP-001,John Smith,350000,400000,95000,780,CONVENTIONAL
```

A 20-row sample is included at `src/main/resources/sample-applications.csv`.

## Key Design Decisions

- **Spring Batch 5 API**: Uses `JobBuilder(name, jobRepository)` and `StepBuilder(name, jobRepository)` constructor signatures required by Batch 5.
- **Composite processor**: All three processors are chained via `CompositeItemProcessor` in a single step — eliminates redundant read/write passes.
- **No Flyway for Batch meta-tables**: Spring Batch manages `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, etc. via `spring.batch.jdbc.initialize-schema=always`. Application tables (V1, V2) are Flyway-managed.
- **SSN masking ready**: `borrowerSsn` field is present on the entity; masking logic can be added in `LoanValidationProcessor`.
- **Stateless JWT**: No session state — suitable for horizontal scaling on EKS.
