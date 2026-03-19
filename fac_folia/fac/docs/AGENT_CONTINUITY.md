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

---

## ITERATION 4 DELTA

### Tarih
2026-03-19

### Yapılan Değişiklikler

#### WindowStatsTracker (yeni shared extractor)
- `check/support/WindowStatsTracker.java` eklendi.
- Sliding window istatistikleri (mean, variance, stddev, oscillation count, entropy/CV score) tek utility'de toplandı.
- `Stats` record: `isFlat(maxVariance)`, `isOscillating(minOscillations)`, `isUniformlyCadenced(maxCv)`.
- Test: `check/support/WindowStatsTrackerTest.java`.

#### SeverityNormalizer (yeni servis)
- `service/SeverityNormalizer.java` eklendi.
- Family bazlı severity cap: deep-item 0.45x, via-derived 0.55x, statistical 0.65x, protocol-noise 0.70x, core-punish 1.0x.
- `RiskService.apply(...)` artık normalize edilmiş severity kullanıyor.
- Test: `SeverityNormalizerTest.java`.

#### Check migrasyonları (WindowStatsTracker)
- `ReachVarianceCollapseCheck` — duplicate deque + manuel variance kaldırıldı, WindowStatsTracker kullanılıyor.
- `CombatIntervalEntropyCheck` — duplicate deque + distinct count kaldırıldı, WindowStatsTracker kullanılıyor.

#### Eval-count telemetrisi
- `CheckRegistry`: `totalEvaluations`, `totalSkipped` LongAdder + getter metodlar + `registeredCheckCount()`.
- `evaluateAll(...)` her skip/evaluate'i sayıyor.
- `AntiCheatEngine`: proxy getter metodlar eklendi.

#### /fac status genişletmesi
- İkinci satır: `checks=N evaluations=X skipped=Y skip-ratio=Z%`
- Tiered execution verimliliği operatöre anlık görünür.

#### config.yml tamamlandı
- `src/main/resources/config.yml` artık tam ve yorumlanmış default config içeriyor.

### Sonraki Adımlar
1. InventoryIntervalEntropyCheck, BreakIntervalVarianceCollapseCheck, KeepAliveJitterCollapseCheck → WindowStatsTracker migration.
2. skip-ratio karşılaştırmalı benchmark raporu.
3. Feedback summary → severity normalization → tuning döngüsü belgesi.

---

## ITERATION 5 DELTA

### Tarih
2026-03-19

### Bu Iterasyonun Amacı
Iteration 4'te başlatılan WindowStatsTracker shared extractor'ının tüm entropy/variance/plateau
ailelerine genişletilmesi, player quit memory leak'inin tam olarak kapatılması ve
`ActionPolicyService`'e gerçek profil davranışlarının (minigame, survival, via-heavy) eklenmesi.

---

### Yapısal Değişiklikler

#### 1. AbstractWindowCheck (yeni base class)
- **Dosya:** `check/AbstractWindowCheck.java`
- `AbstractBufferedCheck`'i extend eder, `WindowStatsTracker stats` field'ını sağlar
- Constructor: `super(bufferLimit, windowSize)` — her check kendi window boyutunu belirler
- `onPlayerQuit(playerId)` — hem parent buffer'ı hem de window tracker'ı temizler
- Tüm WindowStatsTracker kullanan check'ler artık bu sınıfı extend eder
- **Test:** `check/AbstractWindowCheckTest.java`

#### 2. Check.onPlayerQuit (interface genişletmesi)
- **Dosya:** `check/Check.java`
- `default void onPlayerQuit(String playerId) {}` eklendi
- Tüm check'ler için no-op default, override eden check'ler state'lerini temizler

#### 3. AbstractBufferedCheck.onPlayerQuit
- **Dosya:** `check/AbstractBufferedCheck.java`
- `buffers.remove(playerId)` — buffer map'ten player state'i siler

#### 4. CheckRegistry.clearPlayer
- **Dosya:** `engine/CheckRegistry.java`
- `public void clearPlayer(String playerId)` — tüm kayıtlı check'lerin `onPlayerQuit`'ini çağırır
- Exception handling ile disconnect path korunuyor

#### 5. BukkitSignalBridge.onQuit wire-up
- **Dosya:** `packet/BukkitSignalBridge.java`
- `intake.registry().clearPlayer(playerId)` — disconnect'te tüm check state temizlenir
- Zincirleme: tracker.clear → playerStateService.clear → registry.clearPlayer

