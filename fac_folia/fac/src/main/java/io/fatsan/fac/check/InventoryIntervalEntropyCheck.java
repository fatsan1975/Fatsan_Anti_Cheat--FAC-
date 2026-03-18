package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.InventoryClickEventSignal;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryIntervalEntropyCheck extends AbstractBufferedCheck {
  private static final int WINDOW = 8;
  private final Map<String, Deque<Long>> intervals = new ConcurrentHashMap<>();
  public InventoryIntervalEntropyCheck(int limit) { super(limit); }
  @Override public String name() { return "InventoryIntervalEntropy"; }
  @Override public CheckCategory category() { return CheckCategory.INVENTORY; }

  @Override public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof InventoryClickEventSignal i) || i.intervalNanos() == Long.MAX_VALUE) return CheckResult.clean(name(), category());
    long ms = i.intervalNanos() / 1_000_000L;
    Deque<Long> w = intervals.computeIfAbsent(i.playerId(), k -> new ArrayDeque<>(WINDOW));
    w.addLast(ms);
    if (w.size() > WINDOW) w.removeFirst();
    if (w.size() < WINDOW) return CheckResult.clean(name(), category());
    long unique = w.stream().distinct().count();
    if (unique <= 2 && ms < 120L) {
      int buf = incrementBuffer(i.playerId());
      if (overLimit(buf)) return new CheckResult(true, name(), category(), "Low-entropy inventory click interval window", Math.min(1.0D, buf / 8.0D), false);
    } else coolDown(i.playerId());
    return CheckResult.clean(name(), category());
  }
}
