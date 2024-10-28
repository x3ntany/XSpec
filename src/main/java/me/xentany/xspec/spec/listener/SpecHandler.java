package me.xentany.xspec.spec.listener;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.util.DateFormatUtil;
import me.xentany.xspec.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class SpecHandler implements Listener {

  private final SpecPlugin plugin;
  private final SpecManager specManager;

  public SpecHandler() {
    this.plugin = SpecPlugin.getInstance();
    this.specManager = plugin.getSpecManager();
  }

  @EventHandler
  public void on(final @NotNull PlayerQuitEvent event) {
    this.onLogout(event.getPlayer());
  }

  @EventHandler
  public void on(final @NotNull PlayerKickEvent event) {
    this.onLogout(event.getPlayer());
  }

  @EventHandler
  public void on(final @NotNull PlayerInteractEvent event) {
    if (this.specManager.isSpectator(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void on(final @NotNull PlayerCommandPreprocessEvent event) {
    var player = event.getPlayer();

    this.specManager.resolveSpec(player)
        .ifPresent(spec -> {
          var message = event.getMessage();

          spec.logger().logCommand(message);

          if (Settings.IMP.MAIN.BLOCKED_COMMANDS.contains(message.split(" ")[0].toLowerCase(Locale.ROOT))) {
            event.setCancelled(true);

            MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.COMMAND_BLOCKED);
          }
        });
  }

  @EventHandler
  public void on(@SuppressWarnings("deprecation") final @NotNull AsyncPlayerChatEvent event) {
    this.specManager.resolveSpec(event.getPlayer())
        .ifPresent(spec -> spec.logger().logChat(event.getMessage()));
  }

  @EventHandler
  public void on(final @NotNull PlayerGameModeChangeEvent event) {
    var player = event.getPlayer();
    this.specManager.resolveSpec(player).ifPresent(spec -> {
      event.setCancelled(true);

      if (Settings.IMP.MAIN.OFF_SPEC_ON_GAMEMODE_CHANGE) {
        this.specManager.stop(spec);
        MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.STOPPED_BY_GAMEMODE_CHANGE, DateFormatUtil.getFormattedDate(), spec.suspect().getName());
      } else {
        MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.CANNOT_CHANGE_GAMEMODE);
      }
    });
  }

  @EventHandler
  public void on(final @NotNull PlayerJoinEvent event) {
    if (Settings.IMP.MAIN.CHECK_FOR_UPDATES) {
      var player = event.getPlayer();

      if (player.isOp() || player.hasPermission("*")) {
        this.plugin.getLaterVersionFetcher().resolve(true)
            .thenAccept(optionalVersion -> optionalVersion.ifPresent(latestVersion -> {
              if (!latestVersion.equals(this.plugin.getDescription().getVersion())) {
                player.sendMessage(ChatColor.RED +
                        """
                        A new version of XSpec is available!
                        download: https://github.com/x3ntany/XSpec/releases/
                        """
                );
              }
            }));
      }
    }
  }

  private void onLogout(final @NotNull Player player) {
    this.specManager.findSpec(player).ifPresent(spec -> {
      this.specManager.stop(spec);

      MessageUtil.formatAndSendIfNotEmpty(spec.spectator(),
          Settings.IMP.MAIN.MESSAGES.SUSPECT_LEFT,
          spec.suspect().getName(),
          DateFormatUtil.getFormattedDate()
      );
    });
  } 
}