---

### WindowStatsTracker Migration — Tamamlanan Check'ler

Iteration 4 + 5 sonrası WindowStatsTracker kullanan toplam 8 check:

| Check | Önceki Yaklaşım | Yeni Yaklaşım | Window |
|---|---|---|---|
| `ReachVarianceCollapseCheck` | Manuel deque + variance hesabı | `AbstractWindowCheck` + `isUniformlyCadenced()` | 8 |
| `CombatIntervalEntropyCheck` | `distinct().count() <= 2` | `AbstractWindowCheck` + CV tabanlı | 8 |
| `InventoryIntervalEntropyCheck` | `distinct().count() <= 2` | `AbstractWindowCheck` + CV tabanlı | 8 |
| `BreakIntervalVarianceCollapseCheck` | Tek-çift `last` map karşılaştırması | `AbstractWindowCheck` + CV tabanlı | 8 |
| `KeepAliveJitterCollapseCheck` | `lastPing` + `stableTicks` iki map | `AbstractWindowCheck` + `isUniformlyCadenced()` | 10 |
| `CombatHitIntervalPlateauCheck` | `last` + `streak` iki map | `AbstractWindowCheck` + `isUniformlyCadenced()` | 8 |
| `MovementGroundSpeedPlateauCheck` | `last` + `streak` iki map | `AbstractWindowCheck` + `isUniformlyCadenced()` | 8 |
| `RotationYawVarianceCollapseCheck` | `last` + `streak` iki map + minified kod | `AbstractWindowCheck` + `isUniformlyCadenced()` | 8 |

**Detection kalitesi artışı:** Önceki `streak` ve `distinct().count()` yaklaşımları
tek outlier değer ekleyerek atlatılabiliyordu. `WindowStatsTracker` CV tabanlı yaklaşım
tüm window'un uniform olmasını gerektirdiğinden bypass çok daha zor.

---

### ActionPolicyService Profil Genişletmesi

**Dosya:** `service/ActionPolicyService.java`

Eklenen profil metodları:
- `isMinigame()` — "minigame" profili
- `isSurvival()` — "survival" profili (gelecek iterasyon için)
- `isViaHeavy()` — "via-heavy" profili

Eklenen yardımcı predicate'ler:
- `isHighTeleportFamily(String)` — teleport-ağırlıklı famileler
- `isBuildWorldFamily(String)` — scaffold/fastbreak/block interaction famileleri

**Profil Davranışı Tablosu (güncel):**

| Profil | Core Punish | Via/Timing | Statistical | Teleport | Build/World | Deep-Item |
|---|---|---|---|---|---|---|
| `default` | CORR_SETBACK/BASELINE | REVIEW_ONLY/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `strict` | CORR_KICK/BASELINE | REVIEW_ONLY/ELEVATED | ALERT/ELEVATED | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `lightweight` | CORR_SETBACK/BASELINE | REVIEW_ONLY/HOT | ALERT/HOT | ALERT/HOT | ALERT/BASELINE | REVIEW/HOT |
| `minigame` | CORR_SETBACK/BASELINE | REVIEW_ONLY/HOT | ALERT/HOT | REVIEW_ONLY/ELEVATED | ALERT/ELEVATED | REVIEW/HOT |
| `via-heavy` | CORR_SETBACK/BASELINE | REVIEW_ONLY/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `custom-mechanics-safe` | CORR_SETBACK/BASELINE | REVIEW_ONLY/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | DISABLED/HOT |

**Test:** `ActionPolicyServiceProfileTest.java` (25 senaryo)

---

### Test Coverage (Iteration 5 sonu)
- Main java: 230 dosya
- Test java: 82 dosya
- Docs/Reports: 34+ belge

---

### Iteration 5 Sonrası Bilinen Eksikler

1. **Per-player service temizleme eksik:** `RiskService`, `PlayerTrustService`,
   `SuspicionPatternService`, `CorroborationService`, `EvidenceService` hâlâ
   player quit'te kendi map'lerini temizlemiyor. Bu Iteration 6 önceliği.

2. **`survival` profil davranışı varsayılandan farklılaşmıyor:** `isSurvival()` predicate
   var ama henüz hiçbir resolve logic'i buna göre dallanmıyor.

