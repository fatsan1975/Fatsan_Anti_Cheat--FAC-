# AGENT_CONTINUITY

> Bu dosya FAC_Folia'nın yaşayan kaynak-gerçeklik kaydıdır. Amaç sıfırdan anti-cheat yazmak değil; mevcut çalışan sistemi audit ederek, kontrollü delta-bazlı iterasyonlarla production-grade kaliteye yaklaştırmaktır.

## 1) Proje Kimliği
- Proje: **FAC_Folia**
- Açılım: **Fatsan Anti Cheat**
- Hedef oyun sürümü: **Minecraft Java 1.21.11**
- Platform: **Folia**
- Dil/JDK: **Java 21**
- Ana sınıf: `io.fatsan.fac.bootstrap.FatsanAntiCheatPlugin`
- Komut yüzeyi: `/fac <status|premium|feedback|label|dq|reload>`
- Yetkiler: `fac.admin`, `fac.alerts`

## 2) Çalışan Sistem Gerçekliği
Bu repo artık iskelet/prototype değildir. Mevcut durumda:
- runtime pipeline aktif ve modülerdir
- 182 registry binding / 181 benzersiz check sınıfı vardır
- movement / rotation / combat / block-place / block-break / inventory / keepalive / traffic / teleport alanlarında geniş check yüzeyi vardır
- default action mode `ALERT` durumundadır
- corroboration aktiftir
- world-seed guard aktiftir
- premium varsayılanda pasiftir
- iteration 2 audit teslimatları mevcuttur
- iteration 3 ile birlikte merkezi action policy ve suspicion-driven execution temeli eklenmiştir

## 3) Runtime Graph (Güncel)
1. `FatsanAntiCheatPlugin`
2. `FacConfigLoader`
3. `ActionPolicyService`
4. `CheckRegistry`
5. `PacketIntakeService`
6. `BukkitSignalBridge`
7. `AntiCheatEngine`
8. `EvidenceService`
9. `CorroborationService`
10. `RiskService`
11. `PlayerTrustService`
12. `SuspicionPatternService`
13. `ActionService`
14. `NextLevelAntiCheatPlatform`

## 4) Varsayılan Operasyonel Durum
- `alerts.enabled: true`
- `actions.mode: ALERT`
- `actions.policy-profile: default`
- `corroboration.enabled: true`
- `security.world-seed-guard.enabled: true`
- `premium.enabled: false`
- `checks.disabled: []`
- `compatibility.*: true`

## 5) Aktif Check Dağılımı
- `MovementEvent`: 40
- `RotationEvent`: 20
- `CombatHitEvent`: 28
- `BlockPlaceEventSignal`: 10
- `BlockBreakEventSignal`: 28
- `InventoryClickEventSignal`: 17
- `KeepAliveSignal`: 18
- `TrafficSignal`: 11
- `TeleportSignal`: 10

## 6) Iteration 2'den Devralınan Öncelikler
Iteration 2 sonunda belirlenen sonraki teknik adımlar şunlardı:
1. check family bazlı merkezi action policy
2. suspicion-driven tiered-cost execution
3. shared feature extractor ile redundant aile sadeleştirmesi
4. replay/benchmark altyapısının güçlendirilmesi
5. feedback -> threshold tuning zincirinin kurulması

## 7) Iteration 3'te İncelenen Alanlar
### 7.1 Merkezi action policy temas noktaları
- `ActionService` içinde punish kararı
- `CheckRegistry` içinde evaluation kapsamı
- `AntiCheatEngine` içinde per-player suspicion state
- docs/reports tarafındaki action safety matrix

### 7.2 Tiered execution temas noktaları
- mevcut risk/trust/pattern state'leri zaten mevcuttu
- `RiskService`, `PlayerTrustService`, `SuspicionPatternService` evaluation öncesi suspicion tier üretmek için uygun temel sağlıyordu
- en uygun ilk uygulama noktası `AntiCheatEngine -> CheckRegistry` zinciri olarak seçildi

