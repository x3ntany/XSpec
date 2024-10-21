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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public final class ProtocolLibUtil {

  private static final ConcurrentMap<UUID, Set<UUID>> GLOWING_RELATIONS;
  private static final ConcurrentMap<UUID, WrappedDataWatcher> WATCHERS;

  static {
    GLOWING_RELATIONS = new ConcurrentHashMap<>();
    WATCHERS = new ConcurrentHashMap<>();
  }

  private static ProtocolManager protocolManager;
  private static PacketAdapter glowingPacketListener;
  private static boolean isProtocolLibAvailable = false;

  public static void load() {
    var plugin = SpecPlugin.getInstance();

    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
      ProtocolLibUtil.protocolManager = ProtocolLibrary.getProtocolManager();
      ProtocolLibUtil.isProtocolLibAvailable = true;
      ProtocolLibUtil.glowingPacketListener = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {

        @Override
        public void onPacketSending(final PacketEvent event) {
          var target = event.getPlayer();

          if (target != null && isGlowingForAny(target)) {
            var viewers = getViewers(target);

            if (!viewers.isEmpty()) {
              ProtocolLibUtil.sendGlowingPacket(target, viewers, true);
            }
          }
        }
      };

      ProtocolLibUtil.protocolManager.addPacketListener(ProtocolLibUtil.glowingPacketListener);
    } else {
      plugin.getLogger().warning("ProtocolLib not found. ProtocolLibUtil will not function");
    }
  }

  private static void sendReducedDebugInfoPacket(final @NotNull Player player, final boolean hideDebugInfo) {
    if (ProtocolLibUtil.isProtocolLibAvailable) {
      var packet = ProtocolLibUtil.protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);

      packet.getIntegers().write(0, player.getEntityId());
      packet.getBytes().write(0, hideDebugInfo ? (byte) 22 : (byte) 23);

      ProtocolLibUtil.protocolManager.sendServerPacket(player, packet);
    }
  }

  public static void hideDebugInfo(final @NotNull Player player) {
    ProtocolLibUtil.sendReducedDebugInfoPacket(player, true);
  }

  public static void showDebugInfo(final @NotNull Player player) {
    ProtocolLibUtil.sendReducedDebugInfoPacket(player, false);
  }

  public static void addGlowingRelation(final @NotNull Player target, final @NotNull Player viewer) {
    if (ProtocolLibUtil.isProtocolLibAvailable) {
      ProtocolLibUtil.GLOWING_RELATIONS.computeIfAbsent(target.getUniqueId(), k ->
          new CopyOnWriteArraySet<>()).add(viewer.getUniqueId()
      );
      ProtocolLibUtil.sendGlowingPacket(target, Set.of(viewer), true);
    }
  }

  public static void removeGlowingRelation(final @NotNull Player target, final @NotNull Player viewer) {
    if (ProtocolLibUtil.isProtocolLibAvailable) {
      var viewers = ProtocolLibUtil.GLOWING_RELATIONS.get(target.getUniqueId());

      if (viewers != null && viewers.remove(viewer.getUniqueId())) {
        if (viewers.isEmpty()) {
          ProtocolLibUtil.GLOWING_RELATIONS.remove(target.getUniqueId());
          ProtocolLibUtil.WATCHERS.remove(target.getUniqueId());
        }

        ProtocolLibUtil.sendGlowingPacket(target, Set.of(viewer), false);
      }
    }
  }

  private static void sendGlowingPacket(final @NotNull Player target,
                                        final @NotNull Set<Player> viewers,
                                        final boolean glowing) {
    if (ProtocolLibUtil.isProtocolLibAvailable) {
      var watcher = ProtocolLibUtil.WATCHERS.computeIfAbsent(target.getUniqueId(), uuid ->
          new WrappedDataWatcher(target)
      );
      var flagsObject = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
      var currentFlags = watcher.getByte(0);
      byte flags = (currentFlags != null) ? currentFlags : 0;

      if (glowing) {
        flags |= 64;
      } else {
        flags &= -65;
      }

      if (currentFlags == null || currentFlags != flags) {
        watcher.setObject(flagsObject, flags);
      }

      var packet = ProtocolLibUtil.protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

      packet.getIntegers().write(0, target.getEntityId());
      packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

      viewers.forEach(viewer -> ProtocolLibUtil.protocolManager.sendServerPacket(viewer, packet));
    }
  }

  public static void shutdown() {
    if (ProtocolLibUtil.isProtocolLibAvailable &&
        ProtocolLibUtil.glowingPacketListener != null) {
      ProtocolLibUtil.protocolManager.removePacketListener(ProtocolLibUtil.glowingPacketListener);
    }
  }

  public static boolean isGlowingForAny(final @NotNull Player target) {
    var viewers = ProtocolLibUtil.GLOWING_RELATIONS.get(target.getUniqueId());
    return viewers != null && !viewers.isEmpty();
  }

  private static @NotNull Set<Player> getViewers(final @NotNull Player target) {
    var viewerUniqueIds = GLOWING_RELATIONS.get(target.getUniqueId());

    return viewerUniqueIds == null || viewerUniqueIds.isEmpty() ? Set.of() : viewerUniqueIds.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}