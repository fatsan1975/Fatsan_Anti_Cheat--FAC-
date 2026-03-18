# ACTION_SAFETY_MATRIX

## Iteration 3 central policy summary
FAC_Folia artık action safety'yi yalnız belge olarak değil, merkezi policy resolver üzerinden yorumlanan teknik bir katman olarak ele alır.

## Policy classes
### ALERT_ONLY
- Gürültülü veya düşük kanıt kalitesine sahip ama operatöre görünür kalması gereken sinyaller.
- Örnek aileler: keepalive/traffic anomaly, teleport pattern, bazı cadence/entropy varyantları.

### REVIEW_ONLY
- Staff incelemesine uygun, punish için doğrudan güvenilmeyen sinyaller.
- Örnek aileler: via/rewrite/window/smear/skew, timing/order-derived, deep item context anomaly ailesi.

### CORROBORATED_SETBACK
- Temel movement/combat/world çekirdeğindeki daha güvenilir aileler.
- Örnekler: `SpeedEnvelope`, `VerticalMotionEnvelope`, `NoFallHeuristic`, `ImpossibleGroundTransition`, `ReachHeuristic`, `ImpossibleCritical`, `FastBreak`, `ScaffoldPattern`.

### CORROBORATED_KICK
- Yalnızca daha sert profillerde ve merkezi policy tarafından yükseltilen çekirdek aileler.
- Varsayılan profilde agresif kullanılmaz.

### DISABLED_BY_DEFAULT
- Özellikle `custom-mechanics-safe` gibi profillerde kapatılması gereken derin item/meta/lore/enchant/attribute odaklı aileler.

## Profile davranışı
- `default`: güvenli üretim davranışı, punish tarafı kontrollü.
- `lightweight`: maliyetli/gürültülü aileler daha yüksek suspicion tier altında çalışır.
- `strict`: core punish adayları daha erken ve daha güçlü aksiyon profiline yükselebilir.
- `custom-mechanics-safe`: deep item context ailesi default disabled.
- `via-heavy`: via/rewrite/smear/skew ailesi review-only tutulmalı.

## Kod gerçekliği
- Policy çözümlemesi artık `ActionPolicyService` içinde merkezileştirilmiştir.
- `ActionService` punish kararı vermeden önce resolved policy'yi dikkate alır.
- `CheckRegistry` evaluation aşamasında aynı policy katmanı suspicion tier ile birlikte kullanılır.
