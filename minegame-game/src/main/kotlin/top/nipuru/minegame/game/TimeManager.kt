package top.nipuru.minegame.game

import java.time.*
import java.time.temporal.TemporalAdjusters

fun now(): Long {
    return Instant.now().epochSecond
}

fun dayZero(): Long {
    return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
}

fun timeDayZero(time: Long): Long {
    val dateTime =
        LocalDateTime.ofEpochSecond(time, 0, ZoneId.systemDefault().rules.getOffset(Instant.ofEpochSecond(time)))
    val startOfDay = dateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault())
    return startOfDay.toEpochSecond()
}

fun weekZero(): Long {
    return LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
}
