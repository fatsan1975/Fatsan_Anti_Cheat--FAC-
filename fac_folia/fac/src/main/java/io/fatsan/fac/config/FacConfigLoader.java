package io.fatsan.fac.config;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.plugin.java.JavaPlugin;

public final class FacConfigLoader {
  private final JavaPlugin plugin;

  public FacConfigLoader(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public FacConfig load() {
    FileConfiguration cfg = plugin.getConfig();

    String requestedProfile = cfg.getString("performance.profile", "auto");
    int cpuCores = Math.max(1, Runtime.getRuntime().availableProcessors());
    RuntimePerformanceDefaults runtimeDefaults = RuntimePerformanceDefaults.resolve(requestedProfile, cpuCores);

    return new FacConfig(
        cfg.getBoolean("alerts.enabled", true),
        clamp(cfg.getInt("evidence.max-window", 80), 8, 1024),
        clamp(cfg.getInt("checks.movement-cadence.buffer-limit", 8), 1, 128),
        clamp(cfg.getInt("checks.speed-envelope.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.vertical-motion-envelope.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.air-strafe-acceleration.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.vertical-direction-flip.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.micro-teleport.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.ground-spoof-pattern.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.air-hover-streak.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.combat-rotation-snap.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.rotation-quantization.buffer-limit", 7), 1, 128),
        clamp(cfg.getInt("checks.rotation-jitter-pattern.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.pitch-lock.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.impossible-critical.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.critical-cadence-abuse.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.hit-interval-burst.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.reach.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.reach-spike-cluster.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.reach-variance-collapse.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.auto-clicker.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.timer.buffer-limit", 10), 1, 128),
        clamp(cfg.getInt("checks.nofall.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.teleport-order.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.impossible-ground.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.keepalive-consistency.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.keepalive-drift.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.scaffold.buffer-limit", 7), 1, 128),
        clamp(cfg.getInt("checks.block-place-interval-consistency.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.fast-break.buffer-limit", 7), 1, 128),
        clamp(cfg.getInt("checks.fast-break-cadence-cluster.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.inventory-move.buffer-limit", 7), 1, 128),
        clamp(cfg.getInt("checks.inventory-burst-while-moving.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.ping-spoof.buffer-limit", 5), 1, 128),
        clamp(cfg.getInt("checks.ping-oscillation-spoof.buffer-limit", 6), 1, 128),
        clamp(cfg.getInt("checks.packet-burst.buffer-limit", 5), 1, 128),
        clamp(runtimeDefaults.maxEventsPerSecond(), 100, 5000),
        clamp(runtimeDefaults.keepAliveSampleIntervalMillis(), 50, 2000),
        runtimeDefaults.resolvedProfileName(),
        cfg.getBoolean("ml.enabled", true),
        cfg.getString("actions.mode", "ALERT"),
        cfg.getString("actions.policy-profile", "default"),
        clampDouble(cfg.getDouble("actions.risk-thresholds.alert", 3.0D), 0.1D, 100.0D),
        clampDouble(cfg.getDouble("actions.risk-thresholds.setback", 6.0D), 0.1D, 100.0D),
        clampDouble(cfg.getDouble("actions.risk-thresholds.kick", 10.5D), 0.1D, 100.0D),
        clamp(cfg.getInt("actions.cooldowns.alert-ms", 2000), 0, 120000),
        clamp(cfg.getInt("actions.cooldowns.setback-ms", 5000), 0, 120000),
        clamp(cfg.getInt("actions.cooldowns.kick-ms", 25000), 0, 600000),
        cfg.getBoolean("corroboration.enabled", true),
        clamp(cfg.getInt("corroboration.window-ms", 5000), 500, 120000),
        clamp(cfg.getInt("corroboration.min-distinct-categories", 2), 1, 5),
        clamp(cfg.getInt("corroboration.min-events", 4), 1, 20),
        cfg.getBoolean("security.world-seed-guard.enabled", true),
        cfg.getStringList("checks.disabled").stream()
            .map(v -> v.toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet()),
        cfg.getBoolean("compatibility.via-aware", true),
        cfg.getBoolean("compatibility.packet-events-optimized", true),
        cfg.getBoolean("compatibility.protocol-adaptive", true),
        cfg.getBoolean("compatibility.legacy-combat", true),
        cfg.getBoolean("compatibility.legacy-movement", true),
        cfg.getBoolean("compatibility.modern-offhand", true),
        cfg.getBoolean("compatibility.modern-inventory", true),
        cfg.getBoolean("compatibility.latest-physics", true),
        cfg.getBoolean("compatibility.latest-combat", true),
        cfg.getBoolean("compatibility.attribute-aware-breaking", true),
        cfg.getBoolean("compatibility.packet-latency-normalization", true),
        cfg.getBoolean("compatibility.keepalive-coalescing", true),
        cfg.getBoolean("compatibility.teleport-confirm-correlation", true),
        cfg.getBoolean("compatibility.folia-region-safe-setback", true),
        cfg.getBoolean("compatibility.folia-async-evidence", true),
        cfg.getBoolean("compatibility.item-attribute-context", true),
        cfg.getBoolean("compatibility.item-enchant-context", true),
        cfg.getBoolean("compatibility.potion-context", true),
        cfg.getBoolean("compatibility.legacy-timing-relax", true),
        cfg.getBoolean("compatibility.modern-timing-strict", true),
        cfg.getBoolean("compatibility.via-protocol-cache", true),
        cfg.getBoolean("compatibility.via-translation-aware", true),
        cfg.getBoolean("compatibility.packet-events-native-path", true),
        cfg.getBoolean("compatibility.packet-events-decode-bypass", true),
        cfg.getBoolean("compatibility.folia-region-thread-pinning", true),
        cfg.getBoolean("compatibility.folia-scheduler-batching", true),
        cfg.getBoolean("compatibility.command-attribute-tracing", true),
        cfg.getBoolean("compatibility.nbt-context-scan", true),
        cfg.getBoolean("compatibility.tool-durability-context", true),
        cfg.getBoolean("compatibility.server-tick-drift-compensation", true),
        cfg.getBoolean("compatibility.folia-region-io-fuse", true),
        cfg.getBoolean("compatibility.folia-entity-snapshot-cache", true),
        cfg.getBoolean("compatibility.via-version-window-smoothing", true),
        cfg.getBoolean("compatibility.via-keepalive-rewrite-aware", true),
        cfg.getBoolean("compatibility.packet-events-fast-reflection", true),
        cfg.getBoolean("compatibility.packet-events-bundle-aware", true),
        cfg.getBoolean("compatibility.deep-item-meta-heuristics", true),
        cfg.getBoolean("compatibility.deep-item-attribute-signature", true),
        cfg.getBoolean("compatibility.deep-item-command-lore", true),
        cfg.getBoolean("compatibility.deep-item-unbreakable-tracing", true),
        cfg.getBoolean("premium.enabled", false),
        cfg.getString("premium.license-key", ""),
        cfg.getBoolean("premium.webhook.enabled", false),
        cfg.getString("premium.webhook.url", ""));
  }

  private record RuntimePerformanceDefaults(
      String resolvedProfileName, int maxEventsPerSecond, int keepAliveSampleIntervalMillis) {
    private static RuntimePerformanceDefaults resolve(String requestedProfile, int cpuCores) {
      if (requestedProfile == null) {
        requestedProfile = "auto";
      }
      String normalized = requestedProfile.toLowerCase(Locale.ROOT);
      return switch (normalized) {
        case "low", "safe" -> new RuntimePerformanceDefaults("safe", 500, 300);
        case "high", "aggressive" -> new RuntimePerformanceDefaults("aggressive", 1600, 180);
        case "balanced" -> new RuntimePerformanceDefaults("balanced", 900, 220);
        case "auto" -> auto(cpuCores);
        default -> new RuntimePerformanceDefaults(normalized, 900, 220);
      };
    }

    private static RuntimePerformanceDefaults auto(int cpuCores) {
      if (cpuCores <= 2) {
        return new RuntimePerformanceDefaults("auto-safe-" + cpuCores + "c", 450, 320);
      }
      if (cpuCores <= 4) {
        return new RuntimePerformanceDefaults("auto-balanced-" + cpuCores + "c", 700, 260);
      }
      if (cpuCores <= 8) {
        return new RuntimePerformanceDefaults("auto-balanced-" + cpuCores + "c", 1000, 220);
      }
      return new RuntimePerformanceDefaults("auto-aggressive-" + cpuCores + "c", 1400, 180);
    }
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  private static double clampDouble(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}
