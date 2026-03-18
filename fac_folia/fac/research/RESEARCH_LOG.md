# Research Log (Append-only)

## 2026-03-16 - Entry 1 (Aşama 0/1 başlangıç)
- Researched: Existing repository state and baseline template plugin shape.
- Decision: Replace test-template Paper plugin with Folia-first FAC architecture scaffold.
- Why: Current code is a paperweight sample and not production-aligned.
- Risks: Over-scoping; protocol parsing depth initially limited.
- Performance impact: Early design uses layered cheap-first pipeline to control tick cost.
- False positive impact: Buffer + corroboration + evidence-first actions.
- Security impact: Input sanity checks, bounded data structures, log-safe formatting.
- Files touched: project structure bootstrap and docs/research/report paths.
- Next step: Produce threat model and architecture docs before deep coding.

## 2026-03-16 - Entry 2 (Aşama 9 başlangıç)
- Researched: Current architecture docs and decisions before coding core.
- Decision: Build a lightweight modular scaffold with bounded evidence buffers and starter checks.
- Why: Provides maintainable base while preserving low overhead.
- Risks: Initial check sophistication is limited and requires iterative hardening.
- Performance impact: Cheap checks + bounded collections + no per-tick global heavy loops.
- False positive impact: Buffered escalation instead of one-shot punish.
- Security impact: Finite-number guard and bounded memory windows included.
- Files touched: build config, plugin bootstrap, config loader, model/check/engine/packet classes, tests, resources.
- Next step: Validate test/build pipeline and fill remaining operational docs.

## 2026-03-16 - Entry 3 (Aşama 12/13 başlangıç)
- Researched: Build/runtime dependency compatibility and available Folia dev bundle versions.
- Decision: Pin to foliaDevBundle 1.21.11-R0.1-SNAPSHOT and add JUnit launcher runtime.
- Why: Ensures target-version compatibility and stable test execution.
- Risks: Startup check/action path remains conservative until deeper PacketEvents integration.
- Performance impact: No negative runtime cost from test dependency changes.
- False positive impact: None directly; still buffered baseline.
- Security impact: No new attack surface introduced by these changes.
- Files touched: build.gradle.kts, AGENT_CONTINUITY.md.
- Next step: finalize reports and produce release-ready summary.

## 2026-03-16 - Entry 4 (Aşama 10 genişletme)
- Researched: Existing logs/decisions and current check engine limitations.
- Decision: Expand in-game signal intake via Bukkit bridge and add multi-domain heuristic check set.
- Why: Move from pure scaffold to practical in-server detection coverage.
- Risks: Bukkit-event abstraction is not full packet-level parity yet; may miss low-level bypass families.
- Performance impact: Event-driven architecture avoids full-tick polling; each check remains O(1) buffered.
- False positive impact: Buffer + cooldown preserved for all heuristic checks.
- Security impact: Still bounded state and no untrusted deserialization.
- Files touched: model events, packet bridge, tracker service, config, multiple checks, registry, tests, docs.
- Next step: add packet library integration (PacketEvents) and Folia-safe setback executor.

## 2026-03-16 - Entry 5 (Aşama 10/11 derinleştirme)
- Researched: Prior implementation limits around check activation cost, action safety, and enforcement reliability.
- Decision: Add context-aware check routing, risk fusion with decay, actionable confidence flags, and Folia-safe setback/kick path.
- Why: Closer to production anti-cheat behavior with lower false positives and controlled enforcement.
- Risks: Still not full packet-level parity without PacketEvents ordering-level data.
- Performance impact: Better (event-class routing reduces unnecessary check invocations).
- False positive impact: Better (risk thresholds + actionable gating + decay model).
- Security impact: Safer enforcement by entity scheduler usage, bounded state retained.
- Files touched: check API/results, registry, risk/action/state services, bridge, config, docs, tests.
- Next step: packet-level semantic adapters + protocol profile tuning + advanced movement reconstruction.

## 2026-03-16 - Entry 6 (Aşama 10 protocol/perf ilerletme)
- Researched: Remaining gap to packet-level resilience and flood safety requirements.
- Decision: Add intake burst guard, TrafficSignal normalization, PacketBurst check, and protocol profile resolver for ViaVersion-aware tagging.
- Why: Improve DOS/flood resilience and prepare protocol-aware tuning path.
- Risks: Still event-derived; not full packet ordering semantics yet.
- Performance impact: Positive under burst/flood (protective shedding).
- False positive impact: PacketBurst buffered; avoids single-spike punish.
- Security impact: Better resource exhaustion resistance via per-player rate guard.
- Files touched: packet intake, new traffic/protocol models, packet burst check, config, docs, tests.
- Next step: PacketEvents transaction/teleport/keepalive ordering adapters.

