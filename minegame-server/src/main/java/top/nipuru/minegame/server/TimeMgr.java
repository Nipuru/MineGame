package top.nipuru.minegame.server;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class TimeMgr {

    public static long now() {
        return Instant.now().getEpochSecond();
    }

    public static long dayZero() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    public static long timeDayZero(long time) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneId.systemDefault().getRules().getOffset(Instant.ofEpochSecond(time)));
        ZonedDateTime startOfDay = dateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault());
        return startOfDay.toEpochSecond();
    }

    public static long weekZero() {
        return LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

}
