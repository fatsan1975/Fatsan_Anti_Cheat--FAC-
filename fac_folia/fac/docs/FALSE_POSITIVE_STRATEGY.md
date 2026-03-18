# FALSE_POSITIVE_STRATEGY

## Iteration 3 güncel strateji
False positive düşürme artık beş katmanlı ele alınıyor:
1. merkezi action policy
2. suspicion-driven tiered execution
3. corroboration
4. profile-specific compatibility davranışı
5. feedback -> tuning loop

## Yeni pratik etki
- Gürültülü aileler baseline'da her zaman çalışmıyor.
- Review-only ve disabled-default aileler artık merkezi policy ile belirleniyor.
- Deep item context ve via-derived aileler daha güvenli profile davranışı alıyor.

## Riskli aileler
- via/rewrite/window/skew/smear
- keepalive/traffic anomaly kümeleri
- deep item meta/lore/attribute/enchant
- entropy/collapse/variance/plateau varyantları

## Tuning loop bağlantısı
- False-positive feedback artık `NextLevelAntiCheatPlatform` içinde son şüpheli check ile ilişkilendirilebiliyor.
- Bu veri, hangi ailenin tuning adayı olduğunu daha görünür hale getirir.
