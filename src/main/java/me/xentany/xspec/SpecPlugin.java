package me.xentany.xspec;

import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.spec.command.SpecCommand;
import me.xentany.xspec.spec.listener.SpecHandler;
import me.xentany.xspec.spec.SpecManagerImpl;
import me.xentany.xspec.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class SpecPlugin extends JavaPlugin {

  private static SpecPlugin instance;
  private SpecManager specManager;
  private LatestVersionFetcher laterVersionFetcher;

  @Override
  public void onEnable() {
    SpecPlugin.instance = this;
    Settings.IMP.reload(this.getDataFolder().toPath().resolve("config.yml").toFile());

    DateFormatUtil.load();
    MessageUtil.load();
    ProtocolLibUtil.load();
    WebhookUtil.load();

    this.specManager = new SpecManagerImpl();

    Optional.ofNullable(this.getCommand("spec"))
        .ifPresent(command -> command.setExecutor(new SpecCommand()));
    Bukkit.getPluginManager().registerEvents(new SpecHandler(), this);

    if (Settings.IMP.MAIN.BSTATS) {
      var metrics = new Metrics(this, 23644);

      metrics.addCustomChart(new Metrics.SimplePie("update_check_enabled", () ->
          Settings.IMP.MAIN.CHECK_FOR_UPDATES ? "enabled" : "disabled")
      );
    }

    this.laterVersionFetcher = new LatestVersionFetcher("X-Bukkit-Community", "XSpec");
    this.startCheckingForUpdates();
  }

  @Override
  public void onDisable() {
    this.specManager.stopAll();
    ProtocolLibUtil.shutdown();
  }

  private void startCheckingForUpdates() {
    if (Settings.IMP.MAIN.CHECK_FOR_UPDATES) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
        getLogger().info("Checking for updates...");

        this.laterVersionFetcher.resolve(false)
            .thenAccept(optionalVersion -> optionalVersion.ifPresentOrElse(latestVersion ->
                getLogger().info(!latestVersion.equals(getDescription().getVersion())
                    ? "New version is available: " + latestVersion
                    : "You're using the latest version"),
                () -> getLogger().info("Couldn't retrieve the latest version"))
            )
            .exceptionally(e -> {
              getLogger().severe("Error while checking updates: " + e.getMessage());
              return null;
            });
      }, 0L, 288000L);
    }
  }

  public static SpecPlugin getInstance() {
    return SpecPlugin.instance;
  }

  public SpecManager getSpecManager() {
    return this.specManager;
  }

  public LatestVersionFetcher getLaterVersionFetcher() {
    return this.laterVersionFetcher;
  }
}