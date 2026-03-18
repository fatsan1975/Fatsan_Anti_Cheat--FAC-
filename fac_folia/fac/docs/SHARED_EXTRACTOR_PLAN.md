# SHARED_EXTRACTOR_PLAN

## Iteration 3 applied extractor
### `FixedStepWindowTracker`
- Amaç: fixed-step / cadence-lock / near-equal interval pencerelerini tek bir utility altında toplamak.
- İlk entegrasyon:
  - `CombatIntervalStepCheck`
  - `BlockBreakStepPatternCheck`
  - `InventoryCadenceLockCheck`

## Neden seçildi?
- Aynı tür “önceki değer + tolerans + min/max window + streak” mantığı farklı sınıflarda tekrar ediyordu.
- Düşük riskli, ölçülebilir ve debug edilebilir bir sadeleştirme noktası sundu.

## Sonraki extractor adayları
- variance/entropy pencere istatistikleri
- oscillation / flatline tracker
- shared reach window stats
- rotation step-grid/window stats
