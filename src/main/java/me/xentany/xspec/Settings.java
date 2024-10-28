package me.xentany.xspec;

import net.elytrium.commons.config.YamlConfig;
import net.elytrium.commons.kyori.serialization.Serializers;
import net.kyori.adventure.bossbar.BossBar;

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

  public static class MAIN {

    @Comment("Lightweight and anonymous. Please keep enabled to support the author.")
    public boolean BSTATS = true;

    @Comment({
        "Highly optimized and stress-tested update checker.",
        "Please keep enabled to always stay up to date with the latest version."
    })
    public boolean CHECK_FOR_UPDATES = true;

    @Comment({
        "After the player's nickname in the command you must enter the reason,",
        "which will be stored in the logs"
    })
    public boolean NEED_REASON = false;

    public List<String> REASONS = List.of("cheats", "report");

    @Comment({
        "If true, you can enter any reason, otherwise",
        "if false, you can only enter pre-prepared reasons from the config (TabComplete is present)"
    })
    public boolean ANY_REASON = false;

    @Comment({
        "If true, it will require an exact match of reasons from the logged reasons, otherwise",
        "if false, it will offer tab complete but will not complain that the reason is not from the logged reasons,",
        "simply put, allow any reason but leave tab complete"
    })
    public boolean EXACT_REASON = true;
    public boolean OFF_SPEC_ON_GAMEMODE_CHANGE = true;
    public boolean SUSPECT_GLOW = true;
    public boolean BOSSBAR_ENABLED = true;

    @Comment("If false, the player after '/spec off' will stay where he wrote the command, no teleporting")
    public boolean TELEPORT_AFTER_STOP = true;

    @Comment("Will message everyone who has xspec.notify them that someone has started spectating or finished")
    public boolean NOTIFY = true;

    @Comment(value = "For spectator", at = Comment.At.SAME_LINE)
    public boolean ACTIONBAR = true;

    @Comment("When stopped, it teleports to the old location, not from the config")
    public boolean RETURN_TO_OLD_LOCATION = false;

    @Comment("When stopped, the old game mode is enabled, not from the config")
    public boolean RETURN_TO_OLD_GAMEMODE = false;

    @Comment(value = "For spectator", at = Comment.At.SAME_LINE)
    public boolean NIGHT_VISION = true;

    public String STOP_GAMEMODE = "SURVIVAL";

    @Comment("World and coordinates where player teleports to after a stop spec.")
    public String TELEPORT_WORLD_NAME = "world";
    public double TELEPORT_X = 0.5d;
    public double TELEPORT_Y = 90.0d;
    public double TELEPORT_Z = 0.5d;

    @Comment("Leave it blank so it doesn't send anything")
    public String WEBHOOK = "";

    public String BAR_NAME = "&6Spectating behind player {0}";
    public BossBar.Color BAR_COLOR = BossBar.Color.WHITE;
    public BossBar.Overlay BAR_OVERLAY = BossBar.Overlay.PROGRESS;

    @Comment("Maximum distance in blocks at which a spectator can fly away.")
    public double MAXIMUM_DISTANCE = 25.0d;

    @Comment({
        "{0} - Hours",
        "{1} - Minutes",
        "{2} - Seconds",
        "{3} - Hours in total",
        "{4} - Minutes in total",
        "{5} - Seconds in total"
    })
    public String DURATION_FORMAT = "{0} h, {1} min, {2} sec";

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

    @Comment({
        "Don't use \\n, use {NL} for new line.",
        "If the message is empty, it will not be sent."
    })
    public static final class MESSAGES {

      public String USAGE = "Command help:{NL}/spec go (Nickname) [?Reason]{NL}/spec off";
      public String UNKNOWN_SUBCOMMAND = "Unknown subcommand.";
      public String SPECIFY = "Please specify the suspect's nickname.";
      public String SPECIFY_REASON = "Please specify the reason.";
      public String SUSPECT_NOT_FOUND = "The player could not be found.";
      public String PLAYER_BYPASSED = "You can't spectating this player.";
      public String CANNOT_SPECTATE_SELF = "You cannot spectate yourself.";
      public String NO_SPECTATE_SPECTATOR = "You cannot spectate a player who is already spectating.";
      public String INVALID_REASON = "You must write a prepared reason.";
      public String STARTED = "You are now spectating {0}. Date: {1}";
      public String STARTED_WEBHOOK = "{\"content\":null,\"embeds\":[{\"title\":\"Spectating of `{0}` has begun.\",\"description\":\"Start date: {1}\\nReason: {2}\\nSpectator: {4}\",\"color\":16711680}],\"attachments\":[]}";
      public String STARTED_NOTIFY = "Spectator {0} started spectating for {1} for the reason {2} in {3}.";
      public String ALREADY_STARTED = "You are already spectating someone.";
      public String WORLD_NOT_FOUND = "&4The world for teleportation could not be found. Please contact an administrator.";
      public String STOPPED = "You have stopped spectating. Date: {0}, Suspect: {1}";
      public String STOPPED_WEBHOOK = "{\"content\":null,\"embeds\":[{\"title\":\"Spectating of `{0}` is complete.\",\"description\":\"End date: {1}\\nDuration: {2}\\nSpectator: {4}\",\"color\":16745728}],\"attachments\":[]}";
      public String STOPPED_NOTIFY = "Spectator {0} stopped spectating for {1} at {2} the duration was {3}.";
      public String NOT_SPECTATING = "You are not currently spectating anyone.";
      public String SUSPECT_LEFT = "The player you were spectating ({0}) has left the server. Date: {1}";
      public String TOO_FAR = "You cannot move too far away while spectating.";
      public String ACTIONBAR = "&bSuspect's nickname {0}, suspect's ping: {1}.";
      public String COMMAND_BLOCKED = "You cannot use this command while spectating.";
      public String CANNOT_CHANGE_GAMEMODE = "You can't change the gamemode during the spectating.";
      public String STOPPED_BY_GAMEMODE_CHANGE = "Spectating stopped by changing the game mode. Date: {0}, Suspect: {1}";
    }
  }
}