package me.xentany.xspec.spec;

import me.xentany.xspec.api.Spec;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public record SpecImpl(@NonNull Player spectator,
                       @NonNull Player suspect,
                       long timestamp) implements Spec {}