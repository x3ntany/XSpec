package me.xentany.xspec.spec;

import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecBar;
import me.xentany.xspec.api.SpecLogger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SpecImpl(@NotNull Player spectator,
                       @NotNull Player suspect,
                       @NotNull Location oldLocation,
                       @NotNull GameMode oldGameMode,
                       @NotNull SpecLogger logger,
                       @NotNull SpecBar specBar,
                       @NotNull String reason,
                       long timestamp) implements Spec {}