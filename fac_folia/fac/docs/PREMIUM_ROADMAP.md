# FAC Premium Yol Haritası (Tamamlandı)

Bu doküman premium seviyeye geçiş için yol haritasını ve bu repo içinde hayata geçirilen çıktıları içerir.

## Faz 1 — Lisanslama ve Feature Gating ✅
- [x] `premium.enabled` + `premium.license-key` konfigürasyonu eklendi.
- [x] Yerel format + checksum doğrulaması yapan `PremiumLicenseService` eklendi.
- [x] Geçersiz lisans durumunda premium özellikler otomatik kapatılıyor.

## Faz 2 — Premium Operasyonel Gözlemlenebilirlik ✅
- [x] `PremiumInsightsService` ile toplam alert, ortalama severity ve en çok tetiklenen check metrikleri eklendi.
- [x] `/fac premium` komutu ile canlı premium durum raporu eklendi.

## Faz 3 — Premium Alarm Entegrasyonları ✅
- [x] `premium.webhook.enabled` + `premium.webhook.url` konfigürasyonu eklendi.
- [x] `AlertWebhookService` ile alertlerin JSON webhook'a async iletilmesi sağlandı.
- [x] HTTP hata durumları server loglarında düşük seviye (FINE) izlenebilir hale getirildi.

## Faz 4 — Yönetim ve İşletim ✅
- [x] Plugin komut yardım metni premium komutunu içerecek şekilde güncellendi.
- [x] Runtime başlangıcında lisans doğrulama özeti loglanıyor.
- [x] Reload akışı premium state'i yeniden kuruyor.

## Kısa Kullanım
```yaml
premium:
  enabled: true
  license-key: "FAC-ABCD-EFGH-IJKL"
  webhook:
    enabled: true
    url: "https://discord.com/api/webhooks/..."
```

> Not: Lisans anahtarı örnek olarak verilmiştir; checksum doğrulamasını geçmeyebilir.
