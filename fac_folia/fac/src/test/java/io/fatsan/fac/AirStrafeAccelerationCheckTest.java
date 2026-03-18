package io.fatsan.fac;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.fatsan.fac.check.AirStrafeAccelerationCheck;
import io.fatsan.fac.model.MovementEvent;
import org.junit.jupiter.api.Test;

final class AirStrafeAccelerationCheckTest {
  @Test
  void flagsHighAirborneAccelerationSpike() {
    AirStrafeAccelerationCheck check = new AirStrafeAccelerationCheck(2);

    check.evaluate(
        new MovementEvent("player", System.nanoTime(), 0.20D, 0.05D, false, 0.0F, false, false, 50_000_000L));
    check.evaluate(
        new MovementEvent("player", System.nanoTime(), 0.95D, 0.04D, false, 0.0F, false, false, 50_000_000L));
    assertTrue(
        check.evaluate(
                new MovementEvent(
                    "player", System.nanoTime(), 1.30D, 0.03D, false, 0.0F, false, false, 50_000_000L))
            .suspicious());
  }

  @Test
  void ignoresGroundedOrExemptMovement() {
    AirStrafeAccelerationCheck check = new AirStrafeAccelerationCheck(2);

    assertFalse(
        check.evaluate(
                new MovementEvent(
                    "player", System.nanoTime(), 1.00D, 0.0D, true, 0.0F, false, false, 50_000_000L))
            .suspicious());
  }
}
