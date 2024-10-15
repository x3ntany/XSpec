package me.xentany.xspec;

import org.bukkit.plugin.java.JavaPlugin;

public final class SpecPlugin extends JavaPlugin {

  private static SpecPlugin instance;

  @Override
  public void onEnable() {
    instance = this;
  }
  
  @Override
  public void onDisable() {

  }
  
  public static SpecPlugin getInstance() {
    return instance;
  }
}
