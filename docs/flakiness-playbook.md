# Flakiness Playbook

## Purpose

- Flaky tests reduce CI trust, slow delivery, and obscure real regressions.
- This playbook defines the operational policy for detecting, classifying, and eliminating flakiness at scale.
- The goal is to ensure the test suite remains a reliable quality signal as it grows.

---

## Definition: What Is a Flaky Test?

- A flaky test is one that produces inconsistent outcomes without any relevant code change.
- Common symptoms include:
    - passes locally but fails intermittently in CI
    - fails only under parallel execution
    - depends on timing, ordering, or environmental noise

---

## Flakiness Classification

### Accidental Flakiness (Not Acceptable)

- Accidental flakiness is treated as test debt and must be fixed or quarantined immediately.
- Common causes:
    - `Thread.sleep()` and fixed delays
    - race conditions and async timing assumptions
    - shared mutable state between tests
    - unstable external dependencies leaking into deterministic suites
    - environment-sensitive assertions

Policy:
- Tests in gating layers (`unit`, `api`, `contract`, stable `integration`) must be fully reproducible.

---

### Intentional Instability (Allowed, Controlled)

- Controlled instability is permitted only for resilience validation.
- These scenarios are explicitly isolated under `chaos`-tagged suites.
- Examples include:
    - flaky downstream payment behavior
    - timeout and retry validation
    - recovery under partial failure

Policy:
- Chaos tests do not gate pull requests.
- They run only in scheduled or dedicated pipelines.

---

## Common Root Causes and Fix Patterns

| Root Cause | Example Symptom | Fix Pattern |
|-----------|-----------------|------------|
| Timing assumptions | sleep-based waiting | Awaitility bounded polling |
| Shared state leakage | order-dependent failures | isolate test data + cleanup |
| Async convergence | eventual consistency delays | assert final state, not intermediate |
| Unstable dependencies | intermittent downstream failures | isolate into chaos suites |
| Parallel execution issues | fails only under load | remove global state + enforce isolation |

---

## Enforcement Rules (CI Contract)

- PR pipelines must remain deterministic and high-signal.
- Untagged or unstable tests are treated as invalid.
- Execution boundaries are enforced through tagging:
    - `unit/api/contract/integration` → PR gating
    - `chaos/e2e` → scheduled validation only

- Awaitility-based bounded waiting is used to eliminate timing-related flakiness and avoid brittle sleep-based tests.
- Assertions use AssertJ for readable and diagnostic validation.

---

## Flaky Test Triage Workflow

1. **Identify failure type**
    - regression vs environment vs chaos expectation

2. **Reproduce deterministically**
    - rerun locally with identical configuration

3. **Classify root cause**
    - timing, shared state, dependency instability, ordering

4. **Fix or quarantine**
    - accidental flakiness is fixed immediately
    - chaos behavior is isolated explicitly

5. **Prevent regression**
    - add coverage or enforcement rules to avoid recurrence

---

## Quarantine Policy

- Flaky tests must not silently remain in gating pipelines.
- Quarantine is applied through explicit tagging (e.g., `quarantined`).
- Requirements:
    - every quarantined test must have an owner
    - quarantined suites do not block merges
    - quarantined tests must be resolved within a defined time window

---

## Tooling Practices

- **Awaitility** is used for bounded convergence assertions in async workflows.
- **AssertJ** is used for fluent, readable validation of system outcomes.
- Retries are limited and applied only to explicitly unstable suites.

---

## Success Metrics

- PR suite stability ≥ 99%
- Mean time to fix flaky tests < 24 hours
- No unowned quarantined tests
- Chaos instability remains isolated and non-blocking

---

## Scope in This Repository

- Payment behavior is intentionally unreliable to validate resilience paths.
- Chaos scenarios are expected only in explicitly tagged suites.
- Deterministic layers remain stable and trusted for CI gating.