## 2026-03-16 - Entry 7 (Aşama 10/11 state+protocol genişletme)
- Researched: Missing high-value movement/protocol checks from initial master scope.
- Decision: Extend movement state features (deltaY/fall/glide/vehicle) and add NoFallHeuristic + TeleportOrderHeuristic checks.
- Why: Cover key bypass families with low-cost stateful heuristics before full packet adapter rollout.
- Risks: Heuristic nature requires careful buffer calibration per server mode.
- Performance impact: Minimal (O(1) map state per player, event-routed checks only).
- False positive impact: Controlled via buffers and non-actionable teleport check.
- Security impact: No new unsafe API usage; bounded state maps continue.
- Files touched: movement model, signal bridge, config, new checks/tests, registry, docs.
- Next step: PacketEvents transaction/confirm ordering for deterministic protocol checks.

## 2026-03-16 - Entry 8 (Aşama 13 operasyonel olgunluk)
- Researched: Remaining operational gaps (runtime manageability, reload safety, alert noise).
- Decision: Implement admin command surface (`/fac status`, `/fac reload`), runtime lifecycle restart safety, and risk-threshold alert gating.
- Why: Production operations need safe live tuning/reload and measurable runtime visibility.
- Risks: Command misuse by unauthorized users mitigated via permission checks.
- Performance impact: Minimal; counters are lightweight LongAdder.
- False positive impact: Better due to alert threshold gating.
- Security impact: Controlled admin surface with explicit permission.
- Files touched: plugin bootstrap, action/engine, build metadata, admin docs, logs.
- Next step: deterministic packet adapter and deeper benchmark automation.

## 2026-03-16 - Entry 9 (Aşama 11 low-FP premium gating)
- Researched: Remaining gap for premium-grade low false positive enforcement.
- Decision: Add corroboration service and enforce multi-signal evidence before punishment actions.
- Why: Single-check spikes are common in noisy environments; multi-signal corroboration is safer.
- Risks: Over-strict corroboration may delay action against blatant cheats.
- Performance impact: Minimal bounded deque operations.
- False positive impact: Significant improvement for punitive actions.
- Security impact: No new external surface; in-memory bounded logic only.
- Files touched: config, action/engine wiring, corroboration service/test, docs/logs.
- Next step: deterministic packet ordering checks to reduce corroboration latency.

## 2026-03-16 - Entry 10 (Aşama 10/11 kapsam derinleştirme)
- Researched: Need for stronger deterministic-like protocol/movement cues before packet adapter completion.
- Decision: Add KeepAliveConsistency and ImpossibleGroundTransition checks.
- Why: Increase coverage and corroboration signal diversity with low CPU overhead.
- Risks: Signals can be noisy in edge cases; kept non-actionable and buffered.
- Performance impact: O(1) per event and event-routed only.
- False positive impact: Controlled by non-actionable status + corroboration gate.
- Security impact: No extra external interfaces, bounded in-memory state.
- Files touched: checks, config, registry, tests, docs/logs.
- Next step: PacketEvents deterministic order layer.

## 2026-03-16 - Entry 11 (Aşama 10 combat sinyal kalitesi)
- Researched: Remaining combat-side gap for stable aim-assist style rotation patterns.
- Decision: Add `RotationQuantizationCheck` with buffered repeated-step detection and keep it non-actionable.
- Why: Increase corroboration quality for premium-style multi-signal decisions without heavy packet parser yet.
- Risks: Some players can produce semi-regular deltas; mitigated by strict window, variance, and non-actionable status.
- Performance impact: Minimal (small bounded deque per active player only on rotation events).
- False positive impact: Controlled by non-actionable contribution and corroboration gate.
- Security impact: No external dependencies; bounded in-memory state.
- Files touched: combat check, registry, config, tests, docs/research logs.
- Next step: PacketEvents integration + replay-driven deterministic validation path.

