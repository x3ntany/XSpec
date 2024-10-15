package me.xentany.xspec.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.reflect.FieldAccessException;
import me.xentany.xspec.SpecPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class DebugInfoUtil {

  private static @MonotonicNonNull ProtocolManager protocolManager;
  private static boolean isProtocolLibAvailable = false;

  static {
    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
      protocolManager = ProtocolLibrary.getProtocolManager();
      isProtocolLibAvailable = true;
    } else {
      SpecPlugin.getInstance()
          .getLogger()
          .warning("ProtocolLib is not installed. DebugInfoUtil will not function");
    }
  }

  public static void hideDebugInfo(final @NonNull Player player) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      DebugInfoUtil.sendReducedDebugInfoPacket(player, true);
    }
  }

  public static void showDebugInfo(final @NonNull Player player) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      DebugInfoUtil.sendReducedDebugInfoPacket(player, false);
    }
  }

  private static void sendReducedDebugInfoPacket(final @NonNull Player player, final boolean hideDebugInfo) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      var packet = DebugInfoUtil.protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);

      packet.getIntegers().write(0, player.getEntityId());
      packet.getBytes().write(0, (byte) (hideDebugInfo ? 22 : 23));

      DebugInfoUtil.protocolManager.sendServerPacket(player, packet);
    }
  }
}