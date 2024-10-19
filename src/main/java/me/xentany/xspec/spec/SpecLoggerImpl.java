package me.xentany.xspec.spec;

import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.SpecLogger;
import me.xentany.xspec.util.DateFormatUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public final class SpecLoggerImpl implements SpecLogger {

  private final SpecPlugin plugin;
  private final Logger logger;
  private final Player spectator;
  private final Player suspect;
  private final String startTime;
  private final Path logFilePath;
  private BufferedWriter writer;

  public SpecLoggerImpl(final @NotNull Player spectator, @NotNull final Player suspect) {
    this.plugin = SpecPlugin.getInstance();
    this.logger = plugin.getLogger();
    this.spectator = spectator;
    this.suspect = suspect;
    this.startTime = DateFormatUtil.getFormattedDate();
    this.logFilePath = this.createLogFile(spectator, suspect);

    try {
      this.writer = Files.newBufferedWriter(this.logFilePath);
      this.log("Started spectating at: " + startTime);
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to initialize log file for " + spectator.getName(), e);
    }
  }

  private Path createLogFile(final @NotNull Player spectator, final @NotNull Player suspect) {
    var logsDir = this.plugin.getDataFolder().toPath().resolve(Paths.get("logs", spectator.getName()));

    try {
      if (Files.notExists(logsDir)) {
        Files.createDirectories(logsDir);
      }
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to create log directory for " + spectator.getName(), e);
    }

    return getUniqueFilePath(
        logsDir.resolve(this.startTime.replace(":", "-") + "-" + suspect.getName() + ".log")
    );
  }

  private Path getUniqueFilePath(final Path path) {
    if (Files.exists(path)) {
      return IntStream.range(1, Integer.MAX_VALUE)
          .mapToObj(i -> path.getParent().resolve(path.getFileName().toString().replace(".log", "-" + i + ".log")))
          .filter(Files::notExists)
          .findFirst()
          .orElse(path);
    }

    return path;
  }

  @Override
  public synchronized void log(final @NotNull String message) {
    try {
      var timestamp = DateFormatUtil.getFormattedDate();
      this.writer.write("[" + timestamp + "] " + message);
      this.writer.newLine();
      this.writer.flush();
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to write to log file for " + spectator.getName(), e);
    }
  }
  @Override
  public void logCommand(final @NotNull String command) {
    this.log("Command used: " + command);
  }

  @Override
  public void logChat(final @NotNull String message) {
    this.log("Chat message: " + message);
  }

  @Override
  public void logLocation() {
    if (spectator.isOnline()) {
      var location = spectator.getLocation();
      this.log(
          String.format("Location: X: %.2f, Y: %.2f, Z: %.2f, World: %s",
              location.getX(),
              location.getY(),
              location.getZ(),
              location.getWorld().getName()
          )
      );
    }
  }

  public synchronized void stop() {
    try {
      this.log("Stopped spectating at: " + DateFormatUtil.getFormattedDate());

      if (this.writer != null) {
        this.writer.close();
      }
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to close log file for " + spectator.getName(), e);
    }

    try {
      var endTime = DateFormatUtil.getFormattedDate().replace(":", "-");
      var newFilePath = logFilePath.getParent().resolve(
          startTime.replace(":", "-") + " - " + endTime + " - " + suspect.getName() + ".log"
      );

      newFilePath = getUniqueFilePath(newFilePath);

      Files.move(logFilePath, newFilePath);
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to rename log file for " + spectator.getName(), e);
    }
  }
}