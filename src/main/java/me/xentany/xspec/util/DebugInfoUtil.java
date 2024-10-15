package me.xentany.xspec.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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

  private static void sendReducedDebugInfoPacket(final @NonNull Player player,
                                                 final boolean hideDebugInfo) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      var packet = DebugInfoUtil.protocolManager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);

      packet.getIntegers().write(0, 3);
      packet.getFloat().write(1, hideDebugInfo ? 1.0f : 0.0f);

      DebugInfoUtil.protocolManager.sendServerPacket(player, packet);
    }
  }
}