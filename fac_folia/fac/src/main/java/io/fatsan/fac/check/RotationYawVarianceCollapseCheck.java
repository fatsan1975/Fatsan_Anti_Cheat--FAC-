package io.fatsan.fac.check;

import io.fatsan.fac.model.*; import java.util.Map; import java.util.concurrent.ConcurrentHashMap;
public final class RotationYawVarianceCollapseCheck extends AbstractBufferedCheck {
  private final Map<String,Float> last=new ConcurrentHashMap<>(); private final Map<String,Integer> streak=new ConcurrentHashMap<>();
  public RotationYawVarianceCollapseCheck(int limit){super(limit);} @Override public String name(){return "RotationYawVarianceCollapse";} @Override public CheckCategory category(){return CheckCategory.COMBAT;}
  @Override public CheckResult evaluate(NormalizedEvent event){ if(!(event instanceof RotationEvent r)) return CheckResult.clean(name(),category()); Float p=last.put(r.playerId(),Math.abs(r.deltaYaw())); if(p==null) return CheckResult.clean(name(),category());
    int s=(Math.abs(Math.abs(r.deltaYaw())-p)<0.015F && p>6.0F)?streak.getOrDefault(r.playerId(),0)+1:0; streak.put(r.playerId(),s); if(s>=7){int b=incrementBuffer(r.playerId()); if(overLimit(b)) return new CheckResult(true,name(),category(),"yaw variance collapse",Math.min(1D,b/7D),false);} else coolDown(r.playerId()); return CheckResult.clean(name(),category());}
}
