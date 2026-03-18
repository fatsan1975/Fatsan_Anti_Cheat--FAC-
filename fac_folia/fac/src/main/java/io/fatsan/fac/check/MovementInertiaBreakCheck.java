package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.MovementEvent;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MovementInertiaBreakCheck extends AbstractBufferedCheck {
  private final Map<String, Double> lastDelta = new ConcurrentHashMap<>();

  public MovementInertiaBreakCheck(int limit) {
    super(limit);
  }

  @Override
  public String name() {
    return "MovementInertiaBreak";
  }

  @Override
  public CheckCategory category() {
    return CheckCategory.MOVEMENT;
  }

  @Override
  public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof MovementEvent movement)) return CheckResult.clean(name(), category());
    if (movement.gliding() || movement.inVehicle() || movement.intervalNanos() == Long.MAX_VALUE) {
      return CheckResult.clean(name(), category());
    }

    Double prev = lastDelta.put(movement.playerId(), movement.deltaXZ());
    if (prev == null) return CheckResult.clean(name(), category());

    double jerk = movement.deltaXZ() - prev;
    boolean suspicious = !movement.onGround() && prev > 0.18D && movement.deltaXZ() < 0.02D && jerk < -0.16D;

    if (suspicious) {
      int buf = incrementBuffer(movement.playerId());
      if (overLimit(buf)) {
        return new CheckResult(true, name(), category(), "abrupt airborne inertia break", Math.min(1.0D, buf / 7.0D), false);
      }
    } else {
      coolDown(movement.playerId());
    }
    return CheckResult.clean(name(), category());
  }
}
