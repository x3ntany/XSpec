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
  public Component getName() {
    return this.name;
  }

  @Override
  public BossBar.Color getColor() {
    return this.color;
  }

  @Override
  public BossBar.Overlay getOverlay() {
    return this.overlay;
  }

  @Override
  public BossBar getBossBar() {
    return this.bossBar;
  }
}