# CHECK_TUNING_PLAN

## Iteration 3 tuning direction
Bu plan artık yalnız sınıflandırma değil; action policy ve tiered execution ile birlikte yürütülecek tuning stratejisidir.

## Family bazlı uygulama yönü
### Core punish candidates
- `SpeedEnvelope`
- `VerticalMotionEnvelope`
- `NoFallHeuristic`
- `ImpossibleGroundTransition`
- `ReachHeuristic`
- `ImpossibleCritical`
- `FastBreak`
- `ScaffoldPattern`

Bu aileler baseline evaluation'da kalır ve corroborated punish için ana çekirdeği oluşturur.

### Statistical families
- cadence / interval / burst
- entropy / variance / collapse / plateau
- oscillation / modulo / lock

Bu aileler iteration 3 itibarıyla merkezi policy altında daha üst suspicion tier'a ötelenir.

### Deep context families
- attribute / lore / enchant / meta / custom-item / unbreakable

Bu aileler profile dependent tutulur; custom-mechanics-safe profilinde disabled-default yaklaşımı benimsenir.

## Shared extractor hedefleri
- fixed-step cadence ailesi → `FixedStepWindowTracker`
- sonraki adaylar: variance/entropy pencereleri, oscillation pencereleri, ping-pattern extractor'ları

## Sonraki tuning adımı
- Family-level severity normalization.
- Feedback yoğunluğu yüksek aileler için default eşik daraltma/genişletme.
