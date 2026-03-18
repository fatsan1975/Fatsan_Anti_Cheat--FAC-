package io.fatsan.fac.check;

import io.fatsan.fac.model.*; import java.util.Map; import java.util.concurrent.ConcurrentHashMap;
public final class CombatHitIntervalPlateauCheck extends AbstractBufferedCheck {
  private final Map<String,Long> last=new ConcurrentHashMap<>(); private final Map<String,Integer> streak=new ConcurrentHashMap<>();
  public CombatHitIntervalPlateauCheck(int limit){super(limit);} @Override public String name(){return "CombatHitIntervalPlateau";} @Override public CheckCategory category(){return CheckCategory.COMBAT;}
  @Override public CheckResult evaluate(NormalizedEvent event){ if(!(event instanceof CombatHitEvent h)||h.intervalNanos()==Long.MAX_VALUE) return CheckResult.clean(name(),category()); long ms=h.intervalNanos()/1_000_000L; Long p=last.put(h.playerId(),ms); if(p==null) return CheckResult.clean(name(),category());
    int s=(Math.abs(ms-p)<=1L&&ms>60L&&ms<100L)?streak.getOrDefault(h.playerId(),0)+1:0; streak.put(h.playerId(),s); if(s>=8){int b=incrementBuffer(h.playerId()); if(overLimit(b)) return new CheckResult(true,name(),category(),"hit interval plateau",Math.min(1D,b/7D),false);} else coolDown(h.playerId()); return CheckResult.clean(name(),category());}
}
