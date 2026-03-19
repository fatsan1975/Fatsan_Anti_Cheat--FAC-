# FAC — CLAUDE.md

## Proje
- **FAC_Folia** — Minecraft Java 1.21.11, Folia, Java 21
- Build: `./gradlew test` | `./gradlew build -x test`
- Ana sınıf: `io.fatsan.fac.bootstrap.FatsanAntiCheatPlugin`

## Kritik Dosyalar
- `docs/AGENT_CONTINUITY.md` — tüm geçmiş, mimari kararlar, açık eksikler
- `engine/CheckRegistry.java` — check kaydı (standard() factory)
- `packet/BukkitSignalBridge.java` — Bukkit → NormalizedEvent köprüsü
- `bootstrap/FatsanAntiCheatPlugin.java` — servis wiring, /fac komutu
- `service/ActionPolicyService.java` — tier/disposition kararları
- `service/SeverityNormalizer.java` — family-level severity cap'ler

## Mimari Özeti
- **Event flow:** BukkitSignalBridge → PacketIntakeService → CheckRegistry → AntiCheatEngine
- **Event tipleri:** MovementEvent, PlayerStateEvent, RotationEvent, CombatHitEvent, BlockPlaceEventSignal, BlockBreakEventSignal, InventoryClickEventSignal, KeepAliveSignal, TeleportSignal, TrafficSignal
- **Check base class:** AbstractWindowCheck (WindowStatsTracker tabanlı) veya AbstractBufferedCheck
- **Severity caps:** deep-item 0.45 → via-derived 0.55 → protocol-noise 0.70 → statistical 0.65 → core-punish 1.0
- **Tier gating:** BASELINE / ELEVATED / HOT — düşük suspicion'da pahalı check'ler atlanır
- **clearPlayer zinciri:** BukkitSignalBridge.onQuit → velocityTracker + registry + engine

## Mevcut Durum (Iteration 8)
- 191 check, 222 test, BUILD SUCCESSFUL
- Yeni servisler: VelocityTracker, MovementPhysicsValidator
- Yeni event: PlayerStateEvent (eating, blocking, inWater, inLava, climbable, gliding, inVehicle, velocity)

## Çalışma Kuralları
- İzin sormadan direkt çalış
- Yeni check → CheckRegistry'e kaydet + ActionPolicyService + SeverityNormalizer güncelle
- Her değişiklikte `./gradlew test` çalıştır
- Dokümantasyon: sadece AGENT_CONTINUITY.md güncelle, ayrı CHANGELOG oluşturma
- Background agent kullanma
- Sadece değiştireceğin dosyaları oku