3. **Kalan minified check'ler:** `CombatHitDistancePlateauCheck`, bazı Traffic/KeepAlive
   check'leri hâlâ tek satır formatında — okunabilirlik ve maintenance için temizlenmeli.

4. **`AbstractWindowCheck` test coverage eksik:** `BreakIntervalVarianceCollapseCheck`,
   `KeepAliveJitterCollapseCheck` için entegrasyon testleri henüz yok.


---

## ITERATION 6 DELTA

### Tarih
2026-03-19

### Bu Iterasyonun Amacı
Tüm per-player service state'lerinin player disconnect'te temizlenmesi.
Memory leak kapatma işi bu iterasyonla tamamlanmıştır.

---

### Yapısal Değişiklikler

#### clearPlayer zinciri — tamamlandı

Her servise `clearPlayer(String playerId)` eklendi:

| Servis | Temizlenen state |
|---|---|
| `RiskService` | `riskByPlayer` |
| `PlayerTrustService` | `trustByPlayer` |
| `SuspicionPatternService` | `states` |
| `CorroborationService` | `entriesByPlayer` |
| `EvidenceService` | `byPlayer` |

`AntiCheatEngine.clearPlayer(playerId)` tüm servisleri tek çağrıda koordine eder.
`AntiCheatEngine` artık `EvidenceService` constructor parametresi alıyor.

`BukkitSignalBridge.onQuit` tam temizleme zinciri:
1. `tracker.clear(uuid)` — interval tracker
2. `playerStateService.clear(uuid)` — safe location
3. `intake.registry().clearPlayer(playerId)` — check buffer + window tracker (182 check)
4. `engine.clearPlayer(playerId)` — risk + trust + suspicion + corroboration + evidence

`BukkitSignalBridge` artık `AntiCheatEngine` constructor parametresi alıyor.

---

### Mevcut Sistem Durumu (Iteration 6 sonu)

#### Runtime Graph (tam güncel)

```
FatsanAntiCheatPlugin
  └─ FacConfigLoader
  └─ ActionPolicyService (profil: default/strict/lightweight/minigame/survival/via-heavy/custom-mechanics-safe)
  └─ EvidenceService
  └─ CheckRegistry (182 binding, 230 java dosyası)
  └─ PacketIntakeService (rate limiting + traffic coalescing)
  └─ AntiCheatEngine
      ├─ RiskService (decay=0.92, SeverityNormalizer entegrasyonlu)
      ├─ PlayerTrustService (score [0.05-1.0], initial=0.55)
      ├─ SuspicionPatternService (half-life decay, streak boost)
      ├─ CorroborationService (configurable window + category thresholds)
      ├─ EvidenceService (max-window=96, bounded deque)
      ├─ ActionService (ALERT/SETBACK/KICK modes, Folia-safe scheduler)
      └─ NextLevelAntiCheatPlatform (health telemetry, feedback, labels)
  └─ BukkitSignalBridge (Bukkit event → NormalizedEvent)
  └─ WorldSeedGuardListener (/seed bloğu)

Player disconnect temizleme:
  BukkitSignalBridge.onQuit
    → tracker.clear()
    → playerStateService.clear()
    → registry.clearPlayer() → tüm 182 check'in onPlayerQuit()
    → engine.clearPlayer() → 5 servis temizleme
```

#### Check Mimarisi (tam güncel)

```
Check (interface)
  └─ onPlayerQuit(playerId) default metod eklendi

AbstractBufferedCheck (abstract)
  └─ onPlayerQuit → buffers.remove(playerId)

AbstractWindowCheck (abstract, Iteration 5 yeni)
  └─ AbstractBufferedCheck'i extend eder
  └─ WindowStatsTracker stats field'ı sağlar
  └─ onPlayerQuit → super.onPlayerQuit + stats.clear(playerId)
  └─ Kullanan check'ler (8 adet):
     ReachVarianceCollapseCheck, CombatIntervalEntropyCheck,
     InventoryIntervalEntropyCheck, BreakIntervalVarianceCollapseCheck,
     KeepAliveJitterCollapseCheck, CombatHitIntervalPlateauCheck,
     MovementGroundSpeedPlateauCheck, RotationYawVarianceCollapseCheck

WindowStatsTracker (Iteration 4 yeni)
  └─ Sliding window stats: mean, variance, stddev, oscillations, entropyScore
  └─ Stats.isFlat(), isOscillating(), isUniformlyCadenced()

FixedStepWindowTracker (Iteration 3 yeni)
  └─ Fixed-step/cadence-lock penceresi
  └─ Kullanan: CombatIntervalStepCheck, BlockBreakStepPatternCheck, InventoryCadenceLockCheck

SeverityNormalizer (Iteration 4 yeni)
  └─ Family-level severity cap: deep-item 0.45x → core-punish 1.0x
  └─ RiskService.apply() tarafından kullanılıyor
```

