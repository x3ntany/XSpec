package me.xentany.xspec.api;

import org.jetbrains.annotations.NotNull;

public interface SpecLogger {

  void log(final @NotNull String message);
  void logChat(final @NotNull String message);
  void logCommand(final @NotNull String message);
  void logLocation();
}