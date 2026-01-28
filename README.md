# Order Processing Test Architecture

## How to Read This Repository (For Reviewers)

- This repository is a testing architecture showcase focused on validating distributed, asynchronous workflows.

- Start with the core documentation:
  - TESTING_STRATEGY.md:
    - overall testing principles, taxonomy, async validation, and scalability model

  - ci-decisions.md:
    - CI tiering (PR vs nightly), tag-driven execution boundaries, and signal protection

  - flakiness-playbook.md:
    - accidental vs intentional instability, quarantine policy, and flakiness triage workflow

- Then explore the implementation:
  - tests/ → tagged test layers (api, contract, integration, chaos, e2e)
  - .github/workflows/ci.yml → GitHub Actions workflow enforcing execution tiers
  - app-services/ → order, payment (intentionally flaky), and inventory services
- Key focus areas:
  - eventual consistency and async workflow validation
  - controlled downstream instability (intentional chaos)
  - scalable test suite design that remains reliable at 1000+ tests

## Project Summary

A runnable distributed order-processing system designed to demonstrate a **mature test strategy** for async workflows, partial failures, and high-signal CI execution.

## Key Testing Goals (Why This Exists)

- Validate workflows under **eventual consistency** rather than assuming immediate state convergence  
- Test resilience against **partial failures and unreliable downstream dependencies**  
- Showcase layered validation through:
  - API tests  
  - contract tests  
  - integration tests  
  - controlled chaos scenarios  
- Maintain **high CI signal quality** by separating fast PR checks from scheduled resilience suites  
- Ensure the test suite remains scalable and maintainable as it grows to **1000+ tests**

## Architecture Overview (Current Implementation)

This project currently models an order-processing workflow composed of three independently deployable services:

- **Order Service** — orchestrates order placement and downstream interactions  
- **Payment Service** — simulates payment processing, including unreliable behavior  
- **Inventory Service** — validates and reserves stock availability  

### High-Level Flow

```
Client / Tests
     |
     v
Order Service
     |
     +-------------------+
     |                   |
     v                   v
Payment Service      Inventory Service
```

### Key Characteristics

- Multi-service workflow with real HTTP integration points  
- Payment dependency can be unstable to validate resilience behavior  
- Testing focuses on service interaction correctness rather than isolated mocks  


## Test Suite Structure

| Tag          | Purpose                                   | CI Frequency |
|--------------|-------------------------------------------|--------------|
| `api`        | HTTP boundary correctness                  | PR           |
| `contract`   | Producer/consumer compatibility checks     | PR           |
| `integration`| Multi-service workflow validation          | PR/Nightly   |
| `chaos`      | Resilience under controlled instability    | Nightly      |
| `e2e`        | Critical full-system flows (low volume)    | Nightly      |


## CI Execution Model

- **Pull Requests** run fast deterministic suites (`unit`, `api`, `contract`, stable `integration`)
- **Nightly pipelines** run extended integration and controlled chaos scenarios
- Failures are expected to remain actionable and clearly attributable
- PR runs stable suites only. Chaos scenarios execute nightly to preserve CI trust.

## Quick Start (Run Locally)

### Prerequisites
- Docker + Docker Compose
- Java + Maven

### Start services

```bash
docker-compose up --build
```

### Run tests

```bash
mvn test
```

---

## Documentation Entry Points

- `TESTING_STRATEGY.md` → detailed principles and enforcement model  
- `tests/` → layered test suite implementation  
- `.github/workflows/ci.yml` → CI tiering and execution boundaries  

---

## Planned Extensions

- **Dedicated Notification Worker**
  - Asynchronous side effects are currently simulated via controlled background execution within services.
  - A separate event-driven notification worker is a planned extension to demonstrate:
    - failure isolation across processes
    - delivery semantics (at-least-once, retries)
    - idempotent event handling and dead-letter scenarios


## Portfolio Context

This repository was built as a professional SDET showcase to demonstrate mature testing judgment for distributed, asynchronous systems, with emphasis on scalability, resilience, and CI signal quality.
