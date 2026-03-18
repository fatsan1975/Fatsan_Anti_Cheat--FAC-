package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.BlockBreakEventSignal;
import io.fatsan.fac.model.NormalizedEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BreakIntervalVarianceCollapseCheck extends AbstractBufferedCheck {
  private final Map<String, Long> last = new ConcurrentHashMap<>();
  public BreakIntervalVarianceCollapseCheck(int limit) { super(limit); }
  @Override public String name() { return "BreakIntervalVarianceCollapse"; }
  @Override public CheckCategory category() { return CheckCategory.WORLD; }

  @Override public CheckResult evaluate(NormalizedEvent event) {
    if (!(event instanceof BlockBreakEventSignal blockBreak)) return CheckResult.clean(name(), category());
    if (blockBreak.intervalNanos() == Long.MAX_VALUE) return CheckResult.clean(name(), category());
    boolean trigger = false;
    long cur=blockBreak.intervalNanos()/1_000_000L; Long prev=last.put(blockBreak.playerId(),cur); if(prev!=null && Math.abs(prev-cur)<=1L && cur<120L) trigger=true;
    if (trigger) {
      int buf = incrementBuffer(blockBreak.playerId());
      if (overLimit(buf)) return new CheckResult(true, name(), category(), "BreakIntervalVarianceCollapse anomaly pattern", Math.min(1.0D, buf / 8.0D), false);
    } else { coolDown(blockBreak.playerId()); }
    return CheckResult.clean(name(), category());
  }
}
