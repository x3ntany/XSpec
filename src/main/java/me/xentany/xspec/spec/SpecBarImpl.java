package me.xentany.xspec.spec;

import me.xentany.xspec.api.SpecBar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public final class SpecBarImpl implements SpecBar {

  private final Component name;
  private final BossBar.Color color;
  private final BossBar.Overlay overlay;
  private final BossBar bossBar;

  public SpecBarImpl(final Component name, final BossBar.Color color, final BossBar.Overlay overlay) {
    this.name = name;
    this.color = color;
    this.overlay = overlay;
    this.bossBar = BossBar.bossBar(name, 1.0f, color, overlay);
  }

  @Override
  public Component name() {
    return this.name;
  }

  @Override
  public BossBar.Color color() {
    return this.color;
  }

  @Override
  public BossBar.Overlay overlay() {
    return this.overlay;
  }

  @Override
  public BossBar bossBar() {
    return this.bossBar;
  }
}