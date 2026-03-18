package io.fatsan.fac.check;

import io.fatsan.fac.model.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatHitDistancePlateauCheck extends AbstractBufferedCheck {
  private final Map<String,Integer> streak=new ConcurrentHashMap<>();
  private final Map<String,Double> last=new ConcurrentHashMap<>();
  public CombatHitDistancePlateauCheck(int limit){super(limit);} 
  @Override public String name(){return "CombatHitDistancePlateau";}
  @Override public CheckCategory category(){return CheckCategory.COMBAT;}
  @Override public CheckResult evaluate(NormalizedEvent event){
    if(!(event instanceof CombatHitEvent h)) return CheckResult.clean(name(),category());
    Double prev=last.put(h.playerId(),h.reachDistance()); if(prev==null) return CheckResult.clean(name(),category());
    boolean same=Math.abs(prev-h.reachDistance())<0.01D && h.reachDistance()>2.9D;
    int s=same?streak.getOrDefault(h.playerId(),0)+1:0; streak.put(h.playerId(),s);
    if(s>=7){int b=incrementBuffer(h.playerId()); if(overLimit(b)) return new CheckResult(true,name(),category(),"combat distance plateau",Math.min(1D,b/7D),false);} else coolDown(h.playerId());
    return CheckResult.clean(name(),category());
  }
}
