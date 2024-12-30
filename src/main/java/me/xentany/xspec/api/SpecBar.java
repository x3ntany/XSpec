package me.xentany.xspec.api;

import me.xentany.xspec.spec.SpecBarImpl;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused") //api lol
public interface SpecBar {

  Component getName();
  BossBar.Color getColor();
  BossBar.Overlay getOverlay();
  BossBar getBossBar();

  static @NotNull SpecBar of(final @NotNull Component name,
                             final @NotNull BossBar.Color color,
                             final @NotNull BossBar.Overlay overlay) {
    return new SpecBarImpl(name, color, overlay);
  }
}