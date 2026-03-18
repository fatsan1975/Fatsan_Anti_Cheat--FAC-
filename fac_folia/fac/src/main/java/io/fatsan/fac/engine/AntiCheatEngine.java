package io.fatsan.fac.engine;

import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.NormalizedEvent;
import io.fatsan.fac.packet.PacketIntakeService;
import io.fatsan.fac.service.CorroborationService;
import io.fatsan.fac.service.PlayerTrustService;
import io.fatsan.fac.service.RiskService;
import io.fatsan.fac.service.SuspicionPatternService;
import io.fatsan.fac.nextlevel.NextLevelAntiCheatPlatform;
import io.fatsan.fac.service.ActionPolicyService;
import io.fatsan.fac.service.SuspicionTier;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public final class AntiCheatEngine {
  private final PacketIntakeService packetIntakeService;
  private final ActionService actionService;
  private final RiskService riskService;
  private final CorroborationService corroborationService;
  private final PlayerTrustService playerTrustService;
  private final SuspicionPatternService suspicionPatternService;
  private final ActionPolicyService actionPolicyService;
  private final NextLevelAntiCheatPlatform nextLevelPlatform;
  private final LongAdder processedEvents = new LongAdder();
  private final LongAdder suspiciousResults = new LongAdder();

  public AntiCheatEngine(
      PacketIntakeService packetIntakeService,
      ActionService actionService,
      RiskService riskService,
      CorroborationService corroborationService,
      PlayerTrustService playerTrustService,
      SuspicionPatternService suspicionPatternService,
      ActionPolicyService actionPolicyService,
      NextLevelAntiCheatPlatform nextLevelPlatform) {
    this.packetIntakeService = packetIntakeService;
    this.actionService = actionService;
    this.riskService = riskService;
    this.corroborationService = corroborationService;
    this.playerTrustService = playerTrustService;
    this.suspicionPatternService = suspicionPatternService;
    this.actionPolicyService = actionPolicyService;
    this.nextLevelPlatform = nextLevelPlatform;
  }

  public void start() {
    packetIntakeService.setConsumer(this::onEvent);
  }

  public void stop() {
    packetIntakeService.setConsumer(event -> {});
  }

  public long processedEvents() {
    return processedEvents.sum();
  }

  public long suspiciousResults() {
    return suspiciousResults.sum();
  }

  private void onEvent(NormalizedEvent event) {
    processedEvents.increment();
    nextLevelPlatform.onEvent(event);
    SuspicionTier tier =
        actionPolicyService.tierFor(
            riskService.currentRisk(event.playerId()),
            playerTrustService.trustScore(event.playerId()),
            suspicionPatternService.recentIntensity(event.playerId()));
    List<CheckResult> results = packetIntakeService.registry().evaluateAll(event, tier, actionPolicyService);
    if (results.isEmpty()) {
      playerTrustService.onCleanEvent(event.playerId());
      suspicionPatternService.onCleanEvent(event.playerId());
      riskService.coolDown(event.playerId());
      return;
    }

    for (CheckResult result : results) {
      suspiciousResults.increment();
      playerTrustService.onSuspicion(event.playerId(), result);
      corroborationService.record(event.playerId(), result);
      double multiplier =
          playerTrustService.riskMultiplier(event.playerId())
              * suspicionPatternService.onSuspicion(event.playerId(), result);
      double risk = riskService.apply(event.playerId(), result, multiplier);
      nextLevelPlatform.onResult(event.playerId(), result, risk);
      actionService.handleSuspicion(event.playerId(), result, risk);
    }
  }
}
