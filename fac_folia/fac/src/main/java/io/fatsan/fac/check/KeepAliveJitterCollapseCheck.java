package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.KeepAliveSignal;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class KeepAliveJitterCollapseCheck extends AbstractBufferedCheck {
  private final Map<String, Long> lastPing = new ConcurrentHashMap<>();
  private final Map<String, Integer> stableTicks = new ConcurrentHashMap<>();

  public KeepAliveJitterCollapseCheck(int limit) {
    super(limit);
  }

  @Override
  public String name() {
    return "KeepAliveJitterCollapse";
  }

  @Override
  public CheckCategory category() {
    return CheckCategory.PROTOCOL;
  }

  @Override
  public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof KeepAliveSignal keepAlive)) return CheckResult.clean(name(), category());

    Long prev = lastPing.put(keepAlive.playerId(), keepAlive.pingMillis());
    if (prev == null) return CheckResult.clean(name(), category());

    long diff = Math.abs(keepAlive.pingMillis() - prev);
    int stable = diff <= 1L ? stableTicks.getOrDefault(keepAlive.playerId(), 0) + 1 : 0;
    stableTicks.put(keepAlive.playerId(), stable);

    if (stable >= 10 && keepAlive.pingMillis() >= 35L) {
      int buf = incrementBuffer(keepAlive.playerId());
      if (overLimit(buf)) {
        return new CheckResult(true, name(), category(), "ping jitter collapse/uniformity", Math.min(1.0D, buf / 6.0D), false);
      }
    } else {
      coolDown(keepAlive.playerId());
    }

    return CheckResult.clean(name(), category());
  }
}
