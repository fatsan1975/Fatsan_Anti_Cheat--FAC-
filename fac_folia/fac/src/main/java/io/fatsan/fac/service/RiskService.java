package io.fatsan.fac.service;

import io.fatsan.fac.model.CheckResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RiskService {
  private static final double DECAY = 0.92D;
  private final Map<String, Double> riskByPlayer = new ConcurrentHashMap<>();

  public double apply(String playerId, CheckResult result, double trustMultiplier) {
    return riskByPlayer.compute(
        playerId,
        (key, current) -> {
          double base = current == null ? 0.0D : current * DECAY;
          return base + (Math.max(0.2D, result.severity() * weight(result)) * trustMultiplier);
        });
  }

  public void coolDown(String playerId) {
    riskByPlayer.computeIfPresent(playerId, (key, risk) -> Math.max(0.0D, risk * 0.9D - 0.04D));
  }


  public double currentRisk(String playerId) {
    return riskByPlayer.getOrDefault(playerId, 0.0D);
  }
  private static double weight(CheckResult result) {
    return switch (result.category()) {
      case PROTOCOL -> 1.4D;
      case MOVEMENT -> 1.2D;
      case COMBAT -> 1.3D;
      case WORLD -> 1.1D;
      case INVENTORY -> 0.9D;
    };
  }
}
