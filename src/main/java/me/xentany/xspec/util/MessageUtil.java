package me.xentany.xspec.util;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import net.elytrium.commons.kyori.serialization.Serializer;
import net.elytrium.commons.kyori.serialization.Serializers;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Objects;

public final class MessageUtil {

  private static Serializer serializer;

  public static void load() {
    MessageUtil.serializer = new Serializer(Objects.requireNonNullElseGet(
        Settings.IMP.SERIALIZER.getSerializer(),
        () -> {
          SpecPlugin.getInstance().getLogger().warning("Serializer not found. Defaulting to: LEGACY_AMPERSAND");
          return Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer());
        })
    );
  }

  public static void formatAndSendIfNotEmpty(final @NotNull CommandSender sender,
                                             final @NotNull String message,
                                             final Object @NotNull ... args) {
    if (!message.isEmpty()) {
      sender.sendMessage(MessageUtil.getFormattedComponent(message, args));
    }
  }

  public static @NotNull Component getFormattedComponent(final @NotNull String message,
                                                         final Object @NotNull ... args) {
    return MessageUtil.serializer.deserialize(MessageFormat.format(message, args));
  }
}