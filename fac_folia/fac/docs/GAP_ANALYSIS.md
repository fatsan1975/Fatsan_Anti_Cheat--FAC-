# GAP_ANALYSIS

## Scope
Bu rapor FAC_Folia'nın **mevcut çalışan hali** esas alınarak hazırlanmıştır. Amaç yeni proje başlatmak değil; mevcut pipeline'ın üretim öncesi sertleştirme ihtiyaçlarını, false-positive risklerini, performans darboğazlarını ve sürdürülebilir iyileştirme alanlarını haritalamaktır.

## 1. Güçlü taraflar
- Event-driven ve domain-segmented mimari zaten kurulmuş durumda: signal intake -> registry -> evidence -> corroboration -> risk -> action zinciri net.
- Check yüzeyi geniş; movement/combat/world/inventory/protocol/traffic/teleport alanlarında anlamlı kapsama mevcut.
- Corroboration ile punishment ayrıştırılmış; tek sinyalden direkt ceza verme eğilimi çekirdekte sınırlanmış.
- Folia-safe aksiyon yaklaşımı mevcut; scheduler üzerinden setback/kick çalıştırılıyor.
- Config ve compatibility gate sistemi operatöre yüksek kontrol veriyor.
- Next-level katmanı sayesinde health, parse error, label ve false-positive feedback toplanabiliyor.

## 2. Zayıf taraflar
- Check sayısı yüksek olsa da bazı ailelerde sinyal tekrarı var; "aynı davranışın farklı varyant isimleri" kümeleri oluşmuş.
- Risk/trust/pattern modeli basit ve global; server-mode veya protocol-profile bağlamı sınırlı.
- Action güvenliği check bazlı policy matrisiyle kodlanmış değil; bugün karar ağırlıklı olarak `actionable + corroboration + threshold` seviyesinde.
- Telemetry mevcut ama otomatik threshold tuning'e kapalı; feedback toplanıyor fakat karar mekanizmasına doğrudan geri beslenmiyor.
- Compatibility bayrakları isim pattern tabanlı; bu yaklaşım pratik ama zamanla yanlış kapsama/yanlış dışlama üretebilir.
- Performans tarafında tüm event ailesi için sistematik “tiered-cost” gating henüz sınırlı.

## 3. Redundant check kümeleri
- **Cadence / interval / burst** kümeleri: combat, inventory, block-place, block-break ve timer tarafında benzer zaman-serisi sinyalleri çok parçalı.
- **Entropy / variance / plateau / collapse** kümeleri: reach, keepalive ve interval ailelerinde farklı adlarla benzer dağılım anomalileri kontrol ediliyor.
- **Lock / drift / skew / mismatch** kümeleri: rotation, via ve inventory tarafında bazı checkler birbirine yakın kanıt üretiyor.
- Sonuç: Bunların tamamı gereksiz değil; ancak bir kısmı ortak feature extractor veya policy grouping ile daha sürdürülebilir hale getirilebilir.

## 4. Eksik kritik yüzeyler
- Packet-order / transaction-confirm / raw timeline doğrulaması hâlâ sınırlı; bazı protocol checkler event-derived kalıyor.
- Action profile'ların check family bazında koddan okunabilir merkezi matrisi yok.
- Server mode profilleri (survival, practice, minigame, custom-mechanics-safe) henüz operasyonel profile setine dönüşmemiş.
- False-positive tuning için replay / corpus / benchmark bağlantısı dokümante ama otomatikleştirilmiş değil.

## 5. Action güvenliği zayıf checkler
- Via/smoothing/rewrite temalı checkler production'da doğrudan setback/kick için riskli.
- Inventory, keepalive ve bazı traffic anomaly checkleri oyuncu deneyimini bozabilecek kadar çevresel gürültüye açık.
- Attribute/lore/custom item odaklı block-break checkleri custom mechanics sunucularında ceza üretmek için dikkatli kullanılmalı.

