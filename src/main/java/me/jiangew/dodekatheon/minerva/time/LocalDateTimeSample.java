package me.jiangew.dodekatheon.minerva.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 19/12/2017
 */
public class LocalDateTimeSample {

    public static void main(String[] args) {
        LocalDateTime current = LocalDateTime.of(2017, Month.DECEMBER, 19, 21, 45, 59);

        DayOfWeek dayOfWeek = current.getDayOfWeek();
        Month month = current.getMonth();
        long minuteOfDay = current.getLong(ChronoField.MINUTE_OF_DAY);

        System.out.println(dayOfWeek);
        System.out.println(month);
        System.out.println(minuteOfDay);

        Instant instant = current.atZone(ZoneId.systemDefault()).toInstant();
        Date legacyDate = Date.from(instant);
        System.out.println(legacyDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse("2017-12-19 21:50:59", formatter);
        String dateTime = parsed.format(formatter);
        System.out.println(dateTime);
    }
}
