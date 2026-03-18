# TIERED_EXECUTION_DESIGN

## Tasarım amacı
Tüm checklerin her eventte tam güçte çalışmasını önlemek; risk/trust/suspicion durumuna göre evaluation yoğunluğunu katmanlandırmak.

## Tier'lar
- `BASELINE`
- `ELEVATED`
- `HOT`

## Girdi sinyalleri
- `RiskService.currentRisk(...)`
- `PlayerTrustService.trustScore(...)`
- `SuspicionPatternService.recentIntensity(...)`

## Uygulama akışı
1. `AntiCheatEngine` event öncesi mevcut oyuncu state'inden tier hesaplar.
2. `CheckRegistry`, `ActionPolicyService.shouldEvaluate(...)` ile check bazlı minimum tier kontrolü yapar.
3. Baseline oyuncular çekirdek güvenilir ailelerle değerlendirilir.
4. Şüphe yükseldikçe gürültülü ve daha pahalı aileler açılır.

## Hedeflenen kazanç
- düşük şüphede daha hafif hot path
- via/protocol noise ailelerinde daha düşük FP
- profile bazlı daha kontrollü davranış
