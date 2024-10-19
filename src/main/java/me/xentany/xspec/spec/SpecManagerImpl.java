package me.xentany.xspec.spec;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.util.DebugInfoUtil;
import me.xentany.xspec.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SpecManagerImpl implements SpecManager {

  private static final double MAX_DISTANCE_SQUARED;

  static {
    MAX_DISTANCE_SQUARED = Math.pow(Settings.IMP.MAIN.MAXIMUM_DISTANCE, 2);
  }

  private final Map<Player, Spec> specs;

  public SpecManagerImpl() {
    this.specs = new ConcurrentHashMap<>();

    var plugin = SpecPlugin.getInstance();

    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () ->
        specs.values().forEach(spec -> {
          var spectator = spec.spectator();
          var suspectLocation = spec.suspect().getLocation();
          var spectatorLocation = spectator.getLocation();

          spec.logger().logLocation();

          if (!spectatorLocation.getWorld().equals(suspectLocation.getWorld()) ||
              spectatorLocation.distanceSquared(suspectLocation) > MAX_DISTANCE_SQUARED) {
            Bukkit.getScheduler().runTask(plugin, () -> {
              spectator.teleportAsync(suspectLocation);
              MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.TOO_FAR);
            });
          }
        }), 60L, 60L);
  }

  @Override
  public boolean isInSpec(final @NotNull Player player) {
    return this.specs.containsKey(player) ||
        this.specs.values().stream()
            .anyMatch(spec -> spec.suspect().equals(player));
  }

  @Override
  public boolean isSpectator(final @NotNull Player player) {
    return this.specs.containsKey(player);
  }

  @Override
  public boolean tryStart(final @NotNull Spec spec) {
    var spectator = spec.spectator();

    if (!this.specs.containsKey(spectator)) {
      this.specs.put(spectator, spec);

      spectator.showBossBar(spec.specBar().bossBar());

      DebugInfoUtil.hideDebugInfo(spectator);

      spectator.setGameMode(GameMode.SPECTATOR);
      spectator.teleportAsync(spec.suspect().getLocation());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void stop(final @NotNull Spec spec) {
    var spectator = spec.spectator();

    var x = Settings.IMP.MAIN.TELEPORT_X;
    var y = Settings.IMP.MAIN.TELEPORT_Y;
    var z = Settings.IMP.MAIN.TELEPORT_Z;

    Optional.ofNullable(Bukkit.getWorld(Settings.IMP.MAIN.TELEPORT_WORLD_NAME)).ifPresentOrElse(
        world -> spectator.teleportAsync(new Location(world, x, y, z)),
        () -> {
          spectator.teleportAsync(new Location(spectator.getWorld(), x, y, z));
          MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.WORLD_NOT_FOUND);
        }
    );

    spectator.setGameMode(GameMode.SURVIVAL);
    spectator.hideBossBar(spec.specBar().bossBar());

    DebugInfoUtil.showDebugInfo(spectator);

    ((SpecLoggerImpl) spec.logger()).stop();

    this.specs.remove(spectator);
  }

  @Override
  public void stopAll() {
    this.specs.values().forEach(this::stop);
  }

  @Override
  public Optional<Spec> findSpec(final @NotNull Player player) {
    return Optional.ofNullable(this.specs.get(player))
        .or(() -> this.specs.values().stream()
            .filter(spec -> spec.suspect().equals(player))
            .findFirst());
  }

  @Override
  public Optional<Spec> resolveSpec(final @NotNull Player spectator) {
    return Optional.ofNullable(this.specs.get(spectator));
  }

  @Contract(pure = true)
  @Override
  public @NotNull @UnmodifiableView Map<Player, Spec> getSpecs() {
    return Collections.unmodifiableMap(this.specs);
  }
}