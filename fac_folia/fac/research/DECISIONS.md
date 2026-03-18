# Decisions (Append-only)

## 2026-03-16 - D001
- Title: Folia-first modular anti-cheat skeleton
- Decision: Implement packet-event abstraction + state reconstruction + deterministic/heuristic split + ML risk placeholder.
- Rationale: Maintainable, low overhead, clear extension points, safe threading boundaries.
- Trade-offs: Initial checks are intentionally minimal; future depth added incrementally.
- Follow-up: Expand protocol-specific normalization adapters and replay test corpus.

## 2026-03-16 - D002
- Title: Start with deterministic + buffered heuristic minimal set
- Decision: Implement BadPacketNaN, MovementCadence, CombatRotationSnap as base checks.
- Rationale: Covers protocol sanity + movement/combat baseline with low compute cost.
- Trade-offs: Not yet full premium-equivalent coverage; designed for incremental extension.
- Follow-up: Add packet replay adapters and context-aware activation map.

## 2026-03-16 - D003
- Title: Version pinning and test stability
- Decision: Use Folia 1.21.11 dev bundle and explicit junit-platform-launcher.
- Rationale: Align with target version while keeping CI-like test reliability.
- Trade-offs: Dev bundle setup is heavy on first run (~minutes), but cached afterward.
- Follow-up: Add lightweight fast-test profile for local iteration.

## 2026-03-16 - D004
- Title: Event-driven multi-check expansion
- Decision: Implement combat/movement/world/protocol heuristic checks with normalized signals from Bukkit bridge.
- Rationale: Immediate practical coverage increase without forcing heavy NMS/reflection coupling.
- Trade-offs: Not yet true packet decoder parity; high-end protocol exploits need PacketEvents layer.
- Follow-up: Introduce packet normalization adapters and protocol-version profile map.

## 2026-03-16 - D005
- Title: Risk-gated enforcement with Folia-safe actions
- Decision: Introduce weighted decay risk model and action modes (ALERT/SETBACK/KICK), executing enforcement on player entity scheduler.
- Rationale: Enables practical enforcement without unsafe threading and lowers one-shot false positives.
- Trade-offs: Action pipeline still event-derived, not yet packet-order-aware.
- Follow-up: Add PacketEvents-based transaction/teleport/keepalive ordering checks and per-protocol thresholds.

## 2026-03-16 - D006
- Title: Intake backpressure + protocol-aware metadata
- Decision: Enforce per-player event-rate cap in intake, emit `TrafficSignal`, and evaluate with `PacketBurstCheck`; add optional ViaVersion reflection resolver.
- Rationale: Keeps anti-cheat stable during packet/event spikes and gives protocol profile signal without hard dependency.
- Trade-offs: Resolver is best-effort reflection; strict packet fidelity still pending PacketEvents.
- Follow-up: Replace event-derived traffic with raw packet counters and ordering checks.

## 2026-03-16 - D007
- Title: State-enriched movement heuristics
- Decision: Add MovementEvent vertical/fall context and implement NoFallHeuristic + TeleportOrderHeuristic checks.
- Rationale: Increases practical cheat-family coverage while preserving lightweight constraints.
- Trade-offs: Still heuristic until packet-level confirmation/order adapters are added.
- Follow-up: Introduce deterministic teleport-confirm/transaction-based protocol checks.

## 2026-03-16 - D008
- Title: Add live-ops controls and safe runtime reload
- Decision: Expose `/fac status` and `/fac reload`, use lifecycle-safe stop/start and unregister listeners, and enforce alert threshold filtering.
- Rationale: Improves maintainability and production operability without heavy overhead.
- Trade-offs: Reload resets in-memory evidence/risk state intentionally.
- Follow-up: Add optional persisted debug snapshots for offline triage.

## 2026-03-16 - D009
- Title: Corroboration-gated enforcement
- Decision: Require configurable multi-event + multi-category corroboration before setback/kick.
- Rationale: Aligns with premium low-FP philosophy and evidence-based punishment.
- Trade-offs: Fast autoban aggressiveness reduced intentionally.
- Follow-up: combine with deterministic packet-order checks to regain confidence and speed.

## 2026-03-16 - D010
- Title: Add deterministic-ish fallback signals pre-packet-adapter
- Decision: Introduce KeepAliveConsistency and ImpossibleGroundTransition checks as non-actionable corroboration contributors.
- Rationale: Improves detection confidence fusion while preserving low false positives.
- Trade-offs: Not full deterministic packet semantics yet.
- Follow-up: Replace/augment with PacketEvents order verification checks.

## 2026-03-16 - D011
- Title: Quantized rotation consistency signal for aim-assist corroboration
- Decision: Add `RotationQuantizationCheck` as non-actionable combat signal with configurable buffer.
- Rationale: Strengthens combat signal diversity before packet-level adapters while keeping FP risk low.
- Trade-offs: Heuristic signal can still be noisy in edge cases, so enforcement remains indirect via corroboration.
- Follow-up: Replace/augment with packet-level deterministic rotation/order telemetry.

## 2026-03-16 - D012
- Title: Impossible critical-hit consistency check
- Decision: Add `ImpossibleCriticalCheck` and enrich `CombatHitEvent` with movement-state context.
- Rationale: Raises confidence for combat punishments by validating critical-like hits against impossible states.
- Trade-offs: Still event-derived heuristics; packet-level combat semantics remain future work.
- Follow-up: Fuse with packet-order signals once PacketEvents adapter is introduced.

