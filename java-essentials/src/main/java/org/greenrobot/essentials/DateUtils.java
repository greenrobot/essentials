/*
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.greenrobot.essentials;

import java.util.Calendar;

/**
 * Simple Date and time utils.
 */
public class DateUtils {
    /**
     * Calendar objects are rather expensive: for heavy usage it's a good idea to use a single instance per thread
     * instead of calling Calendar.getInstance() multiple times. Calendar.getInstance() creates a new instance each
     * time.
     */
    public static final class DefaultCalendarThreadLocal extends ThreadLocal<Calendar> {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    }

    private static ThreadLocal<Calendar> calendarThreadLocal = new DefaultCalendarThreadLocal();

    public static long getTimeForDay(int year, int month, int day) {
        return getTimeForDay(calendarThreadLocal.get(), year, month, day);
    }

    /** @param calendar helper object needed for conversion */
    public static long getTimeForDay(Calendar calendar, int year, int month, int day) {
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }

    /** Sets hour, minutes, seconds and milliseconds to the given values. Leaves date info untouched. */
    public static void setTime(Calendar calendar, int hourOfDay, int minute, int second, int millisecond) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
    }

    /** Readable yyyyMMdd int representation of a day, which is also sortable. */
    public static int getDayAsReadableInt(long time) {
        Calendar cal = calendarThreadLocal.get();
        cal.setTimeInMillis(time);
        return getDayAsReadableInt(cal);
    }

    /** Readable yyyyMMdd representation of a day, which is also sortable. */
    public static int getDayAsReadableInt(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return year * 10000 + month * 100 + day;
    }

    /** Returns midnight of the given day. */
    public static long getTimeFromDayReadableInt(int day) {
        return getTimeFromDayReadableInt(calendarThreadLocal.get(), day, 0);
    }

    /** @param calendar helper object needed for conversion */
    public static long getTimeFromDayReadableInt(Calendar calendar, int readableDay, int hour) {
        int day = readableDay % 100;
        int month = readableDay / 100 % 100;
        int year = readableDay / 10000;

        calendar.clear(); // We don't set all fields, so we should clear the calendar first
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTimeInMillis();
    }

    public static int getDayDifferenceOfReadableInts(int dayOfBroadcast1, int dayOfBroadcast2) {
        long time1 = getTimeFromDayReadableInt(dayOfBroadcast1);
        long time2 = getTimeFromDayReadableInt(dayOfBroadcast2);

        // Don't use getDayDifference(time1, time2) here, it's wrong for some days.
        // Do float calculation and rounding at the end to cover daylight saving stuff etc.
        float daysFloat = (time2 - time1) / 1000 / 60 / 60 / 24f;
        return Math.round(daysFloat);
    }

    public static int getDayDifference(long time1, long time2) {
        return (int) ((time2 - time1) / 1000 / 60 / 60 / 24);
    }

    public static long addDays(long time, int days) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTimeInMillis();
    }

    public static void addDays(Calendar calendar, int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
    }

}
