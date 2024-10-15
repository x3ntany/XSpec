package me.xentany.xspec.api;

import me.xentany.xspec.spec.SpecImpl;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;

public interface Spec {

  Player spectator();
  Player suspect();
  long timestamp();

  @Contract(value = " -> new", pure = true)
  static @NonNull Builder builder() {
    return new Builder();
  }

  class Builder {

    private @MonotonicNonNull Player spectator;
    private @MonotonicNonNull Player suspect;
    private long timestamp;

    public @NonNull Builder spectator(final @NonNull Player spectator) {
      this.spectator = spectator;
      return this;
    }

    public @NonNull Builder suspect(final @NonNull Player suspect) {
      this.suspect = suspect;
      return this;
    }

    public @NonNull Builder timestamp(final long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public @NonNull Spec build() {
      this.validate();
      return new SpecImpl(this.spectator, this.suspect, this.timestamp);
    }

    private void validate() {
      Validate.notNull(this.spectator, "Spectator must be set before building");
      Validate.notNull(this.suspect, "Suspect must be set before building");
      Validate.isTrue(this.timestamp > 0, "Timestamp must be a positive value");
    }
  }
}