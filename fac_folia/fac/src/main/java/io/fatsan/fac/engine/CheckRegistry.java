package io.fatsan.fac.engine;

import io.fatsan.fac.check.AirHoverStreakCheck;
import io.fatsan.fac.check.AirTimeAccelerationCheck;
import io.fatsan.fac.check.LowGravityPatternCheck;
import io.fatsan.fac.check.ZeroDeltaSpamCheck;
import io.fatsan.fac.check.YawAccelerationPatternCheck;
import io.fatsan.fac.check.TeleportBurstFollowCheck;
import io.fatsan.fac.check.TrafficFloodRampCheck;
import io.fatsan.fac.check.TrafficDropBurstCheck;
import io.fatsan.fac.check.TeleportChainCheck;
import io.fatsan.fac.check.ScaffoldSprintPlaceCheck;
import io.fatsan.fac.check.BlockPlaceSprintCadenceCheck;
import io.fatsan.fac.check.ReachTrendEscalationCheck;
import io.fatsan.fac.check.ReachHighLowAlternationCheck;
import io.fatsan.fac.check.PitchSnapCheck;
import io.fatsan.fac.check.PitchDriftSuppressionCheck;
import io.fatsan.fac.check.PingStepPatternCheck;
import io.fatsan.fac.check.PlaceIntervalBurstCheck;
import io.fatsan.fac.check.KeepAliveFlatlineCheck;
import io.fatsan.fac.check.KeepAlivePlateauDropCheck;
import io.fatsan.fac.check.KeepAliveSawtoothCheck;
import io.fatsan.fac.check.InventoryIntervalEntropyCheck;
import io.fatsan.fac.check.InventoryCadenceLockCheck;
import io.fatsan.fac.check.InventoryIdleMacroPatternCheck;
import io.fatsan.fac.check.InventoryIdleSpamCheck;
import io.fatsan.fac.check.InventoryMovingEntropyCheck;
import io.fatsan.fac.check.HitReachSwitchPatternCheck;
import io.fatsan.fac.check.HorizontalJerkCheck;
import io.fatsan.fac.check.FallDistanceResetAnomalyCheck;
import io.fatsan.fac.check.BreakIntervalVarianceCollapseCheck;
import io.fatsan.fac.check.GroundAccelBurstCheck;
import io.fatsan.fac.check.GroundFrictionBypassCheck;
import io.fatsan.fac.check.LandingBouncePatternCheck;
import io.fatsan.fac.check.MicroStepTimerCheck;
import io.fatsan.fac.check.CombatIntervalEntropyCheck;
import io.fatsan.fac.check.CombatIntervalStepCheck;
import io.fatsan.fac.check.CriticalReachCouplingCheck;
import io.fatsan.fac.check.BlockBreakStepPatternCheck;
import io.fatsan.fac.check.AimConsistencyWindowCheck;
import io.fatsan.fac.check.AirStrafeAccelerationCheck;
import io.fatsan.fac.check.AutoClickerCadenceCheck;
import io.fatsan.fac.check.BadPacketNaNCheck;
import io.fatsan.fac.check.BlockPlaceIntervalConsistencyCheck;
import io.fatsan.fac.check.Check;
import io.fatsan.fac.check.CombatRotationSnapCheck;
import io.fatsan.fac.check.CriticalCadenceAbuseCheck;
import io.fatsan.fac.check.CriticalSyncWindowCheck;
import io.fatsan.fac.check.FastBreakCadenceClusterCheck;
import io.fatsan.fac.check.FastBreakCheck;
import io.fatsan.fac.check.GroundSpoofPatternCheck;
import io.fatsan.fac.check.HitIntervalBurstCheck;
import io.fatsan.fac.check.ImpossibleCriticalCheck;
import io.fatsan.fac.check.ImpossibleGroundTransitionCheck;
import io.fatsan.fac.check.InventoryBurstWhileMovingCheck;
import io.fatsan.fac.check.InventoryMoveCheck;
import io.fatsan.fac.check.KeepAliveConsistencyCheck;
import io.fatsan.fac.check.KeepAliveDriftCheck;
import io.fatsan.fac.check.KeepAliveJitterCollapseCheck;
import io.fatsan.fac.check.MicroTeleportCheck;
import io.fatsan.fac.check.MovementCadenceCheck;
import io.fatsan.fac.check.NoFallHeuristicCheck;
import io.fatsan.fac.check.PacketBurstCheck;
import io.fatsan.fac.check.PingOscillationSpoofCheck;
import io.fatsan.fac.check.PingSpoofHeuristicCheck;
import io.fatsan.fac.check.PitchLockCheck;
import io.fatsan.fac.check.ReachHeuristicCheck;
import io.fatsan.fac.check.ReachSpikeClusterCheck;
import io.fatsan.fac.check.ReachVarianceCollapseCheck;
import io.fatsan.fac.check.ReachOscillationEntropyCheck;
import io.fatsan.fac.check.RotationJitterPatternCheck;
import io.fatsan.fac.check.RotationQuantizationCheck;
import io.fatsan.fac.check.ScaffoldPatternCheck;
import io.fatsan.fac.check.SpeedEnvelopeCheck;
import io.fatsan.fac.check.TrafficBurstJitterCheck;
import io.fatsan.fac.check.TeleportOrderHeuristicCheck;
import io.fatsan.fac.check.MovementInertiaBreakCheck;
import io.fatsan.fac.check.AirVerticalStallCheck;
import io.fatsan.fac.check.TimerCadenceCheck;
import io.fatsan.fac.check.RotationDirectionLockCheck;
import io.fatsan.fac.check.RotationModuloPatternCheck;
import io.fatsan.fac.check.StrafeAngleDriftCheck;
import io.fatsan.fac.check.VerticalOscillationCheck;
import io.fatsan.fac.check.VerticalDirectionFlipCheck;
import io.fatsan.fac.check.VerticalMotionEnvelopeCheck;
import io.fatsan.fac.config.FacConfig;
import io.fatsan.fac.model.BlockBreakEventSignal;
import io.fatsan.fac.model.BlockPlaceEventSignal;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.CombatHitEvent;
import io.fatsan.fac.model.InventoryClickEventSignal;
import io.fatsan.fac.model.KeepAliveSignal;
import io.fatsan.fac.model.MovementEvent;
import io.fatsan.fac.model.NormalizedEvent;
import io.fatsan.fac.model.RotationEvent;
import io.fatsan.fac.model.TeleportSignal;
import io.fatsan.fac.model.TrafficSignal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.fatsan.fac.nextlevel.CheckExecutionObserver;
import io.fatsan.fac.service.ActionPolicyService;
import io.fatsan.fac.service.SuspicionTier;

