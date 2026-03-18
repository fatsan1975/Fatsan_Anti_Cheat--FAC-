package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.CombatHitEvent;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ReachVarianceCollapseCheck extends AbstractBufferedCheck {
  private static final int WINDOW = 6;
  private final Map<String, Deque<Double>> reachWindow = new ConcurrentHashMap<>();

  public ReachVarianceCollapseCheck(int limit) {
    super(limit);
  }

  @Override
  public String name() {
    return "ReachVarianceCollapse";
  }

  @Override
  public CheckCategory category() {
    return CheckCategory.COMBAT;
  }

  @Override
  public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof CombatHitEvent hit)) {
      return CheckResult.clean(name(), category());
    }

    Deque<Double> window = reachWindow.computeIfAbsent(hit.playerId(), ignored -> new ArrayDeque<>(WINDOW));
    window.addLast(hit.reachDistance());
    if (window.size() > WINDOW) {
      window.removeFirst();
    }
    if (window.size() < WINDOW) {
      return CheckResult.clean(name(), category());
    }

    double mean = window.stream().mapToDouble(Double::doubleValue).average().orElse(0.0D);
    double variance = window.stream().mapToDouble(v -> {
      double d = v - mean;
      return d * d;
    }).sum() / window.size();

    if (mean > 3.1D && variance < 0.003D) {
      int buf = incrementBuffer(hit.playerId());
      if (overLimit(buf)) {
        return new CheckResult(true, name(), category(), "Reach variance collapsed around high mean window", Math.min(1.0D, buf / 8.0D), false);
      }
    } else {
      coolDown(hit.playerId());
    }

    return CheckResult.clean(name(), category());
  }
}
