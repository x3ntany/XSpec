package me.xentany.xspec;

import net.elytrium.commons.config.YamlConfig;
import net.elytrium.commons.kyori.serialization.Serializers;

import java.util.List;

public final class Settings extends YamlConfig {

  @Ignore
  public static final Settings IMP = new Settings();

  @Comment({
      "Available serializers:",
      "LEGACY_AMPERSAND - \"&c&lExample &c&9Text\".",
      "LEGACY_SECTION - \"§c§lExample §c§9Text\".",
      "MINIMESSAGE - \"<bold><red>Example</red> <blue>Text</blue></bold>\". (https://webui.adventure.kyori.net/)",
      "GSON - \"[{\"text\":\"Example\",\"bold\":true,\"color\":\"red\"},{\"text\":\" \",\"bold\":true},{\"text\":\"Text\",\"bold\":true,\"color\":\"blue\"}]\". (https://minecraft.tools/en/json_text.php/)",
      "GSON_COLOR_DOWNSAMPLING - Same as GSON, but uses downsampling."
  })
  public Serializers SERIALIZER = Serializers.LEGACY_AMPERSAND;

  @Create
  public MAIN MAIN;

  @Comment("Don't use \\n, use {NL} for new line.")
  public static class MAIN {

    @Comment("Lightweight and anonymous. Please keep enabled to support the author.")
    public boolean BSTATS = true;

    @Comment("World and coordinates where player teleports to after a stop spec.")
    public String TELEPORT_WORLD_NAME = "world";
    public double TELEPORT_X = 0.5d;
    public double TELEPORT_Y = 90.0d;
    public double TELEPORT_Z = 0.5d;

    @Comment("Maximum distance in blocks at which a spectator can fly away.")
    public double MAXIMUM_DISTANCE = 25.0d;

    @Comment("Recommend second precision for log naming.")
    public String DATE_PATTERN = "dd.MM.yyyy - HH:mm:ss";

    @Comment({
        "Available time zones:",
        "UTC - Coordinated Universal Time.",
        "Europe/Moscow - Moscow Time.",
        "America/New_York - Eastern Time (US & Canada).",
        "Asia/Tokyo - Japan Standard Time.",
        "For more time zones, refer to: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones"
    })
    public String TIME_ZONE = "Europe/Moscow";

    public List<String> BLOCKED_COMMANDS = List.of("/sethome", "/setwarp");

    @Create
    public MESSAGES MESSAGES;

    public static final class MESSAGES {

      public String USAGE = "Command help:{NL}/spec go (Nickname){NL}/spec off";
      public String UNKNOWN_SUBCOMMAND = "Unknown subcommand.";
      public String SPECIFY = "Please specify the suspect's nickname.";
      public String SUSPECT_NOT_FOUND = "The player could not be found.";
      public String CANNOT_SPECTATE_SELF = "You cannot spectate yourself.";
      public String NO_SPECTATE_SPECTATOR = "You cannot spectate a player who is already spectating.";
      public String STARTED = "You are now spectating {0}. Date: {1}";
      public String ALREADY_STARTED = "You are already spectating someone.";
      public String WORLD_NOT_FOUND = "&4The world for teleportation could not be found. Please contact an administrator.";
      public String STOPPED = "You have stopped spectating. Date: {0}";
      public String NOT_SPECTATING = "You are not currently spectating anyone.";
      public String SUSPECT_LEFT = "The player you were spectating has left the server.";
      public String TOO_FAR = "You cannot move too far away while spectating.";
      public String COMMAND_BLOCKED = "You cannot use this command while spectating.";
    }
  }
}