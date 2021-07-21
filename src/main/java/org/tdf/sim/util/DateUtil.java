package org.tdf.sim.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtil {
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    public static LocalDateTime now(){
        return ZonedDateTime.now(UTC_ZONE_ID).toLocalDateTime();
    }
}