### 7.3 Shared extractor adayları
- cadence / interval / burst / fixed-step aileleri
- iteration 3 için en düşük riskli başlangıç olarak fixed-step ailesi seçildi

### 7.4 Feedback tuning temas noktaları
- `NextLevelAntiCheatPlatform` zaten label ve false-positive feedback saklıyordu
- ancak feedback belirli bir check ailesine bağlanmıyordu
- tuning loop için bu eksiklik giderildi

## 8) Iteration 3'te Yapılan Kod Değişiklikleri

### 8.1 Merkezi action policy sistemi eklendi
Yeni bileşenler:
- `ActionDisposition`
- `SuspicionTier`
- `ActionPolicyService`

Sağlanan davranışlar:
- check family bazlı merkezi action sınıflandırması
- profile bazlı davranış (`default`, `lightweight`, `strict`, `minigame`, `survival`, `custom-mechanics-safe`, `via-heavy`)
- via/rewrite/window/skew/smear aileleri için daha korumacı politika
- deep item context ailesi için profile-dependent davranış
- core punish adaylarının daha net ayrıştırılması

### 8.2 Action path policy-aware hale getirildi
- `ActionService`, artık sadece `result.actionable()` durumuna bakmıyor
- merkezi policy resolver sonucu (`ActionDisposition`) dikkate alıyor
- alert mesajlarına policy/family bilgisi ekleniyor
- kick yalnız policy izin veriyorsa mümkün oluyor
- setback/kick için corroboration gerekliliği policy ile uyumlu yürütülüyor

### 8.3 Suspicion-driven tiered-cost execution temeli eklendi
- `RiskService.currentRisk(...)` getter eklendi
- `SuspicionPatternService.recentIntensity(...)` getter eklendi
- `AntiCheatEngine`, event öncesi risk + trust + recent intensity üzerinden `SuspicionTier` hesaplıyor
- `CheckRegistry`, `ActionPolicyService.shouldEvaluate(...)` ile bazı aileleri yalnız `ELEVATED` veya `HOT` tier altında değerlendiriyor

### 8.4 Shared extractor gerçek koda girdi
Yeni utility:
- `FixedStepWindowTracker`

Taşınan aileler:
- `CombatIntervalStepCheck`
- `BlockBreakStepPatternCheck`
- `InventoryCadenceLockCheck`

Kazanım:
- önceki değer + tolerans + min/max window + streak mantığı ortaklaştırıldı
- küçük ama gerçek bir redundancy reduction başlangıcı yapıldı

### 8.5 Feedback -> tuning zinciri güçlendirildi
- `NextLevelAntiCheatPlatform.onResult(...)` artık oyuncu başına son tetiklenen check'i izliyor
- `markFalsePositive(...)` false-positive kaydını son check ile ilişkilendiriyor
- `feedbackSummaryByCheck()` eklendi
- `/fac dq` çıktısına check bazlı false-positive özeti dahil edildi

### 8.6 Önceki hardening korundu
- `ActionRateLimiterService` içindeki atomik cooldown acquisition korunuyor
- iteration 2'nin action spam karşıtı sertleştirmesi aktif durumda

## 9) Iteration 3'te Eklenen / Güncellenen Belgeler
### Yeni veya ciddi biçimde genişleyen belgeler
- `docs/SHARED_EXTRACTOR_PLAN.md`
- `docs/TIERED_EXECUTION_DESIGN.md`
- `docs/TUNING_LOOP.md`
- `reports/PROFILE_BEHAVIOR_SUMMARY.md`
- `reports/CHANGELOG_ITERATION_3.md`

