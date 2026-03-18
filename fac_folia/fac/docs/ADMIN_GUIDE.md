# ADMIN_GUIDE

## Komutlar
- `/fac status` : runtime profil, action modu, işlenen event ve şüpheli sonuç sayısı.
- `/fac reload` : config + runtime pipeline hot-reload.

Gerekli izin: `fac.admin`.

## Önerilen rollout
1. Önce `actions.mode: ALERT`.
2. `/fac status` ile event/suspicious oranlarını takip et.
3. Kalibrasyon sonrası `SETBACK`, son aşamada `KICK`.
