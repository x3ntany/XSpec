package me.xentany.xspec.api;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Optional;

public interface SpecManager {

  boolean isInSpec(final @NonNull Player player);
  boolean isSpectator(final @NonNull Player spectator);
  boolean tryStart(final @NonNull Spec spec);
  void stop(final @NonNull Spec spec);
  void stopAll();
  Optional<Spec> findSpec(final @NonNull Player player);
  @NonNull @UnmodifiableView Map<Player, Spec> getSpecs();
}