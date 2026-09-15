# NEU Trading App Demo API

Reference backend implementation derived from **LEAP-BRS-2026-014 v0.9** for the LEAP direct trading platform capstone.

## Purpose

This repository has two goals:

1. Demonstrate what can be derived from the business requirements specification without a prescribed technical design.
2. Provide a runnable, teachable reference implementation for the API and persistence side of the capstone.

The implementation deliberately favors clarity over production complexity.

## Proposed stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Security / JWT fixture authentication
- Spring Data JPA
- PostgreSQL 16
- Flyway migrations
- Apache Kafka
- Docker Compose

## Architecture

```text
Client UI (separate repository)
        |
        v
Spring Boot REST API
        |
        +---- PostgreSQL (transactional system of record)
        |
        +---- Kafka: order.accepted
                    |
                    v
             Execution consumer
                    |
                    v
             atomic fill transaction
                    |
        +-----------+-----------+
        |           |           |
     orders      positions     cash
        |
        +---- append-only lifecycle/audit records

Reporting reads from a separate reporting schema/read model so analytical work can be separated from the live trading path.
```

## Key BRS-derived decisions

- Retail clients and internal/admin users are distinct identities.
- Internal users use role-based access control.
- An accepted order is committed before asynchronous execution begins.
- Order lifecycle events are append-only.
- Execution pricing records the quote actually used.
- Fill, position, cash, order status and audit changes occur in one database transaction.
- Financial quantities and prices use PostgreSQL `NUMERIC`, not floating point.
- The model supports EQUITY, FX and CRYPTO instruments.
- Current positions/cash are projections; immutable transaction/event records provide history.

See [REQUIREMENTS.md](REQUIREMENTS.md) for traceability and [ARCHITECTURE.md](ARCHITECTURE.md) for design rationale.

## Repository status

This repository is being built incrementally as a reference implementation. The initial commits establish the architecture, database model and runnable Spring Boot foundation before the complete trading workflow is added.

## Run target

When the scaffold is complete, the intended developer workflow is:

```bash
docker compose up -d
./mvnw spring-boot:run
```

The API will expose health and version endpoints first, followed by authentication, instruments, quotes, orders, positions, cash, audit/admin and reporting endpoints.

## Source requirements

Business Requirements Specification: LEAP-BRS-2026-014, Version 0.9, issued 20 August 2026.

The BRS does not prescribe architecture. Technology choices in this repository are therefore implementation decisions made for the reference solution, not requirements stated by the BRS.
