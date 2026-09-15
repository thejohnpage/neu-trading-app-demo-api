# Requirements Traceability

Source: **LEAP-BRS-2026-014 v0.9 — Direct Trading Platform**.

This document records how the reference backend interprets the BRS. Items labelled **Implementation decision** are not requirements stated by the BRS.

| Ref | Requirement summary | Reference implementation |
|---|---|---|
| BR-01 | Client registration and secure sign-in | Client identity model plus JWT-based demo authentication |
| BR-02 | Client can access only own positions, cash and history | Authenticated client identity determines account scope; client endpoints do not trust arbitrary client IDs |
| BR-03 | Time-limited, revocable sessions | Expiring JWT access token plus revocable session/refresh-token record |
| BR-04 | Submit BUY or SELL order | `POST /api/v1/orders` |
| BR-05 | Validate trading rules before acceptance | `OrderValidationService`: tradability, positive quantity, sufficient cash/holding |
| BR-06 | Accepted order recorded before separate execution | DB transaction persists ACCEPTED order/event; Kafka execution is initiated afterwards |
| BR-07 | Status changes visible without manual refresh | Order lifecycle API; server push (SSE/WebSocket) is planned for UI integration |
| BR-08 | Execution uses current market quote | `MarketDataService` abstraction; execution records the quote/pricing decision used |
| BR-09 | Holdings, cash and permanent trade record update together | Single PostgreSQL transaction for fill, position, cash, order status and lifecycle/audit event |
| BR-10 | Current holdings and cash | `/api/v1/me/positions`, `/api/v1/me/cash` |
| BR-11 | Chronological order/fill history | `/api/v1/me/orders` and order detail/lifecycle resources |
| BR-12 | Equities UK/US/India, FX, crypto | Instrument model supports `EQUITY`, `FX`, `CRYPTO`; exchange/currency metadata distinguishes markets |
| BR-13 | Indicative price before order | `/api/v1/instruments/{symbol}/quote` |
| BR-14 | Permanent attributable records | Append-only order events, pricing decisions, fills and cash transactions with timestamps and identity linkage |
| BR-15 | Reconstruct full trade lifecycle | Admin lifecycle endpoint joins order, events, pricing and fill information |
| BR-16 | Reporting must not compete with live trading | Separate `reporting` schema/read model populated asynchronously |
| BR-17 | Business insights | Admin reporting endpoints for volume, active instruments and client activity |
| BR-18 | One additional valuable capability | **Proposed:** client watchlist/price alerts; intentionally deferred until core BRS path is working |

## Additional clarified requirement

The reference implementation includes an **Admin Dashboard API**. Internal users are stored separately from retail clients and can have multiple roles, for example:

- `ADMIN`
- `TRADING_OPERATIONS`
- `RISK`
- `COMPLIANCE`
- `FINANCE`
- `ANALYST`

This is a project clarification rather than a separately numbered BRS requirement. It supports the BRS stakeholders and the service expectation that internal client-data access be restricted to what a role genuinely requires.

## Explicit assumptions

1. One client may own one or more trading accounts even though the initial demo can seed one account per client.
2. An order may have multiple fills even though the demo execution engine may initially fill an accepted order in one fill.
3. The BRS status vocabulary (`SUBMITTED`, `ACCEPTED`, `FILLED`, `REJECTED`) is preserved.
4. Payment/banking integration is not implemented; demo cash is seeded/modelled internally.
5. Production KYC/onboarding is not implemented because the BRS excludes it from this phase.
6. The market-data provider is behind an interface so deterministic fixture quotes and a real provider can be swapped without changing order execution logic.

## Definition of reference-demo success

The backend reference is considered functionally useful when a demo can:

1. authenticate a client;
2. retrieve an indicative quote;
3. submit a valid order;
4. persist it as ACCEPTED before execution;
5. publish/consume the execution request asynchronously;
6. price and fill/reject it;
7. atomically update cash, position and permanent records;
8. retrieve the resulting position, balance and order history;
9. authenticate an internal user and reconstruct the order lifecycle; and
10. query reporting data independently of the live trading path.