## 2026-03-16 - Entry 12 (Aşama 10 combat consistency)
- Researched: Need for stronger combat-side deterministic-ish consistency before packet adapter rollout.
- Decision: Add `ImpossibleCriticalCheck` and extend hit signal with on-ground/fall/glide/vehicle context.
- Why: Detect critical-hit spoof patterns with better confidence and earlier escalation.
- Risks: Event-derived edge cases still exist; mitigated with buffered thresholding.
- Performance impact: Low (simple boolean checks on hit events only).
- False positive impact: Controlled via buffer limits and existing risk+corroboration gates.
- Security impact: No external surface change; in-memory signal enrichment only.
- Files touched: hit model, bridge, check, registry, config, tests, docs/logs.
- Next step: PacketEvents deterministic order checks for protocol/combat chain.

## 2026-03-16 - Entry 13 (Aşama 12 hardening/perf)
- Researched: Seed leak vectors and event-pipeline overhead hotspots in Bukkit-derived signal flow.
- Decision: Add world-seed guard listener and keepalive sampling interval control.
- Why: Reduce direct seed disclosure risk and lower per-move pipeline load.
- Risks: Seed inference from terrain/structure observations is still theoretically possible without broader server-side obfuscation strategy.
- Performance impact: Positive; fewer keepalive signals under heavy movement traffic.
- False positive impact: Neutral-to-better due to reduced ping noise density.
- Security impact: Better command-surface hardening (`/seed` blocked by default).
- Files touched: bootstrap, security listener, tracker, signal bridge, config, docs/logs.
- Next step: packet-order deterministic adapter + dedicated replay corpus.

## 2026-03-16 - Entry 14 (Aşama 10 movement premium coverage)
- Researched: Remaining movement-side premium gap for speed/strafe style bypasses.
- Decision: Add `SpeedEnvelopeCheck` with conservative speed cap and buffered escalation.
- Why: Catch obvious movement envelope violations early while keeping Folia runtime overhead minimal.
- Risks: Edge-case lag spikes can distort event-derived speed; mitigated with interval gating and buffering.
- Performance impact: Very low (single division + few condition checks per movement event).
- False positive impact: Controlled by conservative threshold and existing risk/corroboration pipeline.
- Security impact: Better movement exploit coverage without additional attack surface.
- Files touched: check, config, registry, tests, docs/logs.
- Next step: packet-level movement order verification and replay fixtures.

## 2026-03-16 - Entry 15 (Aşama 10 movement vertical coverage)
- Researched: Remaining movement gap around vertical boost/fly style bypasses.
- Decision: Add `VerticalMotionEnvelopeCheck` with conservative airborne upward speed cap.
- Why: Catch obvious vertical anomalies with low overhead before packet-level deterministic layer.
- Risks: Edge-case mechanics may spike vertical deltas; mitigated with strict interval gating, exemptions, and buffering.
- Performance impact: Minimal (simple arithmetic per movement event).
- False positive impact: Controlled with conservative caps + existing risk/corroboration gating.
- Security impact: Better movement exploit family coverage with no new external dependency.
- Files touched: check, config, registry, tests, docs/logs.
- Next step: PacketEvents movement order and teleport-confirm correlation checks.

## 2026-03-16 - Entry 16 (Aşama 14 büyük paket)
- Researched: User feedback emphasized larger single-iteration progress toward premium anti-cheat scope.
- Decision: Ship a bundled movement+protocol hardening set (3 new checks) in one pass.
- Why: Reduce iteration overhead/time and expand cheat-family coverage faster.
- Risks: Heuristic stacking can increase tuning complexity; mitigated via per-check buffers and actionable flags.
- Performance impact: Low (constant-time map lookups and arithmetic only).
- False positive impact: Controlled with conservative thresholds + risk/corroboration pipeline.
- Security impact: Better resilience against strafe/flight/ping-manipulation bypass families.
- Files touched: checks, config, registry, tests, docs/research/test logs.
- Next step: deterministic packet-order layer and protocol-version adaptive thresholds.

## 2026-03-16 - Entry 17 (Aşama 15 büyük paket II)
- Researched: Remaining premium gap after first major bundle, especially clustered reach/critical abuse and displacement spikes.
- Decision: Ship second bundled set (MicroTeleport, CriticalCadenceAbuse, ReachSpikeCluster).
- Why: Accelerate practical premium coverage without waiting for packet adapter milestone.
- Risks: More heuristics increase calibration workload; managed with buffers + actionable/non-actionable split.
- Performance impact: Low, bounded per-player maps and simple arithmetic/deque operations.
- False positive impact: Controlled by conservative windows and existing risk+corroboration pipeline.
- Security impact: Better resilience to combined combat/movement exploit patterns.
- Files touched: checks, config, registry, tests, docs/research/test logs.
- Next step: deterministic packet-order adapter and replay-driven validation.

