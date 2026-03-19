# FAC Iteration 8 Changelog

## Summary
Full premium-quality expansion: 9 new checks, 4 new infrastructure services, PlayerStateEvent support,
VelocityTracker integration, and MovementPhysicsValidator. Total registered checks: 191+.

---

## New Infrastructure

### `model/PlayerStateEvent.java`
New sealed event record emitted alongside every `MovementEvent` during `PlayerMoveEvent`.
Fields: `eating`, `blocking`, `inWater`, `inLava`, `climbable`, `gliding`, `inVehicle`,
`velocityX/Y/Z`, `intervalNanos`. Added to `NormalizedEvent` permits list.

### `service/VelocityTracker.java`
Per-player velocity and pending-knockback tracker.
- `recordVelocity(id, vx, vy, vz)` — stores last observed server-side velocity
- `expectKnockback(id, vy, hz)` — records expected post-hit knockback
- `consumeKnockback(id)` — atomically removes and returns pending knockback (null if expired > 500ms)
- `clearPlayer(id)` — called in `BukkitSignalBridge.onQuit()`

### `service/MovementPhysicsValidator.java`
Simplified Minecraft physics engine for server-side movement prediction.
Constants: GRAVITY=0.08, VERTICAL_DRAG=0.98, AIR_HORIZONTAL_DRAG=0.91, GROUND_FRICTION=0.546.
- `update(id, dxz, dy, onGround, intervalNanos)` — advances physics state
- `predictMaxHorizontalBps(sprinting, speedLevel, slowLevel)` — expected max speed with tolerance
- `isImpossibleVertical(prevState, dy, onGround, gliding)` — vertical physics validation
- `clearPlayer(id)` — called when player enters vehicle/glide or quits

---

## New Checks

| Check | Event | Category | Threshold |
|-------|-------|----------|-----------|
| `NoSlowCheck` | `PlayerStateEvent` | MOVEMENT | Mean speed > 5.5 bps while eating/blocking |
| `JesusCheck` | `PlayerStateEvent` | MOVEMENT | Mean speed > 4.8 bps in water/lava with `\|deltaY\|` < 0.05 |
| `AntiKBCheck` | `PlayerStateEvent` | COMBAT | Observed/expected KB ratio < 0.25 |
| `VelocityManipulationCheck` | `PlayerStateEvent` | MOVEMENT | Horizontal > 3.0 bpt or vertical > 2.5 bpt |
| `MovementPhysicsCheck` | `MovementEvent` | MOVEMENT | Mean speed ratio > 1.6× physics max |
| `PhaseCheck` | `MovementEvent` | MOVEMENT | `onGround=true` + deltaY > 0.8 + speed > 2.0 bps |
| `TimerFrequencyCheck` | `MovementEvent` | PROTOCOL | Mean PPS > 29 (1.45× normal 20 pps) |
| `GlideMimicCheck` | `MovementEvent` | MOVEMENT | Mean upward bps > 4.0 with speed < 7.0 bps while gliding |
| `ReachRaycastCheck` | `CombatHitEvent` | COMBAT | Hard cap 4.5b effective + soft plateau 3.8b with CV < 0.04 |

All new checks inherit from `AbstractWindowCheck` (statistical) or `AbstractBufferedCheck` (threshold),
ensuring automatic per-player state cleanup via `onPlayerQuit()`.

---

## Modified Files

### `packet/BukkitSignalBridge.java`
- Added `VelocityTracker` field + constructor parameter
- `onMove()`: emits `PlayerStateEvent` with full player state (eating, blocking, inWater, inLava,
  climbable via `isClimbing()`, gliding, inVehicle, velocity vector); calls `velocityTracker.recordVelocity()`
- `onHit()`: calls `velocityTracker.expectKnockback()` on the hit target (Player entities only)
- `onQuit()`: calls `velocityTracker.clearPlayer()` before registry/engine cleanup

### `engine/CheckRegistry.java`
- Added `VelocityTracker` + `MovementPhysicsValidator` parameters to `standard()` factory methods
- Old 2-arg and 3-arg overloads still work (create fresh instances internally)
- Registered 9 new checks under appropriate event types

### `bootstrap/FatsanAntiCheatPlugin.java`
- Instantiates `VelocityTracker` and `MovementPhysicsValidator` in `startRuntime()`
- Passes both to `CheckRegistry.standard()` and `VelocityTracker` to `BukkitSignalBridge`

### `service/ActionPolicyService.java`
- Added to `isCorePunishCandidate()`: noslow, jesus, antikb, movementphysics, phase,
  velocitymanipulation, glidemimic, reachraycast → CORROBORATED_SETBACK/KICK, BASELINE tier

### `service/SeverityNormalizer.java`
- Added to `isCorePunishCandidate()`: same 8 checks → severity cap 1.0 (full confidence)

---

## Test Coverage
8 new test files, 222 total tests passing (0 failures):
- `NoSlowCheckTest` (4 tests)
- `JesusCheckTest` (4 tests)
- `PhaseCheckTest` (4 tests)
- `GlideMimicCheckTest` (4 tests)
- `ReachRaycastCheckTest` (4 tests)
- `VelocityManipulationCheckTest` (4 tests)
- `AntiKBCheckTest` (4 tests)
- `TimerFrequencyCheckTest` (3 tests)
- `MovementPhysicsCheckTest` (3 tests)
