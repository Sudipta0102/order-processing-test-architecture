# Test Strategy
## 1. Purpose & Non-Goals
Showcase a test architecture for a runnable, distributed, asynchronous order-processing system
### Purpose:
Demonstrate mature testing judgment in the presence of:
- eventual consistency
- partial failures
- non-deterministic behavior

Focus on test strategy, trade-offs, and CI signal quality, not tool usage.
### Non-goal:
- Not a production system
- Not exhaustive coverage
- Not tool showcase
## 2. Testing Philosophy
- Testing is approached as a risk-based portfolio, not a single uniform suite
  - different test layers provide different confidence signals
  - execution cost and feedback speed are treated as first-class constraints


- The primary objective is reliable and actionable feedback
  - tests should fail only for meaningful regressions
  - non-actionable noise (flakiness, timing sensitivity) is actively minimized


- The strategy prioritizes realistic system behavior over excessive mocking
  - distributed failures occur at service boundaries
  - critical interactions are validated through controlled integration testing


- Asynchronous workflows are tested explicitly
  - eventual consistency is expected and modeled
  - assertions are time-bounded and deterministic, avoiding arbitrary delays


- Failure scenarios are treated as essential test cases
  - downstream instability is introduced intentionally where it improves resilience validation
  - the goal is to verify recovery behavior, not only success paths


- Test scalability is enforced through clear execution boundaries
  - fast deterministic tests run continuously on every change
  - higher-cost integration and chaos-oriented tests are isolated and scheduled appropriately


- Continuous Integration is designed for high trust and clear diagnostics
  - failures should indicate whether the cause is:
    - application regression
    - contract incompatibility
    - infrastructure instability
    - expected chaos conditions


- Assertions are expressed using **AssertJ** for readable, fluent validation of final system state and side effects.


- The suite is structured to remain maintainable at scale
  - consistent tagging, ownership, and execution rules prevent uncontrolled test growth
  - the strategy supports expansion to large test volumes without degrading CI reliability
## 3. System Under Test (Testing View)

- This repository models a distributed order-processing workflow composed of multiple independently deployed services
  - requests enter through an API boundary
  - downstream processing involves multiple service interactions and asynchronous side effects
  

- The system includes both synchronous and asynchronous communication paths
  - synchronous calls are used for immediate validation and orchestration
  - asynchronous events/workers handle eventual side effects such as notifications


- Eventual consistency is an expected operational characteristic
  - state transitions may not be immediately visible across services
  - tests are designed to validate outcomes over time rather than assuming instant consistency


- Certain dependencies are intentionally unreliable to reflect real-world conditions
  - downstream failures (e.g., payment instability) are introduced to validate resilience
  - the goal is to test recovery behavior under partial failure, not only ideal execution


- Service boundaries are treated as primary test surfaces
   - contracts, integration points, and error handling are validated explicitly
   - correctness is evaluated at interaction boundaries rather than internal implementation details


- The system is designed to support testing of common distributed-system challenges, including:
  - transient failures and retries
  - delayed processing and asynchronous completion
  - message-driven workflows and worker execution
  - isolation of failure domains across services


- The test strategy assumes that meaningful validation must account for:
  - timing variability
  - non-deterministic ordering of async events
  - realistic infrastructure behavior in containerized environments
## 4. Test Taxonomy & Tagging Contract
- The test suite is organized as a layered taxonomy, where each category serves a distinct purpose
  - this prevents uncontrolled growth and maintains clear execution boundaries
  - each layer is associated with an expected cost, stability level, and CI frequency


- Every test must belong to exactly one primary category via an explicit tag
  - untagged tests are considered invalid
  - mixed-layer tests are strongly discouraged to preserve suite clarity


- The following test categories are supported:
  - `unit`
    - validates isolated business logic
    - fully deterministic and fast
    - no network or infrastructure dependencies
  - `api`
    - validates synchronous service interfaces at the HTTP boundary
    - focuses on request/response correctness and error handling
    - runs without requiring full multi-service orchestration
  - `contract`
    - validates compatibility between service producers and consumers
    - ensures schema and interaction stability across independent deployments
    - prevents integration breakage before runtime
  - `integration`
    - validates multi-service workflows under realistic conditions
    - includes async behavior and persistence
    - expected to be slower but must remain stable and reproducible
  - `chaos`
    - validates resilience under controlled downstream instability
    - failures are intentional and used to verify recovery paths
    - isolated from PR gating and typically executed on scheduled pipelines
  - `e2e`
    - validates critical user-facing flows across the full system
    - low in volume due to execution cost
    - used primarily for release-level confidence

  
