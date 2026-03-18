# PERFORMANCE_PLAN

## Cost model
- Stage-1: intake rate guard
- Stage-2: event-routed checks
- Stage-3: risk fusion
- Stage-4: corroboration gate + action

## Lightweight controls
- bounded evidence/risk/corroboration windows
- no global brute-force loop
- per-event O(1) state updates
- non-actionable deterministic-ish protocol checks keep enforcement calm

## Next benchmark goals
- keepalive consistency false-positive rate
- impossible-ground signal frequency per world profile
