# NEXT LEVEL IMPLEMENTATION (Faz A-E, 50 başlık)

Bu doküman 50 maddelik roadmap'in plugin içinde kod karşılıklarını listeler.

## Faz A — Data & Telemetry (1-10)
1. Event schema versioning -> `NormalizedEvent.schemaVersion()`
2. Tick-time sync -> `NormalizedEvent.serverTick()/regionTick()` + `NextLevelAntiCheatPlatform.tickSync`
3. Session context -> `NextLevelAntiCheatPlatform.sessionContext`
4. Packet/order trace -> `NextLevelAntiCheatPlatform.traceIdForAction`
5. Ground-truth labels -> `pushGroundTruthLabel` + `/fac label`
6. False-positive feedback -> `markFalsePositive` + `/fac feedback`
7. Retention tiers -> `retentionTier` + internal retention queue
8. Sampling strategy -> `shouldSample`
9. Privacy guardrails -> `privacySafePlayerId`
10. Data quality dashboard -> `dataQuality` + `/fac dq`

## Faz B — Check Engine 2.0 (11-20)
11. Lifecycle standard -> `CheckLifecycleState` + `setLifecycle`
12. Confidence -> `setCheckConfidence`
13. Window abstraction -> `WindowType` + `window`
14. Dependency graph -> `linkChecks`
15. Policy DSL -> `registerPolicyDsl`
16. Version-aware thresholds -> `setThresholdProfile`
17. Adaptive cooldown -> `adaptiveCooldownMillis`
18. World/gamemode profiles -> `profileKey`
19. Self-health hooks -> `selfHealth`
20. A/B deployment -> `setAbDeployment`

## Faz C — Movement Physics Reconstruction (21-30)
21-30 maddeleri `deterministicMovementDelta` ... `setbackSafety` metodlarıyla eklendi.

## Faz D — Combat & Aim Intelligence (31-40)
31-40 maddeleri `normalizeReach` ... `replayVerdict` metodlarıyla eklendi.

## Faz E — Risk/ML/Ops (41-50)
41-50 maddeleri `riskFusionBayesian` ... `releaseGate` metodlarıyla eklendi.

## Runtime Integration
- `AntiCheatEngine` her event ve check sonucu için next-level platformu besler.
- `CheckRegistry` check latency/failure bilgisini `CheckExecutionObserver` ile next-level health katmanına akar.
- `/fac` komutuna `feedback`, `label`, `dq` alt komutları eklendi.
