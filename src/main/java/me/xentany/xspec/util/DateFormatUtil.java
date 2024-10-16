package me.xentany.xspec.util;

import me.xentany.xspec.Settings;
import me.xentany.xspec.SpecPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;

public final class DateFormatUtil {

  private static final String DEFAULT_PATTERN;
  private static final ZoneId DEFAULT_ZONE_ID;

  static {
    DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DEFAULT_ZONE_ID = ZoneId.of("Europe/Moscow");
  }

  private static String PATTERN;
  private static String ZONE;
  private static ZoneId ZONE_ID;
  private static DateTimeFormatter DTF;

  public static void load() {
    var plugin = SpecPlugin.getInstance();

    DateFormatUtil.PATTERN = Settings.IMP.MAIN.DATE_PATTERN;
    DateFormatUtil.ZONE = Settings.IMP.MAIN.TIME_ZONE;

    try {
      DateFormatUtil.ZONE_ID = ZoneId.of(DateFormatUtil.ZONE);
    } catch (final Exception e) {
      DateFormatUtil.ZONE_ID = DateFormatUtil.DEFAULT_ZONE_ID;

      plugin.getLogger().log(Level.WARNING, "Invalid time zone: " + DateFormatUtil.ZONE +
          ". Defaulting to: " + DateFormatUtil.DEFAULT_ZONE_ID.getId());
    }

    try {
      DateFormatUtil.DTF = DateTimeFormatter.ofPattern(DateFormatUtil.PATTERN, Locale.ROOT);
    } catch (final Exception e) {
      DateFormatUtil.DTF = DateTimeFormatter.ofPattern(DateFormatUtil.DEFAULT_PATTERN, Locale.ROOT);

      plugin.getLogger().log(Level.WARNING, "Invalid date pattern: " + DateFormatUtil.PATTERN +
          ". Defaulting to: " + DateFormatUtil.DEFAULT_PATTERN);
    }
  }

  public static @NotNull String getFormattedDate() {
    return ZonedDateTime.now(DateFormatUtil.ZONE_ID).format(DateFormatUtil.DTF);
  }
}