package me.xentany.xspec.util;

import me.xentany.xspec.Settings;
import net.elytrium.commons.kyori.serialization.Serializer;
import net.elytrium.commons.kyori.serialization.Serializers;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.MessageFormat;
import java.util.Objects;

public final class MessageUtil {

  private static final Serializer SERIALIZER;

  static {
    SERIALIZER = new Serializer(Objects.requireNonNullElseGet(Settings.IMP.SERIALIZER.getSerializer(), () ->
        Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer()))
    );
  }

  public static void formatAndSendIfNotEmpty(final @NonNull CommandSender sender,
                                             final @NonNull String message,
                                             final Object @NonNull ... args) {
    if (!message.isEmpty()) {
      sender.sendMessage(MessageUtil.getFormattedComponent(message, args));
    }
  }

  public static @NonNull Component getFormattedComponent(final @NonNull String message,
                                                         final Object @NonNull ... args) {
    return MessageUtil.SERIALIZER.deserialize(MessageFormat.format(message, args));
  }
}