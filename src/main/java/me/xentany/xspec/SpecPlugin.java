package me.xentany.xspec;

import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.spec.command.SpecCommand;
import me.xentany.xspec.spec.listener.SpecHandler;
import me.xentany.xspec.spec.SpecManagerImpl;
import me.xentany.xspec.util.DateFormatUtil;
import me.xentany.xspec.util.DebugInfoUtil;
import me.xentany.xspec.util.MessageUtil;
import me.xentany.xspec.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class SpecPlugin extends JavaPlugin {

  private static SpecPlugin instance;
  private SpecManager specManager;

  //webhook check
  @Override
  public void onEnable() {
    SpecPlugin.instance = this;
    Settings.IMP.reload(this.getDataFolder().toPath().resolve("config.yml").toFile());

    DateFormatUtil.load();
    DebugInfoUtil.load();
    MessageUtil.load();

    this.specManager = new SpecManagerImpl();

    Optional.ofNullable(this.getCommand("spec"))
        .ifPresent(command -> command.setExecutor(new SpecCommand()));
    Bukkit.getPluginManager().registerEvents(new SpecHandler(), this);

    if (Settings.IMP.MAIN.BSTATS) {
      new Metrics(this, 23644);
    }
  }

  @Override
  public void onDisable() {
    this.specManager.stopAll();
  }

  public static SpecPlugin getInstance() {
    return SpecPlugin.instance;
  }

  public SpecManager getSpecManager() {
    return this.specManager;
  }
}