#### Aktif Check Dağılımı (tam güncel)

| Event tipi | Binding sayısı |
|---|---|
| MovementEvent | 40 |
| RotationEvent | 20 |
| CombatHitEvent | 28 |
| BlockPlaceEventSignal | 10 |
| BlockBreakEventSignal | 28 |
| InventoryClickEventSignal | 17 |
| KeepAliveSignal | 18 |
| TrafficSignal | 11 |
| TeleportSignal | 10 |
| **Toplam** | **182** |

#### Config Profilleri (tam güncel)

| Profil | Core Punish | Via/Timing | Statistical | Teleport | Build/World | Deep-Item |
|---|---|---|---|---|---|---|
| `default` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `strict` | CORR_KICK/BASELINE | REVIEW/ELEVATED | ALERT/ELEVATED | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `lightweight` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | ALERT/HOT | ALERT/BASELINE | REVIEW/HOT |
| `minigame` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | REVIEW/ELEVATED | ALERT/ELEVATED | REVIEW/HOT |
| `survival` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `via-heavy` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | REVIEW/HOT |
| `custom-mechanics-safe` | CORR_SETBACK/BASELINE | REVIEW/HOT | ALERT/HOT | ALERT/ELEVATED | ALERT/BASELINE | DISABLED/HOT |

#### Test Coverage (Iteration 6 sonu)

| Dosya | Senaryo |
|---|---|
| AbstractBufferedCheckTest | 2 |
| AbstractWindowCheckTest | 4 |
| WindowStatsTrackerTest | 12 |
| SeverityNormalizerTest | 25 |
| ActionPolicyServiceTest | (mevcut) |
| ActionPolicyServiceProfileTest | 25 |
| PlayerQuitCleanupTest | 11 |
| ...diğer mevcut testler... | ~50 |
| **Toplam** | **~85** |

---

### Bilinen Eksikler (Iteration 7 için)

1. `survival` profil `ActionPolicyService`'te farklılaşmıyor — `isSurvival()` predicate var ama resolve logic ona göre dallanmıyor
2. `ProtocolProfileResolver` UUID cache'i clearPlayer'a bağlanmamış
3. `NextLevelAntiCheatPlatform` per-player state (`lastResultByPlayer`, `sessions`, `retention`) clearPlayer'a bağlanmamış
4. `ActionRateLimiterService` clearPlayer desteği yok
5. Bazı check'ler hâlâ minified tek-satır formatında (okunabilirlik eksikliği)

---

## ITERATION 6 TAMAMLAMA DELTA

### Tarih
2026-03-19 (devam)

### Bu Iterasyonda Eklenenler

#### ActionRateLimiterService.clearPlayer
- `lastAlertAt`, `lastSetbackAt`, `lastKickAt` map entry'leri temizleniyor.
- `ActionService.clearPlayer(playerId)` → `actionRateLimiterService.clearPlayer(playerId)` zinciri.

#### NextLevelAntiCheatPlatform.clearPlayer
- `lastEventNanos`, `sessions`, `lastResultByPlayer`, `retention`, `shadowFlagged` temizleniyor.
- Labels ve false-positive feedback kasıtlı olarak korunuyor (kalıcı operatör anotasyonları).
- Check-level lifecycle/confidence/latency verisi korunuyor (check adına keyed, oyuncu adına değil).

#### AntiCheatEngine.clearPlayer — nihai form
```java
public void clearPlayer(String playerId) {
    riskService.clearPlayer(playerId);
    playerTrustService.clearPlayer(playerId);
    suspicionPatternService.clearPlayer(playerId);
    corroborationService.clearPlayer(playerId);
    evidenceService.clearPlayer(playerId);
    actionService.clearPlayer(playerId);       // → ActionRateLimiterService
    nextLevelPlatform.clearPlayer(playerId);   // → telemetri state
}
```

