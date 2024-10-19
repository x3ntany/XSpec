package me.xentany.xspec.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import me.xentany.xspec.SpecPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class ProtocolLibUtil {

  private static final Map<UUID, Set<UUID>> GLOWING_RELATIONS = new HashMap<>();
  private static final ThreadLocal<Boolean> PACKET_SENDING = ThreadLocal.withInitial(() -> false);
  private static ProtocolManager protocolManager;
  private static PacketAdapter glowingPacketListener;
  private static boolean isProtocolLibAvailable = false;

  public static void load() {
    var plugin = SpecPlugin.getInstance();

    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
      protocolManager = ProtocolLibrary.getProtocolManager();
      isProtocolLibAvailable = true;

      protocolManager.addPacketListener(glowingPacketListener = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {

        @Override
        public void onPacketSending(PacketEvent event) {
          if (event.getPlayer() == null || event.getPacket() == null) {
            return;
          }

          if (PACKET_SENDING.get()) {
            return;
          }

          var target = event.getPlayer();
          var viewers = getViewers(target);

          if (!viewers.isEmpty()) {
            sendGlowingPacket(target, viewers, true);
          }
        }
      });
    } else {
      plugin.getLogger().warning("ProtocolLib not found. DebugInfoUtil will not function");
    }
  }

  public static void shutdown() {
    if (isProtocolLibAvailable && glowingPacketListener != null) {
      protocolManager.removePacketListener(glowingPacketListener);
      glowingPacketListener = null;
    }
  }

  public static void addGlowingRelation(final @NotNull Player target, final @NotNull Player viewer) {
    if (isProtocolLibAvailable) {
      GLOWING_RELATIONS.computeIfAbsent(target.getUniqueId(), k -> new HashSet<>()).add(viewer.getUniqueId());
      sendGlowingPacket(target, Collections.singletonList(viewer), true);
    }
  }

  public static void removeGlowingRelation(final @NotNull Player target, final @NotNull Player viewer) {
    if (isProtocolLibAvailable) {
      var viewers = GLOWING_RELATIONS.get(target.getUniqueId());

      if (viewers != null) {
        viewers.remove(viewer.getUniqueId());

        if (viewers.isEmpty()) {
          GLOWING_RELATIONS.remove(target.getUniqueId());
        }

        sendGlowingPacket(target, Collections.singletonList(viewer), false);
      }
    }
  }

  public static void sendGlowingPacket(final @NotNull Player target,
                                       final @NotNull Collection<? extends Player> viewers,
                                       final boolean glowing) {
    if (PACKET_SENDING.get()) {
      return;
    }


    PACKET_SENDING.set(true);

    try {
      var packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

      packet.getIntegers().write(0, target.getEntityId());

      var watcher = new WrappedDataWatcher();
      var flagsObject = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
      int index = flagsObject.getIndex();
      var flagObject = watcher.getByte(index);
      byte flags = (flagObject != null) ? flagObject : 0;

      if (glowing) {
        flags |= 64;
      } else {
        flags &= -65;
      }

      watcher.setObject(flagsObject, flags);
      packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

      viewers.forEach(viewer -> {
        if (!viewer.equals(target)) {
          protocolManager.sendServerPacket(viewer, packet.deepClone());
        }
      });
    } finally {
      PACKET_SENDING.set(false);
    }
  }

  private static void sendReducedDebugInfoPacket(final @NotNull Player player, final boolean hideDebugInfo) {
    if (isProtocolLibAvailable) {
      var packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);

      packet.getIntegers().write(0, player.getEntityId());
      packet.getBytes().write(0, (byte) (hideDebugInfo ? 22 : 23));

      protocolManager.sendServerPacket(player, packet);
    }
  }

  public static @NotNull Set<Player> getViewers(final @NotNull Player target) {
    var viewerUUIDs = GLOWING_RELATIONS.get(target.getUniqueId());

    if (viewerUUIDs == null) {
      return Collections.emptySet();
    }

    return viewerUUIDs.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  public static void hideDebugInfo(final @NotNull Player player) {
    if (isProtocolLibAvailable) {
      sendReducedDebugInfoPacket(player, true);
    }
  }

  public static void showDebugInfo(final @NotNull Player player) {
    if (isProtocolLibAvailable) {
      sendReducedDebugInfoPacket(player, false);
    }
  }

  public static void removeAllGlowingRelations(final @NotNull Player player) {
    if (isProtocolLibAvailable) {
      var playerUniqueId = player.getUniqueId();
      var viewers = GLOWING_RELATIONS.remove(playerUniqueId);

      if (viewers != null && !viewers.isEmpty()) {
        sendGlowingPacket(player, getPlayersFromUUIDs(viewers), false);
      }

      new HashMap<>(GLOWING_RELATIONS).entrySet().stream()
          .filter(entry -> entry.getValue().remove(playerUniqueId))
          .forEach(entry -> {
            var target = Bukkit.getPlayer(entry.getKey());

            if (target != null) {
              sendGlowingPacket(target, Collections.singletonList(player), false);
            }

            if (entry.getValue().isEmpty()) {
              GLOWING_RELATIONS.remove(entry.getKey());
            }
          });
    }
  }

  private static Collection<Player> getPlayersFromUUIDs(final @NotNull Collection<UUID> uuids) {
    return uuids.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static boolean isGlowingFor(final @NotNull Player target, final @NotNull Player viewer) {
    var viewers = GLOWING_RELATIONS.get(target.getUniqueId());
    return viewers != null && viewers.contains(viewer.getUniqueId());
  }
}