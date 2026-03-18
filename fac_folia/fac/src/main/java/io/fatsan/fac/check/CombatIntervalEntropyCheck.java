package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.CombatHitEvent;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatIntervalEntropyCheck extends AbstractBufferedCheck {
  private static final int WINDOW = 8;
  private final Map<String, Deque<Long>> intervals = new ConcurrentHashMap<>();
  public CombatIntervalEntropyCheck(int limit) { super(limit); }
  @Override public String name() { return "CombatIntervalEntropy"; }
  @Override public CheckCategory category() { return CheckCategory.COMBAT; }

  @Override public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof CombatHitEvent hit) || hit.intervalNanos() == Long.MAX_VALUE) return CheckResult.clean(name(), category());
    long ms = hit.intervalNanos() / 1_000_000L;
    Deque<Long> w = intervals.computeIfAbsent(hit.playerId(), k -> new ArrayDeque<>(WINDOW));
    w.addLast(ms);
    if (w.size() > WINDOW) w.removeFirst();
    if (w.size() < WINDOW) return CheckResult.clean(name(), category());
    long unique = w.stream().distinct().count();
    if (unique <= 2 && ms < 90L) {
      int buf = incrementBuffer(hit.playerId());
      if (overLimit(buf)) return new CheckResult(true, name(), category(), "Low-entropy combat interval window", Math.min(1.0D, buf / 8.0D), false);
    } else coolDown(hit.playerId());
    return CheckResult.clean(name(), category());
  }
}
