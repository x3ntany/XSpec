package me.xentany.xspec.spec;

import me.xentany.xspec.api.SpecManager;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SpecManagerImpl implements SpecManager {

  private final @NonNull Map<Player, SpecImpl> specs;

  public SpecManagerImpl() {
    this.specs = new ConcurrentHashMap<>();
  }

  @Contract(pure = true)
  public @NonNull @UnmodifiableView Map<Player, SpecImpl> getSpecs() {
    return Collections.unmodifiableMap(this.specs);
  }

  @Override
  public boolean isInSpec(final @NonNull Player player) {
    return this.specs.containsKey(player) ||
        this.specs.values().stream()
            .anyMatch(spec -> spec.suspect().equals(player));
  }

  @Override
  public boolean isSpectating(final @NonNull Player player) {
    return this.specs.containsKey(player);
  }
}