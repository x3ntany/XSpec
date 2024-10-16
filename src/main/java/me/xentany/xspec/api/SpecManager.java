package me.xentany.xspec.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused") //api lol
public interface SpecManager {

  boolean isInSpec(final @NotNull Player player);
  boolean isSpectator(final @NotNull Player spectator);
  boolean tryStart(final @NotNull Spec spec);
  void stop(final @NotNull Spec spec);
  void stopAll();
  Optional<Spec> findSpec(final @NotNull Player player);
  Optional<Spec> resolveSpec(final @NotNull Player spectator);
  @NotNull @UnmodifiableView Map<Player, Spec> getSpecs();
}