- CI execution is structured around these categories:
  - Pull Request / Continuous Validation
    - unit
    - api
    - contract
    - stable integration
  - Scheduled / Nightly Validation
    - chaos
    - full integration coverage
    - selected e2e flows


- Test categories are enforced through CI selection (JUnit tags), for example:
  - `mvn test -Dgroups=unit` for fast PR validation
  - `mvn test -Dgroups=integration` for stable workflow coverage
  - `mvn test -Dgroups=chaos` in scheduled resilience pipelines


- This contract ensures the suite remains scalable as it grows
  - fast feedback is preserved for developers
  - higher-cost tests are isolated appropriately
  - intentional instability does not reduce CI trustworthiness


- Tagging is treated as an enforcement mechanism, not a documentation aid
  - tags define execution policy, ownership expectations, and failure interpretation
  - the taxonomy is designed to support growth to large test volumes without degrading signal quality
  - CI enforces tagging discipline: untagged tests are treated as invalid and fail validation.
## 5. Asynchronous & Eventual Consistency Testing
- The system under test exhibits asynchronous processing and delayed state propagation
  - not all outcomes are immediately observable after a request completes
  - tests must account for time, ordering, and independent execution of components


- Eventual consistency is treated as an expected behavior, not an exception
  - tests validate final system state rather than intermediate transitions
  - correctness is defined by convergence within an acceptable time window
  

- Tests avoid fixed delays and arbitrary sleeps
  - static waiting introduces flakiness and inflates execution time
  - assertions are implemented using time-bounded polling or await-based mechanisms
  - Asynchronous outcomes are validated using **Awaitility** with time-bounded polling rather than fixed delays.
    - this ensures tests remain stable under variable execution timing
    - failures provide clear diagnostics when convergence does not occur within the expected window


- Asynchronous outcomes are validated using bounded await-style assertions rather than sleeps, e.g.:
  - wait up to a defined timeout for the system to converge
  - fail with clear diagnostics if the expected state is not reached


- Asynchronous assertions follow a deterministic pattern
  - each async expectation has:
    - a clear success condition
    - an upper time bound
    - a well-defined failure message
  - this ensures failures are diagnosable rather than timing-dependent


- The strategy distinguishes between **slow systems and broken systems**
  - transient delays within defined bounds are tolerated
  - violations of convergence guarantees are treated as test failures


- Ordering of async events is not assumed unless explicitly guaranteed
  - tests assert observable outcomes, not internal sequencing
  - this prevents coupling tests to implementation details


- Asynchronous behavior is validated at appropriate boundaries
  - tests focus on externally visible state and side effects
  - internal worker execution and scheduling remain opaque to tests


- This approach ensures that async tests remain:
  - stable under variable execution timing
  - representative of real-world behavior
  - scalable as test volume and system complexity increase
## 6. Flakiness Policy (Intentional vs Accidental)
- The test strategy explicitly distinguishes between intentional instability and accidental flakiness
  - intentional instability is a controlled testing mechanism
  - accidental flakiness is treated as test debt and actively eliminated


- Accidental flakiness is not acceptable
  - tests must not fail due to:
    - arbitrary timing assumptions
    - environment noise
    - shared state leakage
    - nondeterministic assertions
  - flaky tests reduce CI trust and are considered defects in the test suite


- Intentional instability is permitted only within clearly scoped boundaries
  - specific downstream behaviors (e.g., payment failures) are introduced deliberately
  - these scenarios exist to validate resilience, retry logic, and recovery paths


- Tests that rely on intentional instability are isolated from standard CI gating
  - such tests are tagged explicitly (e.g., chaos)
  - they are executed in scheduled or dedicated pipelines rather than blocking every PR


- Flakiness is treated as an observable signal with defined ownership
  - failures must be attributable to one of the following categories:
    - application regression
    - contract incompatibility
    - infrastructure instability
    - expected chaos condition
  - ambiguous failures are considered unacceptable


- The suite enforces reproducibility wherever possible
  - instability should be configurable and measurable, not random
  - chaos conditions must be controlled through explicit parameters and documented expectations


- The overall objective is to maintain a test suite that is:
  - trusted by developers
  - resilient under distributed-system realities
  - scalable without accumulating nondeterministic noise
## 7. CI Execution Strategy
- Continuous Integration is designed to provide fast, reliable, and high-signal feedback
  - the goal is to detect actionable regressions early
  - CI must remain trusted as the primary quality gate
  