## 2026-03-16 - D013
- Title: Seed disclosure guard and keepalive sampling optimization
- Decision: Add configurable `/seed` guard listener and throttle keepalive signal emission via sampling interval.
- Rationale: Reduce disclosure surface for world seed and lower runtime overhead in event pipeline.
- Trade-offs: Cannot fully prevent seed inference from game-world observations; this only closes direct command leakage and reduces metadata noise.
- Follow-up: Pair with Paper world-seed obfuscation settings and packet-level telemetry hardening.

## 2026-03-16 - D014
- Title: Conservative movement speed envelope check
- Decision: Add `SpeedEnvelopeCheck` as an actionable movement safeguard using conservative horizontal speed caps.
- Rationale: Improves coverage against blatant speed/strafe bypasses with negligible compute overhead.
- Trade-offs: Event-derived speed can still be noisy; threshold kept conservative and buffered.
- Follow-up: Replace/augment with packet-level movement ordering and prediction reconciliation.

## 2026-03-16 - D015
- Title: Vertical movement envelope safeguard
- Decision: Add `VerticalMotionEnvelopeCheck` for conservative airborne upward-speed anomalies.
- Rationale: Improves fly-like exploit coverage with negligible overhead and buffered low-FP behavior.
- Trade-offs: Event-derived vertical motion can be noisy during edge mechanics; conservative threshold + exemptions applied.
- Follow-up: Add packet-level vertical prediction reconciliation from PacketEvents adapter.

## 2026-03-16 - D016
- Title: Major movement/protocol premium bundle in one iteration
- Decision: Add AirStrafeAcceleration, VerticalDirectionFlip, and PingOscillationSpoof checks together.
- Rationale: Increase practical premium-level coverage quickly while preserving bounded CPU cost.
- Trade-offs: Still event-derived heuristics; packet-order determinism remains next milestone.
- Follow-up: Integrate PacketEvents-based deterministic ordering and replay corpus.

## 2026-03-16 - D017
- Title: Premium bundle II for combat+movement clusters
- Decision: Add MicroTeleport, CriticalCadenceAbuse, and ReachSpikeCluster checks together.
- Rationale: Increase detection depth quickly for high-impact bypass families while preserving bounded runtime.
- Trade-offs: Heuristic thresholds require server-specific tuning; defaults remain conservative.
- Follow-up: Protocol-order deterministic adapters and per-version adaptive thresholds.

## 2026-03-16 - D018
- Title: Premium mega bundle with ten additional active checks
- Decision: Add 10 new checks spanning movement, combat, world, inventory, and protocol in one iteration.
- Rationale: Deliver substantial progress in a single session toward premium anti-cheat target.
- Trade-offs: Increased configuration/tuning surface; mitigated by conservative defaults and buffered escalation.
- Follow-up: PacketEvents deterministic verification and adaptive thresholds per protocol profile.

## 2026-03-16 - D019
- Title: Ultra one-session bundle with 20 additional checks
- Decision: Add and activate 20 extra checks across all domains using conservative buffered heuristics.
- Rationale: Deliver major premium-coverage jump in a single session.
- Trade-offs: Larger tuning surface; mitigated with low-cost bounded state and conservative defaults.
- Follow-up: deterministic packet-order layer to reduce heuristic dependence.


## 2026-03-18 - D020
- Title: Audit-first iteration 2 with low-risk production hardening
- Decision: Do not rewrite FAC_Folia; treat the existing codebase as the source-of-truth system and perform gap-analysis, action-safety, performance and compatibility hardening in delta form.
- Rationale: The project is already operational; large architectural churn would increase risk and reduce confidence.
- Trade-offs: Progress is more incremental than a rewrite, but preserves runtime behavior and operator trust.
- Follow-up: Implement central action profile policies and tiered-cost execution for high-density check families.

## 2026-03-18 - D021
- Title: Make action cooldown acquisition atomic
- Decision: Replace `ActionRateLimiterService`'s `get + put` cooldown acquisition with atomic `ConcurrentHashMap.compute(...)` semantics.
- Rationale: Under concurrent access, cooldown decisions should be deterministic and grant only one permit per window.
- Trade-offs: Very small extra map compute overhead, outweighed by better correctness.
- Follow-up: Consider similar hardening for any future shared action-policy or punishment token buckets.

## 2026-03-18 - D022
- Title: Centralize action safety as a runtime policy layer
- Decision: Introduce `ActionPolicyService` with profile-aware action dispositions and use it in both evaluation gating and punishment decisions.
- Rationale: Action safety must live in code, not only in docs, to prevent noisy families from drifting into unsafe punish paths.
- Trade-offs: Policy uses pragmatic family-name heuristics for now; future iterations may replace parts with explicit family metadata.
- Follow-up: Add family-level metadata or annotations if the name-based resolver becomes too coarse.

## 2026-03-18 - D023
- Title: Use existing risk/trust/pattern state for tiered execution
- Decision: Derive `SuspicionTier` from current risk, trust and recent suspicion intensity before check evaluation.
- Rationale: The repo already had enough player state to support low-risk tiered-cost execution without a major redesign.
- Trade-offs: Early tier thresholds are heuristic and should be benchmarked/tuned further.
- Follow-up: Produce before/after evaluated-check-count benchmarks and tune thresholds per profile.

## 2026-03-18 - D024
- Title: Start redundancy reduction with a fixed-step shared extractor
- Decision: Add `FixedStepWindowTracker` and migrate three cadence/step families first.
- Rationale: Provides a tangible shared-extractor win with low regression risk and clear readability.
- Trade-offs: Only one redundancy cluster is addressed in this iteration; broader extractor work remains.
- Follow-up: Expand shared extractors to variance/entropy/oscillation families.