### Güncellenen çekirdek iteration belgeleri
- `docs/ACTION_SAFETY_MATRIX.md`
- `docs/PERFORMANCE_AUDIT.md`
- `docs/COMPATIBILITY_PROFILES.md`
- `docs/CHECK_TUNING_PLAN.md`
- `docs/FALSE_POSITIVE_STRATEGY.md`
- `reports/BENCHMARKS.md`
- `reports/FALSE_POSITIVE_REVIEW.md`
- `docs/ARCHITECTURE.md`
- `docs/CHECK_CATALOG.md`
- `docs/CONFIG_REFERENCE.md`
- `docs/SECURITY_REVIEW.md`
- `docs/TEST_PLAN.md`

## 10) Policy / Profile Son Durumu
### default
- güvenli production başlangıcı
- core punish adayları baseline değerlendirilir
- via/deep-item/statistical aileler daha yüksek suspicion tier bekler

### lightweight
- CPU bütçesi öncelikli
- noise-heavy aileler çoğunlukla `HOT` tier'da açılır

### strict
- core punish adayları `CORROBORATED_KICK` seviyesine yükselebilir
- yine de via/rewrite ailesi review-only kalır

### custom-mechanics-safe
- deep item context ailesi disabled-default

### via-heavy
- via/rewrite/window/skew/smear ailesi review-only tutulur

## 11) False Positive Stratejisinin Son Hali
False positive azaltımı artık şu beş katman üzerinden yürür:
1. merkezi action policy
2. suspicion-driven tiered execution
3. corroboration
4. profile-specific compatibility davranışı
5. feedback -> tuning loop

Özellikle temkinli tutulan aileler:
- via/rewrite/window/skew/smear
- keepalive/traffic anomaly
- entropy/collapse/variance/plateau
- deep item meta/lore/attribute/enchant

## 12) Performance Gerçekliği (Güncel)
- ana hot path hâlâ `MovementEvent` tarafıdır
- tiered execution ile amaç baseline evaluated-check sayısını düşürmektir
- shared extractor yaklaşımı küçük ölçekte başlamıştır
- per-check latency ölçümü hâlâ mevcut ve ileride sampling adayıdır

## 13) Test / Doğrulama Durumu
Iteration 3 kapsamında yeni doğrulanan alanlar:
- `ActionPolicyServiceTest`
- `FixedStepWindowTrackerTest`
- `ActionRateLimiterServiceTest`
- `NextLevelAntiCheatPlatformTest` (feedback attribution genişletmesi)

## 14) Bu Iterasyondaki Kararların Etkisi
### Performans etkisi
- düşük şüphede bazı gürültülü aileler hiç çalışmıyor
- cadence-step ailesinde tekrar eden logic kısmen ortaklaştırıldı
- action spam / tekrar aksiyon riski kontrollü kalıyor

### False positive etkisi
- review-only / disabled-default / elevated-tier yaklaşımı bazı riskli aileleri daha güvenli hale getiriyor
- feedback attribution tuning sürecini daha anlamlı yapıyor

### Compatibility etkisi
- via-heavy ve custom-mechanics-safe gibi profiller artık daha teknik olarak gerekçeli
- custom item/meta ağırlıklı sunucularda daha güvenli başlangıç davranışı mümkün

### Rollback gereksinimi
- düşük; değişiklikler delta-bazlıdır ve çekirdeği yeniden yazmaz

## 15) Bir Sonraki Teknik Adım
1. tiered execution için daha somut before/after ölçüm raporları
2. replay harness / deterministik replayer'ın gerçek koda/test altyapısına taşınması
3. family-level severity normalization
4. feedback summary'nin staff operasyon akışına daha görünür bağlanması
5. shared extractor alanının variance/entropy/oscillation ailelerine genişletilmesi

## 16) Özet
FAC_Folia'nın son hali artık:
- daha merkezi policy yöneten,
- daha temkinli punish veren,
- düşük şüphede daha hafif davranan,
- redundancy azaltımına gerçek kodla başlamış,
- feedback'i tuning sürecine daha iyi bağlayan,
- production-grade premium kaliteye bir adım daha yaklaşmış
bir anti-cheat çekirdeğidir.