- The test suite is executed in tiers, based on cost, stability, and purpose
  - not all tests are expected to run on every change
  - execution frequency is aligned with risk and feedback requirements


- **`Pull Request / Continuous Validation`** focuses on fast deterministic confidence
  - executed on every commit and PR
  - includes:
    - unit tests for isolated correctness
    - api tests for synchronous boundary validation
    - contract tests for service compatibility
    - stable integration tests for core workflows


- **`Scheduled / Nightly Validation`** focuses on broader system resilience
  - executed periodically rather than gating every PR
  - includes:
    - chaos tests validating recovery under instability
    - extended integration coverage
    - selected e2e flows for end-to-end confidence


- CI failures are expected to provide clear diagnostic meaning
  - failures should indicate whether the issue is due to:
    - application regression
    - contract breakage
    - infrastructure instability
    - expected chaos behavior
  - ambiguous or non-actionable failures are treated as test suite defects


- Execution boundaries are enforced to ensure scalability
  - fast feedback remains consistent as test volume grows
  - higher-cost suites are isolated to prevent CI degradation
  - tagging acts as the control mechanism for execution policy


- The CI strategy prioritizes long-term maintainability
  - supports growth to large test counts without excessive runtime increase
  - prevents flaky or unstable scenarios from eroding developer confidence
## 8. Scalability Guardrails (100 → 1000+ Tests)
- The test suite is designed to remain maintainable and trustworthy as it grows from tens to thousands of tests
  - scalability is treated as an architectural requirement, not an afterthought


- Test growth is controlled through enforced execution boundaries
  - every test must belong to a defined category (unit, api, contract, integration, chaos, e2e)
  - execution cost and CI frequency are determined by category, not by individual preference


- The suite prioritizes fast feedback preservation
  - PR pipelines remain lightweight and deterministic
  - higher-cost scenarios are isolated into scheduled or targeted pipelines


- Stability is treated as a non-negotiable constraint for gating layers
  - tests running on every PR must be reproducible and free of timing sensitivity
  - unstable scenarios are explicitly isolated rather than tolerated in critical pipelines


- The strategy prevents uncontrolled accumulation of slow end-to-end coverage
  - full-system tests are intentionally kept low in volume
  - confidence is achieved through layered validation rather than excessive E2E expansion


- Tagging functions as an operational control plane
  - enables selective execution, parallelization, and failure interpretation
  - prevents the suite from degrading into an unstructured monolith


- Suite maintainability is supported through consistent conventions
  - clear ownership expectations for test layers
  - deterministic async assertion patterns
  - avoidance of shared mutable state across tests


- CI design supports scale through execution discipline
  - parallelization is applied where appropriate
  - retries are limited to explicitly unstable categories
  - failures are expected to remain actionable even at high test volume


- The overall objective is to ensure that increasing test count results in increased confidence, not increased noise
  - the suite must scale in size without scaling in brittleness
  - reliability and signal quality remain the defining success criteria

## 9. Trade-offs & Known Limitations
- This project is intentionally scoped as a test architecture showcase, not a production-grade platform
  - design decisions prioritize clarity of testing strategy over exhaustive implementation completeness
  

- The system models realistic distributed-system behavior, but does not attempt to solve every production concern
  - areas such as full observability, security hardening, and operational governance are intentionally out of scope


- Some dependencies are designed to be unreliable for resilience validation
  - downstream instability is introduced deliberately to test recovery behavior
  - this behavior is controlled and isolated to prevent erosion of CI trust


- End-to-end coverage is intentionally constrained
  - the strategy avoids large volumes of expensive E2E tests
  - confidence is achieved through layered validation rather than exhaustive full-system execution


- The repository emphasizes testing at service boundaries rather than internal implementation detail
  - this improves realism and contract stability
  - but may result in less granular verification compared to deeply mocked unit-heavy approaches


- Asynchronous workflows introduce inherent timing variability
  - tests are designed to tolerate eventual consistency within defined bounds
  - however, distributed execution can never be perfectly deterministic across all environments


- CI execution is structured for scalability, but further production-level enhancements are possible
  - examples include advanced orchestration, richer failure analytics, and performance baselining
  - these are considered extensions rather than core goals of this showcase


- These trade-offs are intentional and aligned with the primary objective
  - demonstrating mature testing judgment for distributed, asynchronous systems
  - while maintaining a runnable, scalable, and high-signal test suite