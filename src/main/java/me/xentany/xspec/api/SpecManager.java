package me.xentany.xspec.api;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SpecManager {

  boolean isInSpec(final @NonNull Player player);
  boolean isSpectating(final @NonNull Player player);
}