package me.xentany.xspec.spec;

import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecBar;
import me.xentany.xspec.api.SpecLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SpecImpl(@NotNull Player spectator,
                       @NotNull Player suspect,
                       @NotNull SpecLogger logger,
                       @NotNull SpecBar specBar) implements Spec {}