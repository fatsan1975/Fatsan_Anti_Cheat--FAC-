# THREAT_MODEL

## Classification legend
- D: deterministic
- H: strong heuristic
- B: behavioral inference
- I: server-side mostly invisible

## Combat
- KillAura (H/D), AimAssist (H), TriggerBot (H), Reach (H), AutoClicker (H), Velocity/AntiKB (H), AutoTotem (H), FastBow (D/H), inventory combat spoof (H), lag/backtrack abuse (B).

## Movement
- Speed/Fly/Glide/Hover (H/D with context), Blink/Timer (H), NoFall (D/H), Step/Spider/Jesus/NoWeb (H), Elytra/vehicle exploit (H), phase/desync (H/B).

## World/Interaction
- Scaffold/Tower (H), FastPlace/FastBreak (D/H), Nuker/SpeedMine (H), GhostHand (H/B), inventory move/chest stealer (H), slot spoof/creative spoof (D/H).

## Packet/Protocol
- Bad packet families, ordering anomalies, teleport confirm abuse, keepalive/ping spoof, burst/flood, payload abuse, impossible state transitions (D/H).

## Render/Info advantage
- Freecam/Xray/NewChunks/Seed tools/Baritone-like pathing: mostly B/I, never autoban standalone.