## 2026-03-16 - Entry 18 (Aşama 16 mega paket)
- Researched: Remaining broad coverage gaps and user request for larger one-shot progress.
- Decision: Implement and activate 10 additional checks across all major categories.
- Why: Maximize immediate practical coverage and reduce iteration latency.
- Risks: More heuristics can increase calibration complexity in production.
- Performance impact: Still bounded and lightweight (O(1) maps/deques and arithmetic).
- False positive impact: Controlled by buffer windows + existing risk/corroboration gating.
- Security impact: Better resilience against mixed exploit families (movement/combat/protocol/world/inventory).
- Files touched: checks, config, registry, tests, docs/research/test logs.
- Next step: deterministic packet-order and prediction layer integration.

## 2026-03-16 - Entry 19 (Aşama 17 ultra paket)
- Researched: User requested much larger one-shot delivery toward premium anti-cheat goal.
- Decision: Implement 20 additional active checks spanning movement/combat/protocol/world/inventory.
- Why: Maximize immediate practical coverage and reduce iteration overhead.
- Risks: Higher calibration complexity and interaction effects between heuristics.
- Performance impact: Still bounded O(1) per event with lightweight maps/deques.
- False positive impact: Controlled via buffered escalation + risk/corroboration gating.
- Security impact: Significantly wider exploit-family response surface.
- Files touched: checks, registry, tests, docs/research/test logs.
- Next step: PacketEvents deterministic verification and per-version adaptive profiles.


## 2026-03-17 - Entry 20 (Aşama 18 premium hardening + threat research)
- Researched: Public anti-cheat behavior references (Grim/Matrix/Vulcan topluluk kaynakları) ve cheat client modülleri (Wurst/Meteor/LiquidBounce) üzerinden bypass pattern kümeleri.
- Decision: Tek oturumda premium seviyeyi güçlendirmek için 8 yeni check eklendi ve runtime registry’ye bağlandı: `MovementInertiaBreak`, `AirVerticalStall`, `CriticalSyncWindow`, `ReachOscillationEntropy`, `KeepAliveJitterCollapse`, `TrafficBurstJitter`, `InventoryCadenceLock`, `BlockPlaceSprintCadence`.
- Why: Timer/air-stall/reach-oscillation/ping-manipulation/scaffold-cadence gibi modern client davranışlarına daha iyi karşılık vermek.
- Risks: Daha fazla heuristic -> tuning ihtiyacı artar; mitigasyon için buffer, cooldown, actionable ayrımı korunuyor.
- Performance impact: O(1) map/arithmetik tabanlı; global pahalı işlem yok.
- False positive impact: Tek event değil streak/entropy/cadence pencereleri ile tetiklenir.
- Security impact: Paket burst+jitter, fake-latency uniformity ve scaffold cadence lock gibi adaletsiz yardım modüllerine karşı daha güçlü koruma.
- Files touched: yeni check sınıfları, registry, testler, check catalog ve research sources/log.
- Next step: packet-order deterministic replay corpus + protocol-version adaptive thresholds.

## 2026-03-17 - Entry 21 (Aşama 19 deep integration + folia/via optimization)
- Researched: ViaVersion protocol dağılımı, PacketEvents entegrasyonunda trafik coalescing pratikleri, command-block/attribute tabanlı tool hız manipülasyonu saldırı yüzeyi.
- Decision: 20 yeni check eklendi, runtime’a bağlandı; BlockBreak sinyali attribute/potion/enchant context ile genişletildi; `PacketIntakeService` coalesced traffic emit + stale state cleanup ile optimize edildi; UUID tabanlı playerId kullanımı ile isim-tabanlı map çakışmaları azaltıldı.
- Why: Gerçek sunucuda tool/potion/attribute kaynaklı yasal hız değişimlerini ayırt edip false positive’i düşürmek ve bypass yüzeyini daraltmak.
- Risks: Check sayısındaki artış tuning ve profiling ihtiyacı doğurur.
- Performance impact: O(1) state erişimi korunur; trafik sinyali 250ms coalescing ile ek yük azaltıldı; stale state TTL temizliği eklendi.
- False positive impact: Block-break context awareness ve disabled-check config ile operasyonel kontrol güçlendi.
- Security impact: Cheat client cadence/oscillation/lock patternlerine karşı kapsam genişledi.
- Files touched: model/packet/config/registry/check/test/docs/research.
- Next step: PacketEvents ordering adapter + replay corpus + profile-specific threshold matrix.

