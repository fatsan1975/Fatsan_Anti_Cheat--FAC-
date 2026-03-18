# TUNING_LOOP

## Iteration 3 tuning loop
1. Suspicious sonuç oluşur.
2. `NextLevelAntiCheatPlatform.onResult(...)` son tetiklenen check'i oyuncu için kaydeder.
3. Admin `/fac feedback <playerId> <reason>` ile false-positive işaretlediğinde geri bildirim son check ile ilişkilendirilir.
4. `feedbackSummaryByCheck()` ile hangi check ailesinin daha çok review aldığı görülebilir.
5. Bu veri docs/reports tuning sürecine beslenir.

## Amaç
- Tam otomatik karar sistemi kurmak değil.
- Operatör destekli tuning döngüsünü veriyle beslemek.