#### ws. vs stats. hatası düzeltildi
`AbstractWindowCheck` kullanan check'lerde `stats.isUniformlyCadenced(...)` → `ws.isUniformlyCadenced(...)`
düzeltmesi uygulandı. `stats` parent field (WindowStatsTracker), `ws` yerel Stats değişkeni —
Stats metodları sadece `ws` üzerinden çağrılmalıydı.

---

### Nihai Sistem Durumu (Iteration 6 tamamlandı)

#### Tam clearPlayer zinciri — disconnect'te temizlenen her şey

```
BukkitSignalBridge.onQuit(event)
  │
  ├─ tracker.clear(uuid)                        # interval tracker (PlayerSignalTracker)
  ├─ playerStateService.clear(uuid)             # safe location map
  │
  ├─ intake.registry().clearPlayer(playerId)    # CheckRegistry
  │    └─ her check için onPlayerQuit(playerId)
  │         ├─ AbstractBufferedCheck: buffers.remove()
  │         └─ AbstractWindowCheck: buffers.remove() + stats.clear()  [8 check]
  │
  └─ engine.clearPlayer(playerId)               # AntiCheatEngine
       ├─ riskService.clearPlayer()             # riskByPlayer
       ├─ playerTrustService.clearPlayer()      # trustByPlayer
       ├─ suspicionPatternService.clearPlayer() # states
       ├─ corroborationService.clearPlayer()    # entriesByPlayer
       ├─ evidenceService.clearPlayer()         # byPlayer (~96 EvidenceRecord)
       ├─ actionService.clearPlayer()           # ActionRateLimiterService 3x map
       └─ nextLevelPlatform.clearPlayer()       # telemetri (5x map + shadowFlagged)
```

**Sonuç:** Disconnect eden bir oyuncu artık hafızada hiçbir izleme verisi bırakmıyor
(label ve false-positive feedback hariç — bunlar kasıtlı olarak korunuyor).

---

### Hâlâ Açık Olan Eksikler (Iteration 7 için)

1. `ProtocolProfileResolver` UUID cache'i clearPlayer'a bağlanmamış (TTL=5s, düşük öncelik)
2. `survival` profil `ActionPolicyService`'te davranışsal farklılık yok
3. Minified tek-satır check'lerin okunabilir formata getirilmesi
4. WindowStatsTracker migration adayları: `AirStrafeAccelerationCheck`, `MovementInertiaBreakCheck`
5. Tiered execution skip-ratio benchmark raporu

---

## ITERATION 7 DELTA

### Tarih
2026-03-19

### Bu Iterasyonun Amacı
Build kırıklığını düzeltmek, tüm Iteration 6'da açık bırakılan eksikleri kapatmak ve detection kalitesini artırmak.

---

### Düzeltmeler

#### Build kırıklığı: WindowStatsTracker.Stats import eksikliği
8 check (`BreakIntervalVarianceCollapseCheck`, `CombatHitIntervalPlateauCheck`, `CombatIntervalEntropyCheck`, `InventoryIntervalEntropyCheck`, `KeepAliveJitterCollapseCheck`, `MovementGroundSpeedPlateauCheck`, `ReachVarianceCollapseCheck`, `RotationYawVarianceCollapseCheck`) `WindowStatsTracker.Stats` tipini import etmeden kullanıyordu. `var` ile düzeltildi — Java 21 type inference ile temiz çözüm.

#### SeverityNormalizer test hataları
- `InventoryPacketBundleDesync`: `isTimingDerived` sadece "bundleorder" arıyordu, "bundle" keyword'ü eklendi.
- `KeepAliveDrift`: `isStatisticalFamily` "drift" içerdiğinden 0.65 döndürüyordu, `isProtocolNoiseFamily` sırası öne alındı → 0.70 ✓.

---

### Yapısal Değişiklikler

#### 1. ProtocolProfileResolver.clearPlayer
- `public void clearPlayer(UUID playerId)` eklendi — cache entry'yi siler.
- `ActionService.clearPlayer(String playerId)` zinciri: `actionRateLimiterService.clearPlayer()` + `protocolProfileResolver.clearPlayer(UUID.fromString(playerId))`.
- clearPlayer zinciri artık tam: disconnect'te protocol profile cache da temizleniyor.