## 2026-03-17 - Entry 22 (Aşama 20 premium expansion II)
- Decision: 20 yeni check daha eklendi (movement/rotation/combat/world/inventory/protocol).
- Decision: Compatibility bayrakları registry seviyesinde aktif kullanılmaya başlandı; check family bazlı registration gating eklendi.
- Decision: Folia odaklı optimizasyon olarak check registration short-circuit + disabled-check + compatibility filter birleşik akışa taşındı.
- Why: Daha yüksek bypass kapsaması, operatöre daha fazla kontrol, sürüm/ekosistem farklılıklarında güvenli kapatma.
- Performance impact: registration-time filtering ile runtime check listesi daralabilir; event başı yük düşer.
- Next step: PacketEvents raw packet adapter + transaction/confirm timeline checks.

## 2026-03-17 - Entry 23 (Aşama 21 deep server-context + folia/via hardening)
- Decision: BlockBreak signali item-level context ile derinleştirildi (`itemTypeKey`, `itemAttackSpeedBonus`, `itemMovementSpeedBonus`, `enchantWeight`, `customItemContext`).
- Decision: ActionService oyuncu çözümlemesi UUID fallback ile düzeltildi (isim yerine UUID sinyal akışıyla uyum).
- Decision: ProtocolProfileResolver için Via/PacketEvents varlık tespiti + TTL cache eklendi.
- Decision: 20 yeni check eklendi; özellikle command-block / attribute-modified item kaynaklı hız manipülasyonu için world/protocol tarafı güçlendirildi.
- Decision: Compatibility alanı 10 ek bayrakla genişletildi ve registry gating daha ayrıntılı hale getirildi.
- Performance impact: profile çözümleme cache + runtime registration gating + context-aware check koşulları ile gereksiz değerlendirmeler azaltıldı.
- Next step: PacketEvents raw packet timeline checks (confirm transaction, teleport confirm correlation) ve replay corpus.

## 2026-03-18 - Entry 24 (Iteration 2 audit + production hardening)
- Researched: AGENT_CONTINUITY, architecture/runtime graph, registry density, action safety path, performance hotspots, compatibility gates, next-level telemetry role.
- Found issues: Redundant check families (cadence/entropy/skew/lock variants), missing central action safety matrix, high movement hot-path density, telemetry useful but not yet feeding automatic tuning, action rate limiter had theoretical concurrent permit race due to `get + put` pattern.
- Proposed change: Start with audit/docs-first iteration; add low-risk production hardening before broader behavioral refactors.
- Applied change: `ActionRateLimiterService` switched to atomic `compute(...)` permit acquisition and concurrency regression test added.
- Performance impact: Negligible runtime change; avoids duplicate alert/action acquisition under contention.
- False positive impact: Indirectly positive by reducing repeated action spam during edge contention; no direct threshold increase.
- Compatibility impact: Safe and behavior-preserving for single-threaded usage; stronger determinism under concurrent calls.
- Rollback needed: Unlikely.
- Next step: Centralize action profiles and explore suspicion-driven tiered-cost execution for high-density check families.

## 2026-03-18 - Entry 25 (Iteration 3 central policy + tiered execution)
- Researched: Iteration 2 documents, `ActionService`, `AntiCheatEngine`, `CheckRegistry`, cadence-style check families, next-level feedback path.
- Found issues: Action suitability was documented but not centralized in code; noisy families were still structurally eligible for baseline evaluation; cadence/fixed-step logic had duplicated state handling; false-positive feedback lacked per-check attribution.
- Selected solution: Add a central policy resolver, derive a suspicion tier from existing risk/trust/pattern state, gate evaluation through policy+tier, introduce a low-risk shared extractor for fixed-step families, and connect false-positive feedback to the last suspicious check.
- Applied change: `ActionPolicyService`, `ActionDisposition`, `SuspicionTier`, registry/engine/action-service integration, `FixedStepWindowTracker`, feedback summary by check, new tests, and iteration-3 docs/reports.
- Performance impact: Baseline players no longer need to evaluate some via/deep-item/statistical families; small state-sharing win in fixed-step families.
- False positive impact: Better, because review-only / disabled-default / elevated-tier behavior is now enforceable in code, not only in docs.
- Compatibility impact: Better profile safety for `custom-mechanics-safe` and `via-heavy` style deployments.
- Rollback needed: Low; changes are additive and do not rewrite the runtime pipeline.
- Next step: Add measurable before/after evaluated-check-count reporting and a real replay harness.
