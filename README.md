# order-processing-test-architecture

## Overview
This repository demonstrates a production-inspired test architecture for an asynchronous, distributed order processing system.

The focus is on:
- Test strategy for distributed systems
- Handling non-deterministic behavior
- Managing flaky dependencies
- Designing reliable CI feedback

This is not a product implementation. It is a test-focused system designed to surface real-world quality challenges.

---

## System Architecture
High-level components:
- API Gateway
- Order Service
- Payment Service (intentionally unreliable)
- Inventory Service
- Notification Worker (async)

A simplified architecture diagram is included in `/docs`.

---

## Key Testing Challenges Addressed
- Eventual consistency
- Flaky downstream services
- Test data isolation and cleanup
- Failure classification in CI

---

## Test Strategy (High Level)
Testing is split across:
- API tests
- Contract tests
- Limited integration tests

Details are documented in `/docs/test-strategy.md`.

---

## CI Pipeline Overview
The CI pipeline is designed to:
- Provide fast feedback
- Separate infrastructure failures from product failures
- Avoid blocking development on non-actionable signals

Details are documented in `/docs/ci-decisions.md`.

---

## What This Project Intentionally Does Not Cover
- UI-heavy testing
- Authentication/authorization
- Production-grade infrastructure
- Performance testing

These exclusions are intentional and documented.

---

## How to Use This Repository
Instructions for running services and tests locally will be added here.

We keep service availability as an external precondition so tests remain simple, explicit, and stable; lifecycle orchestration belongs to infra tooling, not API tests.

To Run a Test:
Simply open a terminal and run
mvn -pl tests/test-folder -Dtest=TestName test

Where test-folder can be test-api, test-contract and test-integration.


---

## Future Improvements
Known limitations and scalability considerations are documented to reflect real-world tradeoffs.