## 6. ALERT-only bırakılması gereken checkler
- Via/rewrite/window/smear/skew tabanlı combat/inventory/keepalive/traffic checkleri.
- Packet-order ve timing mismatch ailesi.
- Entropy / variance-collapse / plateau ailesinin büyük bölümü.
- Deep item meta / lore / attribute anomali checkleri.

## 7. Corroboration ile güçlendirilebilecek checkler
- Reach ailesi: tekil signal yerine combat cadence + rotation + protocol beraber bakıldığında daha değerli.
- KeepAlive/Ping ailesi: jitter, drift, modulo, flatline birlikte daha anlamlı.
- Inventory macro ailesi: moving entropy + cadence lock + protocol drift beraber daha güvenli.
- Block-break context ailesi: potion + enchant + attribute + cadence birlikte değerlendirilmeli.

## 8. Evidence kalitesi zayıf olanlar
- “Lock”, “collapse”, “plateau”, “entropy” sınıfındaki tekil checkler çoğu zaman operatöre sınırlı açıklanabilir evidence verir.
- Protocol/via derived checklerin önemli bölümü neden-sonuç yerine “pattern suspicion” üretir; bunlar review/corroboration için iyidir ama tek başına ceza kalitesi düşüktür.

## 9. Performance hotspot'ları
- `MovementEvent` hattı en yoğun maliyet merkezidir; 40 binding ile en kalabalık grup.
- `BukkitSignalBridge.onMove(...)` her hareket eventinde hem movement hem rotation hem de örneklemeli keepalive üretir; doğal hot path budur.
- `CheckRegistry.evaluateAll(...)` içinde her check için `System.nanoTime()` ölçümü yapılması sağlık görünürlüğü sağlar ama yüksek frekansta maliyet yaratır.
- `PacketIntakeService.emit(...)` flood altında güvenlidir; ancak yüksek oyuncu sayısında per-player rate state sayısı izlenmelidir.

## 10. Lightweight optimizasyon adayları
- Suspicion-driven escalation: bazı pahalı/tekrarlı aileleri sadece yükselmiş risk/trust düşüşü altında aktif hale getirmek.
- Family-level toggles: sadece tek tek check değil, check kümesi bazında hızlı kapatma.
- Shared feature extraction: cadence/entropy/variance türevlerinde ortak pencere hesabı.
- Health telemetry sampling: her eventte tam latency ölçümü yerine sampling veya suspicious-only mod.

## 11. Compatibility riski olan alanlar
- Custom item / attribute / lore / enchant temelli world checkleri.
- ViaVersion rewrite/smoothing/check aileleri.
- Custom combat ve minigame sunucularında reach/critical/cadence aileleri.
- Folia tick-skew ve region-io türevleri her topolojide aynı güvenilirlikte olmayabilir.

## 12. Config karmaşıklığı / sadeleştirme adayları
- Çok sayıda `buffer-limit` alanı mevcut; operatör için tek tek ayarlama pahalı.
- `compatibility.*` anahtarları güçlü ama fazla; profile preset'lerle paketlenmeli.
- Action mode, corroboration ve disabled check yönetimi bugün işlevsel ama “policy preset” seviyesine taşınmalı.

## 13. ML/telemetry'nin gerçek katkısı ve zayıflığı
- Gerçek karar katmanı bugün heuristics + corroboration + risk/trust üzerinde.
- `ml.enabled` açık görünse de üretimde model inference yerine telemetri/placeholder rolü baskın.
- Next-level health/label/feedback altyapısı faydalı; ancak tuning loop'a otomatik bağlanmadığı için katkısı yarı-operasyonel seviyede.
- Sonuç: Telemetry korunmalı; ML iddiası ise şimdilik “assistive / future-ready” seviyesinde tutulmalı.

## 14. Bir sonraki iterasyon için öncelik sırası
1. Check family bazlı action safety policy.
2. Tiered-cost execution / suspicion gating.
3. Shared feature extraction ile redundant ailelerin sadeleştirilmesi.
4. Compatibility profiles ve preset config yaklaşımı.
5. Replay/benchmark otomasyonu.
6. Feedback -> threshold tuning bağı.
