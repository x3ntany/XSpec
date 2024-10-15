package me.xentany.xspec;

import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.spec.SpecManagerImpl;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class SpecPlugin extends JavaPlugin {

  private static @MonotonicNonNull SpecPlugin instance;
  private @MonotonicNonNull SpecManager specManager;

  @Override
  public void onEnable() {
    SpecPlugin.instance = this;

    this.specManager = new SpecManagerImpl();
  }
  
  @Override
  public void onDisable() {

  }

  public static @NonNull SpecPlugin getInstance() {
    Validate.notNull(SpecPlugin.instance, "SpecPlugin has not been initialized yet");
    return SpecPlugin.instance;
  }

  public @NonNull SpecManager getSpecManager() {
    Validate.notNull(this.specManager, "SpecManager has not been initialized yet");
    return this.specManager;
  }
}