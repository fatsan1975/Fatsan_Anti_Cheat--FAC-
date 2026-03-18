# COMPATIBILITY_PROFILES

## Iteration 3 profile intent
Bu profiller artık yalnız dokümantasyon önerisi değil; merkezi action policy açısından da referans davranış setidir.

### default
- Genel production profili.
- Baseline oyuncuda yalnız çekirdek/domain güvenilir aileler çalışır.
- Gürültülü via/protocol/deep-item aileleri daha yüksek suspicion tier bekler.

### lightweight
- CPU bütçesi öncelikli.
- Protocol noise ve istatistiksel family'ler daha çok `HOT` tier'a ötelenir.
- Yüksek oyuncu sayılı, düşük maliyet öncelikli ortamlar için.

### strict
- Practice/PvP odaklı.
- Çekirdek punish adayları `CORROBORATED_KICK` seviyesine yükselebilir.
- Yine de via/rewrite/smear ailesi review-only kalır.

### minigame
- Teleport/state değişimi yüksek ortamlar için.
- Inventory/world cadence aileleri daha temkinli tutulur.

### survival
- Speed, fast-break, scaffold, nofall ve world etkileşim çekirdeği daha anlamlıdır.
- Vanilla'ya yakın item davranışı varsa deep item context review seviyesinde tutulabilir.

### custom-mechanics-safe
- Meta/lore/enchant/attribute/custom item anomali ailesi disabled-default.
- RPG ve heavily-modded davranış taklit eden sunucular için güvenli başlangıç noktası.

### via-heavy
- ViaVersion rewrite/window/skew/smear ailesi review-only.
- Protocol-derived punish davranışı daha yüksek corroboration ister.
