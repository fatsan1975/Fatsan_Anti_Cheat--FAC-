package io.fatsan.fac.service;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import java.util.Locale;

public final class ActionPolicyService {
  private final String profile;

  public ActionPolicyService(String profile) {
    this.profile = profile == null || profile.isBlank() ? "default" : profile.toLowerCase(Locale.ROOT);
  }

  public ResolvedPolicy resolve(CheckResult result) {
    return resolve(result.checkName(), result.category());
  }

  public ResolvedPolicy resolve(String checkName, CheckCategory category) {
    String normalized = checkName.toLowerCase(Locale.ROOT);

    if (isDeepItemContext(normalized)) {
      if (isCustomMechanicsSafe()) {
        return new ResolvedPolicy(ActionDisposition.DISABLED_BY_DEFAULT, SuspicionTier.HOT, "deep-item-context");
      }
      return new ResolvedPolicy(ActionDisposition.REVIEW_ONLY, SuspicionTier.HOT, "deep-item-context");
    }

    if (isViaDerived(normalized) || isTimingDerived(normalized)) {
      return new ResolvedPolicy(ActionDisposition.REVIEW_ONLY, isStrict() ? SuspicionTier.ELEVATED : SuspicionTier.HOT, "via-or-timing-derived");
    }

    if (isProtocolNoiseFamily(normalized, category)) {
      return new ResolvedPolicy(ActionDisposition.ALERT_ONLY, isLightweight() ? SuspicionTier.HOT : SuspicionTier.ELEVATED, "protocol-noise");
    }

    if (isCorePunishCandidate(normalized)) {
      return new ResolvedPolicy(isStrict() ? ActionDisposition.CORROBORATED_KICK : ActionDisposition.CORROBORATED_SETBACK, SuspicionTier.BASELINE, "core-punish");
    }

    if (isRedundantStatFamily(normalized)) {
      return new ResolvedPolicy(ActionDisposition.ALERT_ONLY, isStrict() ? SuspicionTier.ELEVATED : SuspicionTier.HOT, "statistical-family");
    }

    if (category == CheckCategory.MOVEMENT || category == CheckCategory.COMBAT || category == CheckCategory.WORLD) {
      return new ResolvedPolicy(ActionDisposition.ALERT_ONLY, SuspicionTier.BASELINE, "core-domain");
    }

    return new ResolvedPolicy(ActionDisposition.ALERT_ONLY, SuspicionTier.ELEVATED, "default");
  }

  public boolean shouldEvaluate(String checkName, CheckCategory category, SuspicionTier tier) {
    ResolvedPolicy policy = resolve(checkName, category);
    return policy.disposition() != ActionDisposition.DISABLED_BY_DEFAULT && tier.atLeast(policy.minimumEvaluationTier());
  }

  public SuspicionTier tierFor(double risk, double trust, double recentIntensity) {
    if (risk >= 7.5D || trust <= 0.20D || recentIntensity >= 5.0D) {
      return SuspicionTier.HOT;
    }
    if (risk >= 3.0D || trust <= 0.45D || recentIntensity >= 2.0D) {
      return SuspicionTier.ELEVATED;
    }
    return SuspicionTier.BASELINE;
  }

  public String profile() {
    return profile;
  }

  private boolean isStrict() {
    return "strict".equals(profile) || "practice".equals(profile) || "pvp".equals(profile);
  }

  private boolean isLightweight() {
    return "lightweight".equals(profile) || "safe".equals(profile);
  }

  private boolean isCustomMechanicsSafe() {
    return "custom-mechanics-safe".equals(profile);
  }

  private static boolean isViaDerived(String normalized) {
    return normalized.contains("via")
        || normalized.contains("rewrite")
        || normalized.contains("smoothing")
        || normalized.contains("window")
        || normalized.contains("smear")
        || normalized.contains("skew");
  }

  private static boolean isTimingDerived(String normalized) {
    return normalized.contains("packetorder")
        || normalized.contains("bundle")
        || normalized.contains("timingmismatch")
        || normalized.contains("latency")
        || normalized.contains("profilemismatch");
  }

  private static boolean isProtocolNoiseFamily(String normalized, CheckCategory category) {
    return category == CheckCategory.PROTOCOL
        || normalized.contains("keepalive")
        || normalized.contains("ping")
        || normalized.contains("traffic")
        || normalized.contains("teleport");
  }

  private static boolean isCorePunishCandidate(String normalized) {
    return normalized.contains("speedenvelope")
        || normalized.contains("verticalmotionenvelope")
        || normalized.contains("nofall")
        || normalized.contains("impossibleground")
        || normalized.contains("reachheuristic")
        || normalized.contains("impossiblecritical")
        || normalized.contains("fastbreak")
        || normalized.contains("scaffoldpattern");
  }

  private static boolean isRedundantStatFamily(String normalized) {
    return normalized.contains("cadence")
        || normalized.contains("interval")
        || normalized.contains("burst")
        || normalized.contains("entropy")
        || normalized.contains("variance")
        || normalized.contains("collapse")
        || normalized.contains("plateau")
        || normalized.contains("lock")
        || normalized.contains("oscillation")
        || normalized.contains("modulo");
  }

  private static boolean isDeepItemContext(String normalized) {
    return normalized.contains("attribute")
        || normalized.contains("lore")
        || normalized.contains("enchant")
        || normalized.contains("meta")
        || normalized.contains("customitem")
        || normalized.contains("unbreakable");
  }

  public record ResolvedPolicy(
      ActionDisposition disposition, SuspicionTier minimumEvaluationTier, String family) {}
}
