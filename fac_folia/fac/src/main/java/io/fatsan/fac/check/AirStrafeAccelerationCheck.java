package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.MovementEvent;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AirStrafeAccelerationCheck extends AbstractBufferedCheck {
  private final Map<String, Double> lastAirSpeedBps = new ConcurrentHashMap<>();

  public AirStrafeAccelerationCheck(int limit) {
    super(limit);
  }

  @Override
  public String name() {
    return "AirStrafeAcceleration";
  }

  @Override
  public CheckCategory category() {
    return CheckCategory.MOVEMENT;
  }

  @Override
  public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof MovementEvent movement) || movement.intervalNanos() == Long.MAX_VALUE) {
      return CheckResult.clean(name(), category());
    }

    if (movement.onGround() || movement.gliding() || movement.inVehicle()) {
      lastAirSpeedBps.remove(movement.playerId());
      coolDown(movement.playerId());
      return CheckResult.clean(name(), category());
    }

    double seconds = movement.intervalNanos() / 1_000_000_000.0D;
    if (seconds <= 0.0D || seconds > 0.15D) {
      return CheckResult.clean(name(), category());
    }

    double speedBps = movement.deltaXZ() / seconds;
    Double last = lastAirSpeedBps.put(movement.playerId(), speedBps);
    if (last != null && speedBps > 12.0D && (speedBps - last) > 6.0D) {
      int buf = incrementBuffer(movement.playerId());
      if (overLimit(buf)) {
        return new CheckResult(
            true,
            name(),
            category(),
            "Airborne horizontal acceleration exceeded movement envelope",
            Math.min(1.0D, buf / 8.0D),
            true);
      }
    } else {
      coolDown(movement.playerId());
    }

    return CheckResult.clean(name(), category());
  }
}