public final class CheckRegistry {
  private static final Logger LOGGER = Logger.getLogger(CheckRegistry.class.getName());
  private final Map<Class<? extends NormalizedEvent>, List<Check>> checksByEvent;
  private final EvidenceService evidenceService;
  private final CheckExecutionObserver checkExecutionObserver;

  private CheckRegistry(
      Map<Class<? extends NormalizedEvent>, List<Check>> checksByEvent,
      EvidenceService evidenceService,
      CheckExecutionObserver checkExecutionObserver) {
    this.checksByEvent = Map.copyOf(checksByEvent);
    this.evidenceService = evidenceService;
    this.checkExecutionObserver = checkExecutionObserver;
  }

  public static CheckRegistry standard(FacConfig config, EvidenceService evidenceService) {
    return standard(config, evidenceService, CheckExecutionObserver.NOOP);
  }

  public static CheckRegistry standard(
      FacConfig config, EvidenceService evidenceService, CheckExecutionObserver checkExecutionObserver) {
    Map<Class<? extends NormalizedEvent>, List<Check>> checks = new HashMap<>();
    java.util.Set<String> disabledChecks = config.disabledChecks();

    register(checks, config, disabledChecks, MovementEvent.class, new BadPacketNaNCheck());
    register(checks, config, disabledChecks, MovementEvent.class, new MovementCadenceCheck(config.movementCadenceBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new SpeedEnvelopeCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new VerticalMotionEnvelopeCheck(config.verticalMotionEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new AirStrafeAccelerationCheck(config.airStrafeAccelerationBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new VerticalDirectionFlipCheck(config.verticalDirectionFlipBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new MicroTeleportCheck(config.microTeleportBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new GroundSpoofPatternCheck(config.groundSpoofPatternBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new AirHoverStreakCheck(config.airHoverStreakBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new AirVerticalStallCheck(config.verticalDirectionFlipBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new HorizontalJerkCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new LowGravityPatternCheck(config.noFallBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new FallDistanceResetAnomalyCheck(config.noFallBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new GroundAccelBurstCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new MovementInertiaBreakCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new GroundFrictionBypassCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new ZeroDeltaSpamCheck(config.movementCadenceBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new VerticalOscillationCheck(config.verticalDirectionFlipBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new AirTimeAccelerationCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new LandingBouncePatternCheck(config.verticalDirectionFlipBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new StrafeAngleDriftCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new MicroStepTimerCheck(config.timerBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new TimerCadenceCheck(config.timerBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new NoFallHeuristicCheck(config.noFallBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new ImpossibleGroundTransitionCheck(config.impossibleGroundBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new TeleportOrderHeuristicCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new CombatRotationSnapCheck(config.combatSnapBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new RotationQuantizationCheck(config.rotationQuantizationBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new RotationJitterPatternCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new PitchLockCheck(config.pitchLockBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new YawAccelerationPatternCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new PitchSnapCheck(config.pitchLockBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new AimConsistencyWindowCheck(config.rotationQuantizationBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new RotationModuloPatternCheck(config.rotationQuantizationBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new RotationDirectionLockCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new PitchDriftSuppressionCheck(config.rotationJitterPatternBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new ImpossibleCriticalCheck(config.impossibleCriticalBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new CriticalCadenceAbuseCheck(config.criticalCadenceAbuseBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new CriticalSyncWindowCheck(config.criticalCadenceAbuseBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new HitIntervalBurstCheck(config.hitIntervalBurstBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachHeuristicCheck(config.reachBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachSpikeClusterCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachVarianceCollapseCheck(config.reachVarianceCollapseBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new AutoClickerCadenceCheck(config.autoClickerBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new HitReachSwitchPatternCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new CombatIntervalEntropyCheck(config.autoClickerBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachTrendEscalationCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachOscillationEntropyCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new CombatIntervalStepCheck(config.autoClickerBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new CriticalReachCouplingCheck(config.reachBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new ReachHighLowAlternationCheck(config.reachSpikeClusterBufferLimit()));

    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new ScaffoldPatternCheck(config.scaffoldBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new BlockPlaceIntervalConsistencyCheck(config.blockPlaceIntervalConsistencyBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new ScaffoldSprintPlaceCheck(config.scaffoldBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new BlockPlaceSprintCadenceCheck(config.scaffoldBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new PlaceIntervalBurstCheck(config.blockPlaceIntervalConsistencyBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new FastBreakCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new FastBreakCadenceClusterCheck(config.fastBreakCadenceClusterBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new BlockBreakStepPatternCheck(config.fastBreakCadenceClusterBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new BreakIntervalVarianceCollapseCheck(config.fastBreakCadenceClusterBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryMoveCheck(config.inventoryMoveBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryBurstWhileMovingCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryIdleSpamCheck(config.inventoryMoveBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryIntervalEntropyCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryCadenceLockCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryMovingEntropyCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new InventoryIdleMacroPatternCheck(config.inventoryMoveBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new PingSpoofHeuristicCheck(config.pingSpoofBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new PingOscillationSpoofCheck(config.pingOscillationSpoofBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAliveConsistencyCheck(config.keepAliveConsistencyBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAliveDriftCheck(config.keepAliveDriftBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAliveJitterCollapseCheck(config.keepAliveConsistencyBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAliveFlatlineCheck(config.keepAliveConsistencyBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new PingStepPatternCheck(config.pingOscillationSpoofBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAliveSawtoothCheck(config.keepAliveDriftBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new KeepAlivePlateauDropCheck(config.keepAliveConsistencyBufferLimit()));

    register(checks, config, disabledChecks, TrafficSignal.class, new PacketBurstCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new TrafficFloodRampCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new TrafficBurstJitterCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new TrafficDropBurstCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TeleportSignal.class, new TeleportOrderHeuristicCheck(config.teleportOrderBufferLimit()));
    register(checks, config, disabledChecks, TeleportSignal.class, new TeleportChainCheck(config.teleportOrderBufferLimit()));
    register(checks, config, disabledChecks, TeleportSignal.class, new TeleportBurstFollowCheck(config.teleportOrderBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementDragConsistencyCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementGroundTransitionBurstCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementLowIntervalHighDistanceCheck(config.timerBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationYawStepGridCheck(config.rotationQuantizationBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationPitchStepGridCheck(config.rotationQuantizationBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationYawPitchRatioLockCheck(config.rotationJitterPatternBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatReachDecayBypassCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatHitDistancePlateauCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatCriticalGroundMismatchCheck(config.impossibleCriticalBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatIntervalVarianceLockCheck(config.autoClickerBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakToolBoostAbuseCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakPotionMismatchCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakPerfectCadenceLockCheck(config.fastBreakCadenceClusterBufferLimit()));

    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new io.fatsan.fac.check.BlockPlaceStrafeLockCheck(config.scaffoldBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new io.fatsan.fac.check.BlockPlaceSpeedStepCheck(config.blockPlaceIntervalConsistencyBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryBurstCadenceLockCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryMoveStopClickDesyncCheck(config.inventoryMoveBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveModuloPatternCheck(config.keepAliveConsistencyBufferLimit()));

    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportIntervalLockCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficCeilingOscillationCheck(config.packetBurstBufferLimit()));

    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementAirTurnStabilityCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementVerticalMicroBounceCheck(config.verticalDirectionFlipBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementGroundSpeedPlateauCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementFallResetCadenceCheck(config.noFallBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationZeroPitchBurstCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationYawVarianceCollapseCheck(config.rotationQuantizationBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatReachSpikeRecoveryCheck(config.reachSpikeClusterBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatCriticalSpamWindowCheck(config.criticalCadenceAbuseBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatHitIntervalPlateauCheck(config.autoClickerBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakAttributeOutlierCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakEfficiencySpoofCheck(config.fastBreakBufferLimit()));

    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new io.fatsan.fac.check.BlockPlaceCadenceEntropyCheck(config.blockPlaceIntervalConsistencyBufferLimit()));
    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new io.fatsan.fac.check.BlockPlaceSprintStopDesyncCheck(config.scaffoldBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryMovingFixedStepCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryIdleIntervalLockCheck(config.inventoryMoveBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveStepCollapseCheck(config.keepAliveConsistencyBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveHighLowFlipCheck(config.keepAliveDriftBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveRampAnomalyCheck(config.keepAliveDriftBufferLimit()));

    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportClusterCadenceCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakItemAttributeMismatchCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakCustomContextBurstCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakEnchantmentWeightCheck(config.fastBreakCadenceClusterBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakHasteAmplifierMismatchCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakFatigueBypassConsistencyCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakItemTypeCadenceCheck(config.fastBreakCadenceClusterBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakAttributeDriftCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakMovementAttributeAbuseCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakAttackAttributeAbuseCheck(config.fastBreakBufferLimit()));

    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementAttributeCouplingCheck(config.speedEnvelopeBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementProtocolBurstMismatchCheck(config.timerBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationAttributeSyncCheck(config.rotationJitterPatternBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatProtocolTimingMismatchCheck(config.autoClickerBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatReachContextLockCheck(config.reachSpikeClusterBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryProtocolDriftCheck(config.inventoryMoveBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryMoveAttributeMismatchCheck(config.inventoryBurstWhileMovingBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveAttributeSpoofPatternCheck(config.keepAliveConsistencyBufferLimit()));

    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportProfileMismatchCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficPacketEventsSkewCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficLatencySurfaceCheck(config.packetBurstBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakCommandAttributeBoostCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakSpeedAttributeSpikeCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakCustomItemContextLockCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakHasteNoEnchantCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakEnchantBurstPatternCheck(config.fastBreakCadenceClusterBufferLimit()));

    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementProtocolLatencyCouplingCheck(config.timerBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementFoliaTickSkewCheck(config.movementCadenceBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatViaVersionReachDesyncCheck(config.reachBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatPacketEventOrderSkewCheck(config.autoClickerBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryViaTransactionSkewCheck(config.inventoryMoveBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryPacketOrderBurstCheck(config.inventoryBurstWhileMovingBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveViaJitterBiasCheck(config.keepAliveDriftBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAlivePacketOrderMismatchCheck(config.keepAliveConsistencyBufferLimit()));

    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportViaConfirmSkewCheck(config.teleportOrderBufferLimit()));
    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportFoliaRegionTransitionBurstCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficViaBandwidthAnomalyCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficProtocolDecodeSkewCheck(config.packetBurstBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationViaSensitivityDriftCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationPacketPhaseLockCheck(config.rotationQuantizationBufferLimit()));

    register(checks, config, disabledChecks, BlockPlaceEventSignal.class, new io.fatsan.fac.check.BlockPlaceProtocolSprintSkewCheck(config.blockPlaceIntervalConsistencyBufferLimit()));

    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakCommandLoreAnomalyCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakUnbreakableSpeedCouplingCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakMetaSignatureDriftCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakAttributeSignatureLockCheck(config.fastBreakBufferLimit()));
    register(checks, config, disabledChecks, BlockBreakEventSignal.class, new io.fatsan.fac.check.BlockBreakDeepContextAmplifierCheck(config.fastBreakCadenceClusterBufferLimit()));

    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementFoliaRegionIoFuseCheck(config.movementCadenceBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementEntitySnapshotSkewCheck(config.movementCadenceBufferLimit()));
    register(checks, config, disabledChecks, MovementEvent.class, new io.fatsan.fac.check.MovementViaSmoothingMismatchCheck(config.timerBufferLimit()));

    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatViaWindowSmearCheck(config.reachBufferLimit()));
    register(checks, config, disabledChecks, CombatHitEvent.class, new io.fatsan.fac.check.CombatPacketBundleOrderCheck(config.autoClickerBufferLimit()));

    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryPacketBundleDesyncCheck(config.inventoryBurstWhileMovingBufferLimit()));
    register(checks, config, disabledChecks, InventoryClickEventSignal.class, new io.fatsan.fac.check.InventoryViaRewriteSkewCheck(config.inventoryMoveBufferLimit()));

    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAliveViaRewriteOscillationCheck(config.keepAliveDriftBufferLimit()));
    register(checks, config, disabledChecks, KeepAliveSignal.class, new io.fatsan.fac.check.KeepAlivePacketFastReflectionSkewCheck(config.keepAliveConsistencyBufferLimit()));

    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportBundleConfirmDriftCheck(config.teleportOrderBufferLimit()));
    register(checks, config, disabledChecks, TeleportSignal.class, new io.fatsan.fac.check.TeleportRegionIoFusionCheck(config.teleportOrderBufferLimit()));

    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficPacketBundlePressureCheck(config.packetBurstBufferLimit()));
    register(checks, config, disabledChecks, TrafficSignal.class, new io.fatsan.fac.check.TrafficViaWindowAnomalyCheck(config.packetBurstBufferLimit()));

    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationDeepMetaCouplingCheck(config.rotationJitterPatternBufferLimit()));
    register(checks, config, disabledChecks, RotationEvent.class, new io.fatsan.fac.check.RotationViaWindowSnapCheck(config.rotationQuantizationBufferLimit()));

    return new CheckRegistry(checks, evidenceService, checkExecutionObserver);
  }

  public List<CheckResult> evaluateAll(
      NormalizedEvent event, SuspicionTier tier, ActionPolicyService actionPolicyService) {
    List<Check> checks = checksByEvent.get(event.getClass());
    if (checks == null || checks.isEmpty()) {
      return List.of();
    }

    List<CheckResult> suspicious = new ArrayList<>(2);
    for (Check check : checks) {
      if (!actionPolicyService.shouldEvaluate(check.name(), check.category(), tier)) {
        continue;
      }
      long startNanos = System.nanoTime();
      boolean failed = false;
      try {
        CheckResult result = check.evaluate(event);
        if (result.suspicious()) {
          suspicious.add(result);
          evidenceService.append(event.playerId(), result);
        }
      } catch (Throwable throwable) {
        failed = true;
        LOGGER.log(
            Level.WARNING,
            "FAC check execution failure: " + check.name() + " event=" + event.getClass().getSimpleName(),
            throwable);
      } finally {
        checkExecutionObserver.onCheckExecution(check.name(), event, System.nanoTime() - startNanos, failed);
      }
    }
    return suspicious;
  }

  private static void register(
      Map<Class<? extends NormalizedEvent>, List<Check>> byEvent,
      FacConfig config,
      java.util.Set<String> disabledChecks,
      Class<? extends NormalizedEvent> eventClass,
      Check check) {
    String checkName = check.name().toLowerCase(java.util.Locale.ROOT);
    if (disabledChecks != null && disabledChecks.contains(checkName)) {
      return;
    }
    if (!isCompatibilityEnabled(config, checkName)) {
      return;
    }
    byEvent.computeIfAbsent(eventClass, key -> new ArrayList<>()).add(check);
  }

  private static boolean isCompatibilityEnabled(FacConfig config, String checkName) {
    if (!config.compatibilityPacketEventsOptimized()
        && (checkName.contains("traffic") || checkName.contains("packetburst"))) return false;
    if (!config.compatibilityViaAware()
        && (checkName.contains("keepalive") || checkName.contains("ping") || checkName.contains("teleport"))) return false;
    if (!config.compatibilityProtocolAdaptive()
        && (checkName.contains("modulo") || checkName.contains("intervallock") || checkName.contains("oscillation"))) return false;
    if (!config.compatibilityLegacyCombat()
        && (checkName.contains("critical") || checkName.contains("reach") || checkName.contains("combat"))) return false;
    if (!config.compatibilityLegacyMovement()
        && (checkName.contains("ground") || checkName.contains("fall") || checkName.contains("movement"))) return false;
    if (!config.compatibilityModernOffhand() && checkName.contains("inventory")) return false;
    if (!config.compatibilityModernInventory()
        && (checkName.contains("inventory") || checkName.contains("blockplace"))) return false;
    if (!config.compatibilityLatestPhysics()
        && (checkName.contains("air") || checkName.contains("vertical") || checkName.contains("strafe"))) return false;
    if (!config.compatibilityLatestCombat()
        && (checkName.contains("rotation") || checkName.contains("yaw") || checkName.contains("pitch"))) return false;
    if (!config.compatibilityAttributeAwareBreaking()
        && (checkName.contains("toolboost") || checkName.contains("attribute") || checkName.contains("efficiency"))) return false;

    if (!config.compatibilityPacketLatencyNormalization()
        && (checkName.contains("latency") || checkName.contains("droprecovery"))) return false;
    if (!config.compatibilityKeepAliveCoalescing()
        && (checkName.contains("keepalive") || checkName.contains("ping"))) return false;
    if (!config.compatibilityTeleportConfirmCorrelation()
        && (checkName.contains("teleport") || checkName.contains("profilemismatch"))) return false;
    if (!config.compatibilityFoliaRegionSafeSetback() && checkName.contains("movementprotocol")) return false;
    if (!config.compatibilityFoliaAsyncEvidence() && checkName.contains("traffic")) return false;
    if (!config.compatibilityItemAttributeContext()
        && (checkName.contains("attribute") || checkName.contains("itemattribute"))) return false;
    if (!config.compatibilityItemEnchantContext() && checkName.contains("enchant")) return false;
    if (!config.compatibilityPotionContext()
        && (checkName.contains("haste") || checkName.contains("fatigue") || checkName.contains("potion"))) return false;
    if (!config.compatibilityLegacyTimingRelax() && checkName.contains("legacy")) return false;
    if (!config.compatibilityModernTimingStrict()
        && (checkName.contains("timing") || checkName.contains("cadence") || checkName.contains("interval"))) return false;
    if (!config.compatibilityViaProtocolCache() && checkName.contains("via")) return false;
    if (!config.compatibilityViaTranslationAware() && checkName.contains("via")) return false;
    if (!config.compatibilityPacketEventsNativePath() && (checkName.contains("packetevent") || checkName.contains("packetorder"))) return false;
    if (!config.compatibilityPacketEventsDecodeBypass() && checkName.contains("decode")) return false;
    if (!config.compatibilityFoliaRegionThreadPinning() && (checkName.contains("folia") || checkName.contains("regiontransition"))) return false;
    if (!config.compatibilityFoliaSchedulerBatching() && checkName.contains("foliatick")) return false;
    if (!config.compatibilityCommandAttributeTracing() && (checkName.contains("commandattribute") || checkName.contains("customitem"))) return false;
    if (!config.compatibilityNbtContextScan() && (checkName.contains("customitem") || checkName.contains("nbt"))) return false;
    if (!config.compatibilityToolDurabilityContext() && checkName.contains("durability")) return false;
    if (!config.compatibilityServerTickDriftCompensation() && (checkName.contains("tickskew") || checkName.contains("latencycoupling"))) return false;
    if (!config.compatibilityFoliaRegionIoFuse() && (checkName.contains("regionio") || checkName.contains("iofusion"))) return false;
    if (!config.compatibilityFoliaEntitySnapshotCache() && checkName.contains("entitysnapshot")) return false;
    if (!config.compatibilityViaVersionWindowSmoothing() && (checkName.contains("viasmoothing") || checkName.contains("viawindow"))) return false;
    if (!config.compatibilityViaKeepAliveRewriteAware() && (checkName.contains("viarewrite") || checkName.contains("keepalivevia"))) return false;
    if (!config.compatibilityPacketEventsFastReflection() && checkName.contains("fastreflection")) return false;
    if (!config.compatibilityPacketEventsBundleAware() && checkName.contains("bundle")) return false;
    if (!config.compatibilityDeepItemMetaHeuristics() && (checkName.contains("metasignature") || checkName.contains("deepmeta"))) return false;
    if (!config.compatibilityDeepItemAttributeSignature() && (checkName.contains("attributesignature") || checkName.contains("signaturelock"))) return false;
    if (!config.compatibilityDeepItemCommandLore() && (checkName.contains("commandlore") || checkName.contains("commandattribute"))) return false;
    if (!config.compatibilityDeepItemUnbreakableTracing() && checkName.contains("unbreakable")) return false;
    return true;
  }
}
