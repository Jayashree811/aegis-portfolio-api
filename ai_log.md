# AI_LOG.md — Portfolio Management API

This log documents how AI tooling (Antigravity by Google DeepMind) was used during the development of this project.

---

## AI Tools Used

| Tool | Version / Model | Purpose |
|---|---|---|
| Antigravity | Gemini 3.1 Pro / GPT-OSS 120B | Primary code generation, project scaffolding, Spring Boot configuration, algorithmic logic (transactions), and UI generation. |

---

## Significant Prompts

### Prompt 1 — Initial scaffold
> "Build a complete, production-ready Portfolio Management API using Java 17, Spring Boot 3.x, and H2 for local development. The project must include core features like portfolio management, buy/sell transactions with weighted average cost calculations, and a dividend tracking system. The goal is to deliver a professional, industry-grade application with a modern, dark-themed UI embedded directly in the backend."

**What it produced:** 
A full project scaffold including the Maven configuration, structured `src/main/java` packages (controllers, entities, repositories, services, DTOs, exceptions), H2 local configuration, `application.yml`, basic Flyway migration (`V1__init.sql`), and the embedded dark-themed UI via Thymeleaf.

**Assessment:** 
The structure was solid and cleanly implemented the core logic. It correctly used `@RestController` and `@Controller` to separate the API returning JSON from the UI layer.

### Prompt 2 — Refining Weighted Average Cost Calculation
> "Implementation for the buy/sell logic to correctly update the weighted average cost when a new BUY transaction occurs, while subtracting from the holding count during a SELL transaction."

**What it produced:** 
Generated the correct specific arithmetic logic in `TransactionService`. It successfully applied the formula: `newAvg = ((oldQty * oldAvg) + (newQty * newPrice)) / totalQty`.

**Assessment:** 
Flawless logic integration that properly mapped across the `Transaction` historical table and the current `Holding` entity values.

---

## Bugs Introduced by AI

### Bug 1 — Flyway Migration Compatibility (H2 vs PostgreSQL)

**Description:** 
The AI initially generated schema structures in the Flyway migration (`V1__init.sql`) that handled native PostgreSQL types. However, when running the local Spring Boot application, H2 struggled to parse some specific data types natively meant for Postgres.

**Fix:** 
Adjusted the Flyway migration syntax to use standard SQL types compatible with both H2 (for local development) and PostgreSQL (for production). Also ensured Hibernate dialects matched exactly in the `application.yml` and `application-local.yml` configurations.

### Bug 2 — Embedded UI Path Resolution

**Description:** 
The AI generated a Thymeleaf controller that inadvertently overlapped with paths used by the REST API endpoints. Visiting root `/` triggered JSON responses instead of rendering the dark-themed dashboard.

**Fix:** 
Refactored the REST APIs under an `/api/v1/...` base path prefix while leaving the Thymeleaf UI rendering strictly tied to the `/` root and HTML-specific routes.

---

## Design Choice Made Against AI Suggestion

### Topic: Over-abstracting the Dividend Tracking

**AI suggestion:** 
The AI suggested creating an entire automated background cron job (using `@Scheduled`) that polls market exchanges to sync real-world dividend payouts instantly.

**Decision made:** 
Rejected for this submission. The project's current scope required a functional dividend tracking system but manual inputs (via `DividendRequest`) are much more testable and simpler for an initial production-ready release than needing external stock API keys.

---

## Time Split

| Activity | Estimated Time |
|---|---|
| Initial AI prompt + reviewing generated scaffold | 20 min |
| Tuning average cost mathematical logic & UI aesthetics | 15 min |
| Correcting Flyway / H2 Database issues | 10 min |
| Testing the API manually & debugging overlapping routes | 15 min |
| Committing to GitHub & writing AI_LOG / README | 10 min |
| **Total** | **~1 h 10 min** |

---

*Logged by: Jayashree — Portfolio Management API, April 2026*
