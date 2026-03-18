package io.fatsan.fac.bootstrap;

import io.fatsan.fac.config.FacConfig;
import io.fatsan.fac.config.FacConfigLoader;
import io.fatsan.fac.engine.ActionService;
import io.fatsan.fac.engine.AntiCheatEngine;
import io.fatsan.fac.engine.CheckRegistry;
import io.fatsan.fac.engine.EvidenceService;
import io.fatsan.fac.packet.BukkitSignalBridge;
import io.fatsan.fac.nextlevel.NextLevelAntiCheatPlatform;
import io.fatsan.fac.packet.PacketIntakeService;
import io.fatsan.fac.security.WorldSeedGuardListener;
import io.fatsan.fac.service.ActionPolicyService;
import io.fatsan.fac.service.AlertWebhookService;
import io.fatsan.fac.service.CorroborationService;
import io.fatsan.fac.service.PlayerSignalTracker;
import io.fatsan.fac.service.PlayerStateService;
import io.fatsan.fac.service.PlayerTrustService;
import io.fatsan.fac.service.PremiumInsightsService;
import io.fatsan.fac.service.PremiumLicenseService;
import io.fatsan.fac.service.RiskService;
import io.fatsan.fac.service.SuspicionPatternService;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class FatsanAntiCheatPlugin extends JavaPlugin {
  private AntiCheatEngine engine;
  private BukkitSignalBridge bridge;
  private FacConfig facConfig;
  private WorldSeedGuardListener worldSeedGuardListener;
  private PremiumLicenseService.LicenseValidation premiumLicenseValidation;
  private PremiumInsightsService premiumInsightsService;
  private NextLevelAntiCheatPlatform nextLevelPlatform;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    startRuntime();
  }

  @Override
  public void onDisable() {
    stopRuntime();
    getLogger().log(Level.INFO, "FAC disabled");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("fac")) {
      return false;
    }

    if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      long events = engine == null ? 0 : engine.processedEvents();
      long suspicious = engine == null ? 0 : engine.suspiciousResults();
      sender.sendMessage(
          "[FAC] status profile="
              + (facConfig == null ? "unknown" : facConfig.performanceProfile())
              + " policy="
              + (facConfig == null ? "unknown" : facConfig.actionPolicyProfile())
              + " action="
              + (facConfig == null ? "unknown" : facConfig.actionMode())
              + " processed="
              + events
              + " suspicious="
              + suspicious);
      return true;
    }

    if (args[0].equalsIgnoreCase("premium")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      String licenseState = premiumLicenseValidation == null ? "unknown" : premiumLicenseValidation.tier();
      boolean valid = premiumLicenseValidation != null && premiumLicenseValidation.valid();
      sender.sendMessage(
          "[FAC] premium enabled="
              + (facConfig != null && facConfig.premiumEnabled())
              + " valid="
              + valid
              + " tier="
              + licenseState);
      if (premiumInsightsService != null
          && facConfig != null
          && facConfig.premiumEnabled()
          && premiumLicenseValidation != null
          && premiumLicenseValidation.valid()) {
        PremiumInsightsService.PremiumSnapshot snapshot = premiumInsightsService.snapshot();
        sender.sendMessage(
            "[FAC] premium insights alerts="
                + snapshot.totalAlerts()
                + " avgSeverity="
                + String.format(Locale.ROOT, "%.2f", snapshot.averageSeverity())
                + " top="
                + snapshot.topChecks());
      }
      return true;
    }

    if (args[0].equalsIgnoreCase("feedback")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      if (args.length < 3) {
        sender.sendMessage("[FAC] Usage: /fac feedback <playerId> <reason>");
        return true;
      }
      nextLevelPlatform.markFalsePositive(args[1], args[2]);
      sender.sendMessage("[FAC] false-positive feedback saved for " + args[1]);
      return true;
    }

    if (args[0].equalsIgnoreCase("label")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      if (args.length < 3) {
        sender.sendMessage("[FAC] Usage: /fac label <playerId> <ban|kick|review|clean>");
        return true;
      }
      nextLevelPlatform.pushGroundTruthLabel(args[1], args[2]);
      sender.sendMessage("[FAC] ground-truth label saved for " + args[1]);
      return true;
    }

    if (args[0].equalsIgnoreCase("dq")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      NextLevelAntiCheatPlatform.DataQualitySnapshot dq = nextLevelPlatform.dataQuality();
      sender.sendMessage("[FAC] dq events=" + dq.events() + " parseErrors=" + dq.parseErrors() + " outOfOrder=" + dq.outOfOrder() + " missing=" + dq.missingFields());
      NextLevelAntiCheatPlatform.NextLevelStatus status = nextLevelPlatform.status();
      sender.sendMessage("[FAC] next-level suspicious=" + status.suspiciousEvents() + " shadow=" + status.shadowFlaggedPlayers() + " labels=" + status.labels());
      NextLevelAntiCheatPlatform.HealthSnapshot health = nextLevelPlatform.selfHealth();
      sender.sendMessage("[FAC] health checks=" + health.checksTracked() + " failures=" + health.totalCheckFailures() + " worstLatencyNs=" + health.worstCheckLatencyNanos());
      sender.sendMessage("[FAC] fp-by-check=" + nextLevelPlatform.feedbackSummaryByCheck());
      return true;
    }

    if (args[0].equalsIgnoreCase("reload")) {
      if (!sender.hasPermission("fac.admin")) {
        sender.sendMessage("[FAC] Missing permission: fac.admin");
        return true;
      }
      reloadConfig();
      stopRuntime();
      startRuntime();
      sender.sendMessage("[FAC] Reload complete.");
      return true;
    }

    sender.sendMessage("[FAC] Usage: /fac <status|premium|feedback|label|dq|reload>");
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (!command.getName().equalsIgnoreCase("fac")) {
      return List.of();
    }
    if (args.length == 1) {
      return List.of("status", "premium", "feedback", "label", "dq", "reload");
    }
    return List.of();
  }

  private void startRuntime() {
    this.facConfig = new FacConfigLoader(this).load();
    this.premiumInsightsService = new PremiumInsightsService();
    this.nextLevelPlatform = new NextLevelAntiCheatPlatform();
    PremiumLicenseService premiumLicenseService = new PremiumLicenseService();
    this.premiumLicenseValidation =
        premiumLicenseService.validate(facConfig.premiumLicenseKey(), facConfig.premiumEnabled());
    if (facConfig.premiumEnabled()) {
      getLogger().info(
          "FAC premium state: "
              + premiumLicenseValidation.message()
              + " tier="
              + premiumLicenseValidation.tier());
    }

    EvidenceService evidenceService = new EvidenceService(facConfig);
    PlayerStateService playerStateService = new PlayerStateService();
    CorroborationService corroborationService =
        new CorroborationService(
            facConfig.corroborationWindowMillis(),
            facConfig.corroborationMinDistinctCategories(),
            facConfig.corroborationMinEvents());
    boolean premiumActive = facConfig.premiumEnabled() && premiumLicenseValidation.valid();
    AlertWebhookService webhookService =
        new AlertWebhookService(
            getLogger(),
            premiumActive && facConfig.premiumWebhookEnabled(),
            facConfig.premiumWebhookUrl());
    ActionPolicyService actionPolicyService = new ActionPolicyService(facConfig.actionPolicyProfile());
    ActionService actionService =
        new ActionService(
            this,
            facConfig,
            playerStateService,
            corroborationService,
            webhookService,
            premiumInsightsService,
            actionPolicyService,
            premiumActive);
    RiskService riskService = new RiskService();
    PlayerTrustService playerTrustService = new PlayerTrustService();
    SuspicionPatternService suspicionPatternService = new SuspicionPatternService();

    CheckRegistry checkRegistry =
        CheckRegistry.standard(facConfig, evidenceService, nextLevelPlatform);
    PacketIntakeService packetIntakeService =
        new PacketIntakeService(checkRegistry, facConfig.maxEventsPerSecond());
    this.engine =
        new AntiCheatEngine(
            packetIntakeService,
            actionService,
            riskService,
            corroborationService,
            playerTrustService,
            suspicionPatternService,
            actionPolicyService,
            nextLevelPlatform);
    this.engine.start();

    PlayerSignalTracker tracker = new PlayerSignalTracker();
    this.bridge =
        new BukkitSignalBridge(
            packetIntakeService,
            tracker,
            playerStateService,
            facConfig.keepAliveSampleIntervalMillis());
    getServer().getPluginManager().registerEvents(this.bridge, this);

    if (facConfig.worldSeedGuardEnabled()) {
      this.worldSeedGuardListener = new WorldSeedGuardListener();
      getServer().getPluginManager().registerEvents(this.worldSeedGuardListener, this);
    }

    getLogger().info("FAC enabled with profile: " + facConfig.performanceProfile());
  }

  private void stopRuntime() {
    if (this.bridge != null) {
      HandlerList.unregisterAll(this.bridge);
      this.bridge = null;
    }
    if (this.worldSeedGuardListener != null) {
      HandlerList.unregisterAll(this.worldSeedGuardListener);
      this.worldSeedGuardListener = null;
    }
    if (this.engine != null) {
      this.engine.stop();
      this.engine = null;
    }
  }
}
