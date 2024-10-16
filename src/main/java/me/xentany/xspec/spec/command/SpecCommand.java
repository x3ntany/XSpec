package me.xentany.xspec.spec.command;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecManager;
import me.xentany.xspec.util.DateFormatUtil;
import me.xentany.xspec.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public final class SpecCommand implements CommandExecutor, TabCompleter {

  private final SpecManager specManager;

  public SpecCommand() {
    this.specManager = SpecPlugin.getInstance().getSpecManager();
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender,
                           final @NotNull Command command,
                           final @NotNull String label,
                           final @NotNull String[] args) {
    if (!(sender instanceof final Player spectator)) {
      return true;
    }

    if (args.length < 1) {
      MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.USAGE);
      return true;
    }

    switch (args[0].toLowerCase(Locale.ROOT)) {
      case "go" -> {
        if (args.length < 2) {
          MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.SPECIFY);
          return true;
        }

        Optional.ofNullable(Bukkit.getPlayer(args[1])).ifPresentOrElse(
            suspect -> {
              if (suspect == spectator) {
                MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.CANNOT_SPECTATE_SELF);
                return;
              }

              if (this.specManager.isSpectator(suspect)) {
                MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.NO_SPECTATE_SPECTATOR);
                return;
              }

              var spec = Spec.builder()
                  .spectator(spectator)
                  .suspect(suspect)
                  .build();

              if (specManager.tryStart(spec)) {
                MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.STARTED,
                    suspect.getName(),
                    DateFormatUtil.getFormattedDate()
                );
              } else {
                MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.ALREADY_STARTED);
              }
            },
            () -> MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.SUSPECT_NOT_FOUND)
        );
      }

      case "off" -> this.specManager.findSpec(spectator).ifPresentOrElse(
          spec -> {
            this.specManager.stop(spec);
            MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.STOPPED, DateFormatUtil.getFormattedDate());
          },
          () -> MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.NOT_SPECTATING)
      );

      default -> MessageUtil.formatAndSendIfNotEmpty(spectator, Settings.IMP.MAIN.MESSAGES.UNKNOWN_SUBCOMMAND);
    }

    return true;
  }

  //todo afk check
  @Contract(pure = true)
  @Override
  public @Unmodifiable @NotNull List<String> onTabComplete(final @NotNull CommandSender sender,
                                                           final @NotNull Command command,
                                                           final @NotNull String alias,
                                                           final String @NotNull [] args) {
    return !(sender instanceof Player) ? List.of() : args.length == 1 ? Stream.of("go", "off")
        .filter(subcommand -> subcommand.startsWith(args[0].toLowerCase(Locale.ROOT)))
        .toList() : args.length == 2 && args[0].equalsIgnoreCase("go") ? Bukkit.getOnlinePlayers().stream()
        .map(Player::getName)
        .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
        .toList() : List.of();
  }
}