package me.xentany.xspec.util;

import me.xentany.xspec.Settings;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateFormatUtil {

  private static final String PATTERN;
  private static final String ZONE;
  private static @MonotonicNonNull ZoneId ZONE_ID;
  private static @MonotonicNonNull DateTimeFormatter DTF;

  static {
    PATTERN = Settings.IMP.MAIN.DATE_PATTERN;
    ZONE = Settings.IMP.MAIN.TIME_ZONE;

    try {
      ZONE_ID = ZoneId.of(Settings.IMP.MAIN.TIME_ZONE);
    } catch (Exception e) {
      ZONE_ID = null;
    }

    try {
      DTF = DateTimeFormatter.ofPattern(PATTERN, Locale.ROOT);
    } catch (Exception e) {
      DTF = null;
    }
  }

  public static @NonNull String getFormattedDate() {
    if (DateFormatUtil.DTF != null && DateFormatUtil.ZONE_ID != null) {
      return ZonedDateTime.now(ZONE_ID).format(DateFormatUtil.DTF);
    } else {
      return "Incorrect date pattern or time zone: " + PATTERN + " " + ZONE;
    }
  }
}