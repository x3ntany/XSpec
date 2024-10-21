package me.xentany.xspec.api;

import me.xentany.xspec.Settings;
import me.xentany.xspec.spec.SpecBarImpl;
import me.xentany.xspec.spec.SpecImpl;
import me.xentany.xspec.spec.SpecLoggerImpl;
import me.xentany.xspec.util.MessageUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused") //api lol
public interface Spec {

  Player spectator();
  Player suspect();
  SpecLogger logger();
  SpecBar specBar();

  // @Deprecated(since = "1.0.0", forRemoval = true)
  // @Contract(" -> fail")
  // static @NotNull Spec.Builder builder() {
  //   throw new UnsupportedOperationException("This builder method is no longer supported. " +
  //       "Use builder(Player spectator, Player suspect) instead");
  // }
  // This method was removed in version 1.1.0.

  @Contract("_, _ -> new")
  static @NotNull Spec.Builder builder(final @NotNull Player spectator,
                                       final @NotNull Player suspect) {
    return new Spec.Builder(spectator, suspect);
  }

  class Builder {

    private final Player spectator;
    private final Player suspect;
    private final SpecLogger logger;
    private SpecBar specBar;

    public Builder(final @NotNull Player spectator, final @NotNull Player suspect) {
      this.spectator = spectator;
      this.suspect = suspect;
      this.logger = new SpecLoggerImpl(spectator, suspect);
      this.specBar = new SpecBarImpl(MessageUtil.getFormattedComponent(Settings.IMP.MAIN.BAR_NAME, suspect.getName()), Settings.IMP.MAIN.BAR_COLOR, Settings.IMP.MAIN.BAR_OVERLAY);
    }

    public Builder specBar(final @NotNull SpecBar specBar) {
      this.specBar = specBar;
      return this;
    }

    public Builder reason(final @Nullable String reason) {
      if (reason != null) {
        this.logger.log("Reason: " + reason);
      }

      return this;
    }

    public @NotNull Spec build() {
      return new SpecImpl(
          this.spectator,
          this.suspect,
          this.logger,
          this.specBar
      );
    }
  }
}