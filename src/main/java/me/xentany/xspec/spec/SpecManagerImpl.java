package me.xentany.xspec.spec;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.util.DateFormatUtil;
import me.xentany.xspec.util.ProtocolLibUtil;
import me.xentany.xspec.util.MessageUtil;
import me.xentany.xspec.util.WebhookUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.MessageFormat;
import java.util.Arrays;
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
          var suspect = spec.suspect();
          var suspectLocation = suspect.getLocation();
          var spectatorLocation = spectator.getLocation();

          if (Settings.IMP.MAIN.ACTIONBAR) {
            spectator.sendActionBar(MessageUtil.getFormattedComponent(Settings.IMP.MAIN.MESSAGES.ACTIONBAR, suspect.getName(), String.valueOf(suspect.getPing())));
          }

          spec.logger().logLocation();

          if (!spectatorLocation.getWorld().equals(suspectLocation.getWorld()) ||
              spectatorLocation.distanceSquared(suspectLocation) > MAX_DISTANCE_SQUARED) {
            Bukkit.getScheduler().runTask(plugin, () -> {
              spectator.teleportAsync(suspectLocation);
              MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.TOO_FAR);
            });
          }

          ProtocolLibUtil.updateGlowing(suspect);
        }), 40L, 40L);
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
      var suspect = spec.suspect();

      spectator.setGameMode(GameMode.SPECTATOR);

      this.specs.put(spectator, spec);

      ProtocolLibUtil.hideDebugInfo(spectator);

      if (Settings.IMP.MAIN.SUSPECT_GLOW) {
        ProtocolLibUtil.addGlowingRelation(suspect, spectator);
      }

      if (Settings.IMP.MAIN.NIGHT_VISION) {
        ProtocolLibUtil.addNightVision(spectator);
      }

      spectator.teleportAsync(suspect.getLocation());

      if (Settings.IMP.MAIN.BOSSBAR_ENABLED) {
        spectator.showBossBar(spec.specBar().getBossBar());
      }

      if (Settings.IMP.MAIN.NOTIFY) {
        Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.hasPermission("xspec.notify"))
            .filter(player -> player != spectator)
            .forEach(player ->
                MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.STARTED_NOTIFY, spectator.getName(), suspect.getName(), spec.reason(), DateFormatUtil.getFormattedDate())
            );
      }

      var webhook = Settings.IMP.MAIN.WEBHOOK;

      if (!webhook.isEmpty()) {
        WebhookUtil.sendWebhookAsync(webhook, Settings.IMP.MAIN.MESSAGES.STARTED_WEBHOOK
            .replace("{0}", suspect.getName())
            .replace("{1}", DateFormatUtil.getFormattedDate())
            .replace("{2}", spec.reason())
            .replace("{4}", spectator.getName()));
      }

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
    var location = Settings.IMP.MAIN.RETURN_TO_OLD_LOCATION ? spec.oldLocation() : new Location(Optional.ofNullable(Bukkit.getWorld(Settings.IMP.MAIN.TELEPORT_WORLD_NAME))
        .orElseGet(() -> {
          MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.WORLD_NOT_FOUND);
          return spectator.getWorld();
        }), x, y, z);
    var suspect = spec.suspect();
    var durationMillis = System.currentTimeMillis() - spec.timestamp();
    var totalSeconds = durationMillis / 1000;
    var totalMinutes = durationMillis / (1000 * 60);
    var totalHours = durationMillis / (1000 * 60 * 60);
    var seconds = (totalSeconds) % 60;
    var minutes = (totalMinutes) % 60;
    var hours = (totalHours) % 24;

    var duration = MessageFormat.format(Settings.IMP.MAIN.DURATION_FORMAT,
        hours,
        minutes,
        seconds,
        totalHours,
        totalMinutes,
        totalSeconds
    );

    if (Settings.IMP.MAIN.NOTIFY) {
      Bukkit.getOnlinePlayers().stream()
          .filter(player -> player.hasPermission("xspec.notify"))
          .filter(player -> player != spectator)
          .forEach(player ->
              MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.STOPPED_NOTIFY, spectator.getName(), suspect.getName(), DateFormatUtil.getFormattedDate(), duration)
          );
    }

    if (Settings.IMP.MAIN.TELEPORT_AFTER_STOP) {
      spectator.teleport(location);
    }

    spectator.hideBossBar(spec.specBar().getBossBar());

    ProtocolLibUtil.showDebugInfo(spectator);
    ProtocolLibUtil.removeGlowingRelation(suspect, spectator);
    ProtocolLibUtil.removeNightVision(spectator);

    ((SpecLoggerImpl) spec.logger()).stop();

    this.specs.remove(spectator);

    var gamemode = Settings.IMP.MAIN.RETURN_TO_OLD_GAMEMODE ? spec.oldGameMode() : Arrays.stream(GameMode.values())
        .filter(gameMode -> gameMode.name().equalsIgnoreCase(Settings.IMP.MAIN.STOP_GAMEMODE))
        .findFirst()
        .orElseGet(() -> {
          SpecPlugin.getInstance().getLogger().severe("Unknown gamemode: " + Settings.IMP.MAIN.STOP_GAMEMODE + ". Defaulting to SURVIVAL");
          return GameMode.SURVIVAL;
        });

    spectator.setGameMode(gamemode);

    var webhook = Settings.IMP.MAIN.WEBHOOK;

    if (!webhook.isEmpty()) {
      WebhookUtil.sendWebhookAsync(webhook, Settings.IMP.MAIN.MESSAGES.STOPPED_WEBHOOK
          .replace("{0}", suspect.getName())
          .replace("{1}", DateFormatUtil.getFormattedDate())
          .replace("{2}", duration)
          .replace("{4}", spectator.getName()));
    }
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