#### 2. ActionPolicyService survival profil farklılaştırması
- `isSurvival() && isBuildWorldFamily(normalized)` koşulu `isRedundantStatFamily` dalına eklendi.
- Survival sunucuda build/world statistical aileler (blockbreak/blockplace/break prefix) `ELEVATED` tier'da değerlendiriliyor (default'ta `HOT`).
- `isBuildWorldFamily` genişletildi: `normalized.startsWith("break")` eklendi (BreakIntervalVarianceCollapse gibi check'ler için).
- Family tag: `"survival-build-stat"`.

#### 3. CombatHitDistancePlateauCheck — tam rewrite
- Minified tek-satır format → okunabilir sınıf.
- `streak` + `last` map pair → `AbstractWindowCheck` + `WindowStatsTracker` (window size 8).
- Eski bypass vektörü: tek outlier değer streak'i sıfırlıyordu. Yeni: tüm window'un `isUniformlyCadenced(0.015)` olması gerekiyor.
- Test: `CombatHitDistancePlateauCheckTest.java`.

#### 4. AirStrafeAccelerationCheck — AbstractWindowCheck migration
- `lastAirSpeedBps` map → `AbstractWindowCheck` (window size 6).
- Memory leak kapatıldı: `onPlayerQuit` artık window tracker'ı da temizliyor.
- Yeni detection: `peek()` ile window mean (established baseline) elde edilip spike buna göre ölçülüyor.
- Landing'de `stats.clear()` çağrısı — airborne phase'ler arası baseline sıfırlanıyor.
- Test: `AirStrafeAccelerationCheckTest.java` güncellendi.

#### 5. MovementInertiaBreakCheck — AbstractWindowCheck migration
- `lastDelta` map → `AbstractWindowCheck` (window size 6).
- Memory leak kapatıldı.
- Yeni detection: `peek()` ile window mean baseline; aynı threshold koşulları korundu.
- Test: `MovementInertiaBreakCheckTest.java` (yeni dosya).

#### 6. EvidenceService snapshot → /fac dq komutuna bağlandı
- `AntiCheatEngine.evidenceSnapshot(String playerId)` getter eklendi.
- `/fac dq <playerId>` — belirli bir oyuncu için son 10 evidence record'u gösteriyor (check adı, severity, reason).
- `/fac dq` (argümansız) — eski global telemetri davranışı korunuyor.

---

### Test Coverage (Iteration 7 sonu)

| Yeni/Güncellenen Test | Senaryo |
|---|---|
| `AirStrafeAccelerationCheckTest` | 4 (güncellendi — window-tabanlı mantığa uyarlandı) |
| `MovementInertiaBreakCheckTest` | 4 (yeni) |
| `CombatHitDistancePlateauCheckTest` | 3 (yeni) |
| `ActionPolicyServiceProfileTest` | +4 survival senaryosu |
| **Toplam** | **188 test, 0 hata** |

---

### Hâlâ Açık Olan Eksikler (Iteration 8 için)

1. Tiered execution skip-ratio karşılaştırmalı benchmark raporu (production traffic verisi gerekiyor)
2. Diğer minified check'ler — `MovementGroundSpeedPlateauCheck`, `RotationYawVarianceCollapseCheck` hâlâ kısmen kısa format (önem düşük, okunabilirlik yeterli)
3. Feedback → severity normalization → tuning döngüsü belgesi
4. Replay harness gerçek test altyapısına taşıma

---

## ITERATION 8 DELTA

### Özet
Premium kalite genişlemesi: 9 yeni check, 4 yeni altyapı servisi, `PlayerStateEvent` desteği.
Toplam test: **222, 0 hata**.

### Yeni Altyapı

- **`model/PlayerStateEvent.java`** — Her `PlayerMoveEvent`'te `MovementEvent` yanında emit edilen yeni sealed event. Alanlar: eating, blocking, inWater, inLava, climbable, gliding, inVehicle, velocityX/Y/Z, intervalNanos. `NormalizedEvent.permits` listesine eklendi.
- **`service/VelocityTracker.java`** — Per-player velocity + pending-knockback tracker. `recordVelocity()`, `expectKnockback()`, `consumeKnockback()` (500ms TTL), `clearPlayer()`. `BukkitSignalBridge.onQuit()`'te temizleniyor.
- **`service/MovementPhysicsValidator.java`** — Vanilla fizik motorunun server-side simülasyonu. Sabitler: GRAVITY=0.08, VERTICAL_DRAG=0.98, AIR_HORIZONTAL_DRAG=0.91, GROUND_FRICTION=0.546. `update()`, `predictMaxHorizontalBps()`, `isImpossibleVertical()`, `clearPlayer()`.

### Yeni Check'ler (9 adet)

| Check | Event | Category | Detection |
|-------|-------|----------|-----------|
| `NoSlowCheck` | PlayerStateEvent | MOVEMENT | Eating/blocking sırasında ortalama hız > 5.5 bps |
| `JesusCheck` | PlayerStateEvent | MOVEMENT | Su/lavada > 4.8 bps + \|deltaY\| < 0.05 |
| `AntiKBCheck` | PlayerStateEvent | COMBAT | KB oranı < 0.25 (VelocityTracker) |
| `VelocityManipulationCheck` | PlayerStateEvent | MOVEMENT | Yatay > 3.0 bpt veya dikey > 2.5 bpt |
| `MovementPhysicsCheck` | MovementEvent | MOVEMENT | Hız oranı > 1.6× fizik maksimumu |
| `PhaseCheck` | MovementEvent | MOVEMENT | onGround=true + deltaY > 0.8 + speed > 2.0 bps |
| `TimerFrequencyCheck` | MovementEvent | PROTOCOL | Ortalama PPS > 29 (1.45× normal) |
| `GlideMimicCheck` | MovementEvent | MOVEMENT | Elytra'da yukarı ortalama > 4.0 bps + speed < 7.0 |
| `ReachRaycastCheck` | CombatHitEvent | COMBAT | Hard cap 4.5b + soft plateau 3.8b CV < 0.04 |

### Değiştirilen Dosyalar

- **`BukkitSignalBridge`**: VelocityTracker constructor param; onMove() PlayerStateEvent emit + recordVelocity(); onHit() expectKnockback(); onQuit() clearPlayer()
- **`CheckRegistry`**: 4-arg + 5-arg standard() overloads; 9 yeni check kaydı; PlayerStateEvent, MovementEvent, CombatHitEvent eşlemeleri
- **`FatsanAntiCheatPlugin`**: VelocityTracker + MovementPhysicsValidator örnekleri oluşturulup CheckRegistry ve BukkitSignalBridge'e geçirildi
- **`ActionPolicyService.isCorePunishCandidate()`**: 8 yeni check eklendi → BASELINE tier, CORROBORATED_SETBACK/KICK disposition
- **`SeverityNormalizer.isCorePunishCandidate()`**: Aynı 8 check → severity cap 1.0

### Hâlâ Açık Olan Eksikler (Iteration 9 için)

1. Tiered execution skip-ratio karşılaştırmalı benchmark raporu
2. PacketEvents optional dependency (`compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")`)
3. Replay harness gerçek test altyapısına taşıma
4. `NoSlowCheck` için potion speed effect tolerans desteği (Speed II ile normal hız 7+ bps olabilir)

---

## ITERATION 9 DELTA

Cheat client analizi (Wurst, Meteor, ClientCommands) sonrası eksik check tespiti ve implementasyonu.

### Değişiklikler
- `InventoryClickEventSignal` — `offhandSwap` boolean alanı eklendi (slot 40 = offhand)
- `BukkitSignalBridge.onInventoryClick()` — `offhandSwap` set edildi

### Yeni Check'ler (5 adet)

| Check | Event | Detection |
|-------|-------|-----------|
| `StepCheck` | MovementEvent | deltaY > 0.625 + onGround + speed > 1.5bps |
| `BoatFlyCheck` | MovementEvent | inVehicle + mean deltaY > 0.3 (window 5) |
| `SpiderCheck` | MovementEvent | airborne + uniform small upward deltaY (0.08–0.25, CV<0.15) |
| `TowerCheck` | BlockPlaceEventSignal | mean interval < 200ms + horizontalSpeed < 0.15 (window 6) |
| `AutoTotemCheck` | InventoryClickEventSignal | offhandSwap + mean interval < 180ms (window 5) |

Tüm yeni check'ler isCorePunishCandidate → severity cap 1.0, BASELINE tier.

**Toplam: 196 check, 233 test, 0 hata.**

### Açık Kalan (Iteration 10)
- MultiTargetAura (aynı tick'te 2+ entity hit)
- AutoCrystal (end kristal place+break döngüsü)
- PacketEvents optional dependency (Velocity paket manipülasyonu)
- Baritone path detection
