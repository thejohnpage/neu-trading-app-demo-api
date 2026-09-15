# Reference Architecture

## Architectural intent

The BRS requires durable order intent, asynchronous/separate execution, atomic settlement effects, reconstructable history, client isolation and reporting that does not interfere with live trading. This reference architecture makes those concerns visible rather than hiding them behind a single CRUD service.

## Components

### Spring Boot API

Owns HTTP authentication/authorization, client queries, order submission and admin resources.

### PostgreSQL

System of record. Logical schemas:

- `identity` — retail clients, internal users, roles and sessions
- `trading` — accounts, instruments, orders, events, fills, positions, cash and quote snapshots
- `audit` — explicit pricing/audit records where useful
- `reporting` — analytical read model

### Kafka

Separates accepted order commitment from execution. The first reference topic is:

```text
order.accepted
```

An execution consumer handles the accepted order only after its database commitment exists.

## Order lifecycle

```text
POST /orders
     |
     v
validate request and trading rules
     |
     +-- invalid ------------------> REJECTED response / record as appropriate
     |
     v
DB transaction
  INSERT order
  INSERT SUBMITTED event
  INSERT ACCEPTED event
COMMIT
     |
     v
publish order.accepted
     |
     v
execution consumer
     |
     v
obtain current quote
     |
     v
DB transaction
  INSERT pricing decision
  INSERT fill
  UPDATE position
  INSERT cash transaction
  UPDATE cash balance
  UPDATE order -> FILLED
  INSERT FILLED event
COMMIT
```

If execution cannot proceed, the execution path records a `REJECTED` lifecycle event and final order status rather than erasing the accepted intent.

## Atomic fill boundary

The fill transaction is deliberately a single database transaction. A successful commit means the order status, fill, holdings, cash and permanent history agree. A failure rolls all of them back.

## Current state versus history

`positions` and `cash_balances` answer current-state questions efficiently. `order_events`, `fills`, `cash_transactions` and pricing records preserve how that state was reached.

This avoids treating a mutable balance row as an audit trail.

## Identity boundary

Retail `clients` and internal `users` are separate concepts. Internal users receive RBAC roles. Client-facing endpoints derive ownership from the authenticated principal rather than accepting a client identifier as authority.

## Reporting

The BRS explicitly requires analytical activity not to compete with live trading. The reference therefore treats reporting as a read model. Kafka/event-driven projection is preferred for the demo because it makes that separation visible.

A production implementation might use a separate database/warehouse. The demo may initially use a separate PostgreSQL schema while preserving the architectural boundary.

## Reliability note: DB + Kafka

A naïve implementation can commit an accepted order and then fail before publishing Kafka, or publish before the DB commit. The reference implementation should therefore evolve to a **transactional outbox** pattern:

```text
same DB transaction:
  order ACCEPTED
  order event ACCEPTED
  outbox event order.accepted

outbox publisher -> Kafka
```

This is an implementation decision motivated by the BRS requirement that accepted intent survive failures and trades not be lost or duplicated.

## Scope discipline

The demo intentionally does not claim to implement production brokerage concerns that the BRS does not specify, including full regulatory controls, KYC, real banking integration, exchange connectivity, settlement infrastructure, corporate actions or production-grade market-data entitlements.
