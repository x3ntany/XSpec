package me.xentany.xspec.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.xentany.xspec.SpecPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DebugInfoUtil {

  private static ProtocolManager protocolManager;
  private static boolean isProtocolLibAvailable = false;

  public static void load() {
    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
      DebugInfoUtil.protocolManager = ProtocolLibrary.getProtocolManager();
      DebugInfoUtil.isProtocolLibAvailable = true;
    } else {
      SpecPlugin.getInstance().getLogger().warning("ProtocolLib not found. DebugInfoUtil will not function");
    }
  }

  public static void hideDebugInfo(final @NotNull Player player) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      DebugInfoUtil.sendReducedDebugInfoPacket(player, true);
    }
  }

  public static void showDebugInfo(final @NotNull Player player) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      DebugInfoUtil.sendReducedDebugInfoPacket(player, false);
    }
  }

  private static void sendReducedDebugInfoPacket(final @NotNull Player player, final boolean hideDebugInfo) {
    if (DebugInfoUtil.isProtocolLibAvailable) {
      var packet = DebugInfoUtil.protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);

      packet.getIntegers().write(0, player.getEntityId());
      packet.getBytes().write(0, (byte) (hideDebugInfo ? 22 : 23));

      DebugInfoUtil.protocolManager.sendServerPacket(player, packet);
    }
  }
}