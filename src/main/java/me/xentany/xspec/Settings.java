package me.xentany.xspec;

import net.elytrium.commons.config.YamlConfig;
import net.elytrium.commons.kyori.serialization.Serializers;

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

    @Comment("World and coordinates where player teleports to after a stop spec.")
    public String TELEPORT_WORLD_NAME = "world";
    public double TELEPORT_X = 0.5d;
    public double TELEPORT_Y = 90.0d;
    public double TELEPORT_Z = 0.5d;

    @Comment("Maximum distance in blocks at which a spectator can fly away.")
    public double MAXIMUM_DISTANCE = 25.0d;

    @Create
    public MESSAGES MESSAGES;

    public static final class MESSAGES {

      public String USAGE = "Command help:{NL}/spec go (Nickname){NL}/spec off";
      public String SPECIFY = "Please specify the suspect's nickname.";
      public String SUSPECT_NOT_FOUND = "The player could not be found.";
      public String CANNOT_SPECTATE_SELF = "You cannot spectate yourself.";
      public String NO_SPECTATE_SPECTATOR = "You cannot spectate a player who is already spectating.";
      public String STARTED = "You are now spectating {0}.";
      public String ALREADY_STARTED = "You are already spectating someone.";
      public String STOPPED = "You have stopped spectating.";
      public String NOT_SPECTATING = "You are not currently spectating anyone.";
      public String SUSPECT_LEFT = "The player you were spectating has left the server.";
      public String TOO_FAR = "You cannot move too far away while spectating.";
    }
  }
}