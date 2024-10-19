package me.xentany.xspec.api;

import me.xentany.xspec.spec.SpecBarImpl;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SpecBar {

  Component name();
  BossBar.Color color();
  BossBar.Overlay overlay();
  BossBar bossBar();

  @Contract("_, _, _ -> new")
  static @NotNull SpecBar of(final @NotNull Component name,
                             final @NotNull BossBar.Color color,
                             final @NotNull BossBar.Overlay overlay) {
    return new SpecBarImpl(name, color, overlay);
  }
}