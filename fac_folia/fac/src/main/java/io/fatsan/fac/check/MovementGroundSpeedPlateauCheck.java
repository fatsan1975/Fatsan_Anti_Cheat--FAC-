package io.fatsan.fac.check;

import io.fatsan.fac.model.*; import java.util.Map; import java.util.concurrent.ConcurrentHashMap;
public final class MovementGroundSpeedPlateauCheck extends AbstractBufferedCheck {
  private final Map<String,Integer> streak=new ConcurrentHashMap<>(); private final Map<String,Double> last=new ConcurrentHashMap<>();
  public MovementGroundSpeedPlateauCheck(int limit){super(limit);} @Override public String name(){return "MovementGroundSpeedPlateau";} @Override public CheckCategory category(){return CheckCategory.MOVEMENT;}
  @Override public CheckResult evaluate(NormalizedEvent event){ if(!(event instanceof MovementEvent m)||!m.onGround()||m.gliding()||m.inVehicle()) return CheckResult.clean(name(),category()); Double p=last.put(m.playerId(),m.deltaXZ()); if(p==null) return CheckResult.clean(name(),category());
    int s=(Math.abs(p-m.deltaXZ())<0.002D && m.deltaXZ()>0.28D)?streak.getOrDefault(m.playerId(),0)+1:0; streak.put(m.playerId(),s); if(s>=8){int b=incrementBuffer(m.playerId()); if(overLimit(b)) return new CheckResult(true,name(),category(),"ground speed plateau",Math.min(1D,b/7D),false);} else coolDown(m.playerId()); return CheckResult.clean(name(),category());}
}
