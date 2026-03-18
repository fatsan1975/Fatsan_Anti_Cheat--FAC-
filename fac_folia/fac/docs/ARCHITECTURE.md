# ARCHITECTURE

## Runtime graph
1. `FatsanAntiCheatPlugin`
2. `FacConfigLoader`
3. `ActionPolicyService`
4. `CheckRegistry`
5. `PacketIntakeService`
6. `AntiCheatEngine`
7. `EvidenceService` + `CorroborationService` + `RiskService` + `PlayerTrustService` + `SuspicionPatternService`
8. `ActionService`
9. `NextLevelAntiCheatPlatform`
10. `BukkitSignalBridge`

## Iteration 3 delta
- Merkezi action policy katmanı eklendi.
- Evaluation akışı suspicion-driven tier yapısına taşındı.
- Shared extractor yaklaşımı küçük ama gerçek bir kod parçasıyla başlatıldı (`FixedStepWindowTracker`).
- Tuning loop için false-positive attribution görünürlüğü artırıldı.

## Architectural stance
- Çekirdek yeniden yazılmadı.
- Değişiklikler kontrollü delta bazlıdır.
- Amaç daha çok check değil, daha güvenli/ölçülü yürütme davranışıdır.
