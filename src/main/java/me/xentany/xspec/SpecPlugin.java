package me.xentany.xspec;

import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.spec.command.SpecCommand;
import me.xentany.xspec.spec.listener.SpecHandler;
import me.xentany.xspec.spec.SpecManagerImpl;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Optional;

@DefaultQualifier(NonNull.class)
public final class SpecPlugin extends JavaPlugin {

  private static @MonotonicNonNull SpecPlugin instance;
  private @MonotonicNonNull SpecManager specManager;

  @Override
  public void onEnable() {
    SpecPlugin.instance = this;
    Settings.IMP.reload(this.getDataFolder().toPath().resolve("config.yml").toFile());

    this.specManager = new SpecManagerImpl();

    Optional.ofNullable(this.getCommand("spec"))
        .ifPresent(command -> command.setExecutor(new SpecCommand()));
    Bukkit.getPluginManager().registerEvents(new SpecHandler(), this);
  }
  
  @Override
  public void onDisable() {
    this.specManager.stopAll();
  }

  public static SpecPlugin getInstance() {
    Validate.notNull(SpecPlugin.instance, "SpecPlugin has not been initialized yet");
    return SpecPlugin.instance;
  }

  public SpecManager getSpecManager() {
    Validate.notNull(this.specManager, "SpecManager has not been initialized yet");
    return this.specManager;
  }
}