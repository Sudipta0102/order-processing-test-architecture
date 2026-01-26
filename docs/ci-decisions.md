# CI Decisions

## Purpose

- Continuous Integration is treated as a **quality control system**, not just an automation script.
- This document captures the key decisions behind the CI structure in this repository.
- The objective is to ensure CI remains:
    - fast for developers
    - trustworthy as a signal
    - scalable as the test suite grows
    - resilient to noise and non-actionable failures

---

## CI Platform

- CI workflows in this repository are executed using **GitHub Actions**.
- Test execution is driven through Maven with JUnit tag-based suite selection.
- Docker Compose is used to provision the multi-service environment for integration validation.

---

## Core CI Principles

- CI is optimized for **high-signal feedback**, not maximum test execution.
- Fast deterministic validation is prioritized on every change.
- Expensive or intentionally unstable suites are isolated to prevent erosion of trust.
- CI should clearly distinguish between:
    - application regressions
    - contract incompatibilities
    - infrastructure noise
    - expected chaos conditions

---

## Tiered Execution Model

- The pipeline is intentionally divided into execution tiers based on cost and stability.

### Pull Request Validation (Gating)

- Executed on every commit and PR.
- Must remain deterministic and fast.
- Includes:

    - `unit` tests (if applicable)
    - `api` boundary validation
    - `contract` compatibility checks
    - stable `integration` workflows

Policy:
- Failures in this tier block merges.
- Flakiness is treated as a defect.

---

### Scheduled Validation (Non-Gating)

- Executed periodically (nightly or dedicated runs).
- Includes higher-cost and resilience-focused suites:

    - extended `integration` coverage
    - `chaos` scenarios with controlled downstream instability
    - selected `e2e` flows (low volume)

Policy:
- These suites provide confidence, not immediate merge gating.
- Instability must remain isolated and explicitly tagged.

---

## Tag-Driven Test Selection

- CI execution boundaries are enforced through JUnit tagging.
- Tags act as an operational control plane, not documentation.

Example execution patterns:

- PR gating:

```bash
mvn test -Dgroups="api,contract,integration"
```

- Scheduled resilience:

```bash
mvn test -Dgroups="chaos,e2e"
```

Policy:
- Untagged tests are considered invalid.
- Tests must not drift into incorrect execution tiers.

---

## Flakiness and Signal Protection

- CI trust is treated as non-negotiable.
- Accidental flakiness is eliminated through:

    - Awaitility-based bounded waiting
    - deterministic assertions via AssertJ
    - isolation of unstable dependencies into chaos suites

- CI does not normalize flaky behavior in gating layers.

---

## Scalability Decisions (100 → 1000+ Tests)

- The pipeline is designed to remain stable as test volume increases.
- Scalability is achieved through:

    - modular test separation (`api`, `contract`, `integration`)
    - tiered execution schedules
    - avoidance of excessive end-to-end expansion
    - parallelization where appropriate

Goal:
- Test growth should increase confidence, not runtime noise.

---

## Failure Interpretation Contract

- CI failures are expected to be actionable.
- Every failure should clearly map to one of:

    - regression in application behavior
    - service contract breakage
    - infrastructure instability
    - expected chaos condition

Ambiguous failures are treated as test suite defects.

---

## Scope in This Repository

- Payment instability is intentionally modeled to validate resilience behavior.
- Chaos conditions are isolated and do not gate PR merges.
- The CI structure reflects a mature distributed-system testing approach rather than exhaustive execution.

---

## Summary

- CI is structured to protect developer velocity while maintaining high confidence.
- Execution boundaries are enforced through tagging and tier separation.
- The pipeline is designed to remain trustworthy and scalable under growth.
