package me.xentany.xspec.spec.listener;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Locale;

@DefaultQualifier(NonNull.class)
public final class SpecHandler implements Listener {

  private final SpecManager specManager;

  public SpecHandler() {
    this.specManager = SpecPlugin.getInstance().getSpecManager();
  }

  @EventHandler
  public void on(final PlayerQuitEvent event) {
    this.onLogout(event.getPlayer());
  }

  @EventHandler
  public void on(final PlayerKickEvent event) {
    this.onLogout(event.getPlayer());
  }

  @EventHandler
  public void on(final PlayerInteractEvent event) {
    if (this.specManager.isSpectator(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void on(final PlayerCommandPreprocessEvent event) {
    var player = event.getPlayer();

    if (this.specManager.isSpectator(player) &&
        Settings.IMP.MAIN.BLOCKED_COMMANDS.contains(event.getMessage().split(" ")[0].toLowerCase(Locale.ROOT))) {
      event.setCancelled(true);
      MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.COMMAND_BLOCKED);
    }
  }

  //todo gamemode change lol
  private void onLogout(final @NonNull Player player) {
    this.specManager.findSpec(player).ifPresent(spec -> {
      this.specManager.stop(spec);

      if (this.specManager.isSpectator(player)) {
        MessageUtil.formatAndSendIfNotEmpty(player, Settings.IMP.MAIN.MESSAGES.SUSPECT_LEFT);
      }
    });
  }
}