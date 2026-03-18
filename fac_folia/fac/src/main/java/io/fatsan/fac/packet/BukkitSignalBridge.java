package io.fatsan.fac.packet;

import io.fatsan.fac.model.BlockBreakEventSignal;
import io.fatsan.fac.model.BlockPlaceEventSignal;
import io.fatsan.fac.model.CombatHitEvent;
import io.fatsan.fac.model.InventoryClickEventSignal;
import io.fatsan.fac.model.KeepAliveSignal;
import io.fatsan.fac.model.MovementEvent;
import io.fatsan.fac.model.RotationEvent;
import io.fatsan.fac.model.TeleportSignal;
import io.fatsan.fac.service.PlayerSignalTracker;
import io.fatsan.fac.service.PlayerStateService;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BukkitSignalBridge implements Listener {
  private final PacketIntakeService intake;
  private final PlayerSignalTracker tracker;
  private final PlayerStateService playerStateService;
  private final int keepAliveSampleIntervalMillis;
  private static final long ITEM_CONTEXT_CACHE_TTL_NANOS = 2_000_000_000L;
  private static final Map<Integer, CachedItemContext> ITEM_CONTEXT_CACHE = new ConcurrentHashMap<>();

  public BukkitSignalBridge(
      PacketIntakeService intake,
      PlayerSignalTracker tracker,
      PlayerStateService playerStateService,
      int keepAliveSampleIntervalMillis) {
    this.intake = intake;
    this.tracker = tracker;
    this.playerStateService = playerStateService;
    this.keepAliveSampleIntervalMillis = keepAliveSampleIntervalMillis;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null || event.getFrom().getWorld() != event.getTo().getWorld()) {
      return;
    }
    Player player = event.getPlayer();
    long now = System.nanoTime();
    long interval = tracker.intervalMove(player.getUniqueId(), now);
    double dx = event.getTo().getX() - event.getFrom().getX();
    double dz = event.getTo().getZ() - event.getFrom().getZ();
    float dyaw = event.getTo().getYaw() - event.getFrom().getYaw();
    float dpitch = event.getTo().getPitch() - event.getFrom().getPitch();
    double dy = event.getTo().getY() - event.getFrom().getY();
    double deltaXZ = Math.sqrt(dx * dx + dz * dz);

    if (deltaXZ < 0.38D && player.isOnGround()) {
      playerStateService.updateSafeLocation(player.getUniqueId(), event.getTo());
    }

    intake.emit(
        new MovementEvent(
            playerId(player),
            now,
            deltaXZ,
            dy,
            player.isOnGround(),
            player.getFallDistance(),
            player.isGliding(),
            player.isInsideVehicle(),
            interval));
    intake.emit(new RotationEvent(playerId(player), now, dyaw, dpitch));
    if (tracker.shouldSampleKeepAlive(player.getUniqueId(), now, keepAliveSampleIntervalMillis)) {
      intake.emit(new KeepAliveSignal(playerId(player), now, player.getPing()));
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onHit(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player player)) {
      return;
    }
    long now = System.nanoTime();
    long interval = tracker.intervalHit(player.getUniqueId(), now);
    double distance = player.getLocation().distance(event.getEntity().getLocation());
    boolean critLike = !player.isOnGround() && player.getFallDistance() > 0.0F;
    intake.emit(
        new CombatHitEvent(
            playerId(player),
            now,
            distance,
            critLike,
            player.isOnGround(),
            player.getFallDistance(),
            player.isGliding(),
            player.isInsideVehicle(),
            interval));
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    long now = System.nanoTime();
    long interval = tracker.intervalPlace(player.getUniqueId(), now);
    double horizontal = player.getVelocity().setY(0).length();
    intake.emit(new BlockPlaceEventSignal(playerId(player), now, interval, player.isSprinting(), horizontal));
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    long now = System.nanoTime();
    long interval = tracker.intervalBreak(player.getUniqueId(), now);
    ItemStack hand = player.getInventory().getItemInMainHand();
    int haste = potionAmplifierByName(player, "HASTE", "FAST_DIGGING");
    int fatigue = potionAmplifierByName(player, "MINING_FATIGUE", "SLOW_DIGGING");
    double attackSpeed = 4.0D;
    double movementSpeed = Math.max(0.1D, player.getWalkSpeed());
    ItemContext itemContext = resolveItemContext(hand, now);
    intake.emit(
        new BlockBreakEventSignal(
            playerId(player),
            now,
            interval,
            itemContext.efficiency,
            haste,
            fatigue,
            attackSpeed,
            movementSpeed,
            itemContext.itemTypeKey,
            itemContext.itemAttackBonus,
            itemContext.itemMoveBonus,
            itemContext.enchantWeight,
            itemContext.customContext));
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    long now = System.nanoTime();
    long interval = tracker.intervalInventoryClick(player.getUniqueId(), now);
    boolean movingFast = player.getVelocity().setY(0).length() > 0.23D;
    intake.emit(new InventoryClickEventSignal(playerId(player), now, interval, movingFast));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onTeleport(PlayerTeleportEvent event) {
    intake.emit(new TeleportSignal(playerId(event.getPlayer()), System.nanoTime()));
    if (event.getTo() != null) {
      playerStateService.updateSafeLocation(event.getPlayer().getUniqueId(), event.getTo());
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    tracker.clear(event.getPlayer().getUniqueId());
    playerStateService.clear(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    tracker.clear(event.getPlayer().getUniqueId());
    playerStateService.updateSafeLocation(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
  }


  private static int potionAmplifierByName(Player player, String... names) {
    for (PotionEffect effect : player.getActivePotionEffects()) {
      PotionEffectType type = effect.getType();
      if (type == null) continue;
      String key = type.getKey().getKey().toUpperCase(java.util.Locale.ROOT);
      for (String name : names) {
        if (key.contains(name)) return effect.getAmplifier();
      }
    }
    return -1;
  }


  private static double itemAttributeBonus(ItemStack item, String token) {
    if (item == null) return 0.0D;
    var meta = item.getItemMeta();
    if (meta == null) return 0.0D;
    var modifiers = meta.getAttributeModifiers();
    if (modifiers == null) return 0.0D;
    double sum = 0.0D;
    for (var entry : modifiers.entries()) {
      if (entry.getKey() != null && String.valueOf(entry.getKey()).contains(token)) {
        sum += entry.getValue().getAmount();
      }
    }
    return sum;
  }

  private static int enchantWeight(ItemStack item) {
    if (item == null) return 0;
    int total = 0;
    for (int level : item.getEnchantments().values()) {
      total += Math.max(level, 0);
    }
    return total;
  }

  private static ItemContext resolveItemContext(ItemStack item, long nowNanos) {
    if (item == null || item.getType().isAir()) {
      return ItemContext.EMPTY;
    }
    int key = itemFingerprint(item);
    CachedItemContext cached = ITEM_CONTEXT_CACHE.get(key);
    if (cached != null && nowNanos - cached.nanoTime <= ITEM_CONTEXT_CACHE_TTL_NANOS) {
      return cached.context;
    }

    ItemMeta meta = item.getItemMeta();
    int efficiency = item.getEnchantmentLevel(Enchantment.EFFICIENCY);
    double itemAttackBonus = itemAttributeBonus(item, "ATTACK_SPEED");
    double itemMoveBonus = itemAttributeBonus(item, "MOVEMENT_SPEED");
    int enchantWeight = enchantWeight(item);

    boolean hasPersistentData =
        meta != null && meta.getPersistentDataContainer() != null && !meta.getPersistentDataContainer().isEmpty();
    int damage = meta instanceof Damageable damageable ? damageable.getDamage() : 0;
    boolean commandLikeContext =
        meta != null
            && ((meta.hasCustomModelData())
                || meta.isUnbreakable()
                || hasPersistentData
                || damage > 150
                || (meta.hasLore() && !meta.getLore().isEmpty())
                || (meta.hasDisplayName() && meta.getDisplayName().contains("+")));

    boolean customContext =
        commandLikeContext
            || itemAttackBonus > 0.75D
            || itemMoveBonus > 0.10D
            || (itemAttackBonus + itemMoveBonus) > 1.25D
            || enchantWeight > 8
            || efficiency >= 5;

    ItemContext context =
        new ItemContext(
            efficiency,
            item.getType().name(),
            itemAttackBonus,
            itemMoveBonus,
            enchantWeight,
            customContext);
    ITEM_CONTEXT_CACHE.put(key, new CachedItemContext(nowNanos, context));
    if ((nowNanos & 0x1FF) == 0) {
      ITEM_CONTEXT_CACHE.entrySet().removeIf(e -> nowNanos - e.getValue().nanoTime > ITEM_CONTEXT_CACHE_TTL_NANOS);
    }
    return context;
  }

  private static int itemFingerprint(ItemStack item) {
    int hash = 17;
    hash = 31 * hash + item.getType().hashCode();
    hash = 31 * hash + item.getAmount();
    hash = 31 * hash + item.getEnchantments().hashCode();
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      hash = 31 * hash + meta.hashCode();
    }
    return hash;
  }

  private record CachedItemContext(long nanoTime, ItemContext context) {}

  private record ItemContext(
      int efficiency,
      String itemTypeKey,
      double itemAttackBonus,
      double itemMoveBonus,
      int enchantWeight,
      boolean customContext) {
    private static final ItemContext EMPTY = new ItemContext(0, "AIR", 0.0D, 0.0D, 0, false);
  }


  private static String playerId(Player player) {
    return player.getUniqueId().toString();
  }
}
