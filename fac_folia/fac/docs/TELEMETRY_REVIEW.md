# TELEMETRY_REVIEW

## Korunmalı
- Check health (latency/failure) görünürlüğü.
- Data quality (parse/out-of-order/missing field) sayaçları.
- Ground-truth label ve false-positive feedback depoları.

## Güçlendirilmeli
- Feedback -> threshold tuning akışı.
- Hangi checklerin en çok label/feedback ürettiği görünürlüğü.
- Family-level health raporları.

## Sadeleştirilmeli / dikkatli kullanılmalı
- Sürekli ayrıntılı latency ölçümü maliyetlidir; sampling düşünülebilir.
- ML alanı bugün placeholder/telemetry-weighted rol taşıyor; ürün mesajı buna göre gerçekçi tutulmalı.

## Sonuç
Mevcut next-level katman kaldırılmamalı; ancak “karar veren ML” gibi konumlandırılmak yerine “operasyonel gözlem + tuning support” olarak tanımlanması daha doğrudur.
