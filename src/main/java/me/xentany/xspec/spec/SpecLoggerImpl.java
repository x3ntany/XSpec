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
  private final Player player;
  private final String startTime;
  private final Path logFilePath;
  private BufferedWriter writer;

  public SpecLoggerImpl(final Player player) {
    this.plugin = SpecPlugin.getInstance();
    this.logger = plugin.getLogger();
    this.player = player;
    this.startTime = DateFormatUtil.getFormattedDate();
    this.logFilePath = this.createLogFile(player);

    try {
      this.writer = Files.newBufferedWriter(this.logFilePath);
      this.log("Started spectating at: " + startTime);
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to initialize log file for " + player.getName(), e);
    }
  }

  private Path createLogFile(final @NotNull Player player) {
    var logsDir = this.plugin.getDataFolder().toPath().resolve(Paths.get("logs", player.getName()));

    try {
      if (Files.notExists(logsDir)) {
        Files.createDirectories(logsDir);
      }
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to create log directory for " + player.getName(), e);
    }

    return getUniqueFilePath(
        logsDir.resolve(this.startTime.replace(":", "-") + ".log")
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
      this.logger.log(Level.SEVERE, "Failed to write to log file for " + player.getName(), e);
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
    if (player.isOnline()) {
      var location = player.getLocation();
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
      this.logger.log(Level.SEVERE, "Failed to close log file for " + player.getName(), e);
    }

    try {
      var endTime = DateFormatUtil.getFormattedDate().replace(":", "-");
      var newFilePath = logFilePath.getParent().resolve(startTime.replace(":", "-") + " - " + endTime + ".log");

      newFilePath = getUniqueFilePath(newFilePath);

      Files.move(logFilePath, newFilePath);
    } catch (final IOException e) {
      this.logger.log(Level.SEVERE, "Failed to rename log file for " + player.getName(), e);
    }
  }
}