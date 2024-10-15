package me.xentany.xspec.spec;

import me.xentany.xspec.SpecPlugin;
import me.xentany.xspec.api.Spec;
import me.xentany.xspec.api.SpecManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;
import java.util.Optional;

public final class SpecCommand implements CommandExecutor {

  private final SpecManager specManager;

  public SpecCommand() {
    this.specManager = SpecPlugin.getInstance().getSpecManager();
  }

  @Override
  public boolean onCommand(final @NonNull CommandSender sender,
                           final @NonNull Command command,
                           final @NonNull String label,
                           final @NonNull String[] args) {
    if (!(sender instanceof final Player spectator)) {
      return true;
    }

    if (args.length < 1) {
      spectator.sendMessage("Используйте /spec go (игрок) или /spec off");
      return true;
    }

    switch (args[0].toLowerCase(Locale.ROOT)) {
      case "go" -> {
        if (args.length < 2) {
          spectator.sendMessage("Укажите никнейм подозреваемого");
          return true;
        }

        Optional.ofNullable(Bukkit.getPlayer(args[1]))
            .ifPresent(suspect -> {
              var spec = Spec.builder()
                  .spectator(spectator)
                  .suspect(suspect)
                  .timestamp(System.currentTimeMillis())
                  .build();
            });
      }

      case "off" -> {
        //todo хентани ебалай не забудь это сделать пжпжпжжп
      }
    }

    return true;
  }
}