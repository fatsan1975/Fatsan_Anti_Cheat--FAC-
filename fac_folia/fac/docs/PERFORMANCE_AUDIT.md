# PERFORMANCE_AUDIT

## Iteration 3 summary
Bu iterasyonda performans odağı üç başlıkta ele alındı:
1. Merkezi action policy ile gereksiz punish denemelerini azaltmak.
2. Suspicion-driven tiered-cost evaluation ile düşük şüphede bazı aileleri çalıştırmamak.
3. Shared extractor ile tekrar eden pencere mantıklarını sadeleştirmek.

## Hot path gerçekliği
- En yoğun hat hâlâ `BukkitSignalBridge.onMove(...)` + `CheckRegistry.evaluateAll(...)` zinciridir.
- `MovementEvent` 40 binding ile ana maliyet merkezidir.
- `CombatHitEvent` ve `BlockBreakEventSignal` ikinci yoğun kümeyi oluşturur.

## Bu iterasyonda uygulanan teknik iyileştirmeler
### 1. Tiered evaluation gate
- `AntiCheatEngine`, her event için mevcut risk/trust/recent-intensity durumundan bir `SuspicionTier` türetiyor.
- `CheckRegistry`, `ActionPolicyService` ile birlikte bazı check ailelerini yalnız `ELEVATED` veya `HOT` tier'da değerlendiriyor.
- Bu sayede via/rewrite/window/skew ve bazı istatistiksel aileler baseline oyuncularda sürekli çalıştırılmıyor.

### 2. Shared extractor başlangıcı
- `FixedStepWindowTracker` ile fixed-step/cadence benzeri tekrar eden pencere mantıkları ortaklaştırıldı.
- İlk etapta `CombatIntervalStepCheck`, `BlockBreakStepPatternCheck`, `InventoryCadenceLockCheck` bu extractor'a taşındı.

### 3. Action path hardening korundu
- Iteration 2'de eklenen atomik cooldown acquisition devam ediyor; burst altında tekrar aksiyon maliyetini ve spam etkisini baskılıyor.

## Before/after değerlendirmesi
- Önce: her check family'nin kendi küçük state/streak mantığı daha dağınıktı ve bazı gürültülü aileler baseline'da da koşuyordu.
- Sonra: policy+tier kombinasyonu ile bazı aileler şüphe yükselmeden çalışmıyor; cadence-style mantıklarda ortak tracker kullanılmaya başlandı.

## Sonraki ölçüm hedefi
- Tiered execution açık/kapalı karşılaştırmalı micro-benchmark.
- Movement/rotation/combat event family başına ortalama evaluated-check sayısı.
- Per-check latency ölçümünün sampling stratejisiyle maliyet kıyası.
