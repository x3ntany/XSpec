package me.xentany.xspec.api;

import me.xentany.xspec.spec.SpecImpl;
import me.xentany.xspec.spec.SpecLoggerImpl;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Spec {

  Player spectator();
  Player suspect();
  SpecLogger logger();

  @Contract(value = " -> new", pure = true)
  static @NotNull Builder builder() {
    return new Builder();
  }

  class Builder {

    private Player spectator;
    private Player suspect;

    public @NotNull Builder spectator(final @NotNull Player spectator) {
      this.spectator = spectator;
      return this;
    }

    public @NotNull Builder suspect(final @NotNull Player suspect) {
      this.suspect = suspect;
      return this;
    }

    public @NotNull Spec build() {
      this.validate();
      return new SpecImpl(this.spectator, this.suspect, new SpecLoggerImpl(this.spectator));
    }

    private void validate() {
      Validate.notNull(this.spectator, "Spectator must be set before building");
      Validate.notNull(this.suspect, "Suspect must be set before building");
    }
  }
}