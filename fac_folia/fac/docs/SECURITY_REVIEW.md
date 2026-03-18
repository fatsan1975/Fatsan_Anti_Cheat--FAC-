# SECURITY_REVIEW

## Current hardening status
- Listener unregister disiplini korunuyor.
- `/seed` guard aktif kalabiliyor.
- Action cooldown acquisition atomik.
- Merkezi action policy sayesinde bazı gürültülü ailelerin punish'e gitmesi daha zor.

## Iteration 3 security-relevant gains
- Via/deep-item/statistical aileler daha korumacı action sınıfları aldı.
- Baseline oyuncularda bazı gürültülü ailelerin hiç çalışmaması saldırı yüzeyi değil, yanlış karar yüzeyini azaltan bir sertleştirme olarak değerlendirildi.
- Feedback attribution, yanlış davranış kümelerini tespit etmeyi kolaylaştırır.
