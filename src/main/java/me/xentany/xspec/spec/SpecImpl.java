package me.xentany.xspec.spec;

import me.xentany.xspec.api.Spec;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SpecImpl(@NotNull Player spectator,
                       @NotNull Player suspect,
                       @NotNull SpecLoggerImpl logger) implements Spec {}