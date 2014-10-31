package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class DateUtilsTest {

    @Test
    public void testGetDayDifferenceOfReadableIntsPlusMinus1000Days() {
        testGetDayDifferenceOfReadableIntsPlusMinus1000Days(1);
        testGetDayDifferenceOfReadableIntsPlusMinus1000Days(-1);
    }

    private void testGetDayDifferenceOfReadableIntsPlusMinus1000Days(int sign) {
        Calendar calendar = Calendar.getInstance();
        DateUtils.setTime(calendar, 12, 0, 0, 0);
        int today = DateUtils.getDayAsReadableInt(calendar);
        for (int i = 1; i <= 1000; i++) {
            DateUtils.addDays(calendar, sign * 1);
            int day = DateUtils.getDayAsReadableInt(calendar);
            int diff = DateUtils.getDayDifferenceOfReadableInts(today, day);
            Assert.assertEquals(sign * i, diff);
        }
    }

    @Test
    public void testGetDayDifferenceOfReadableInts() {
        checkDayDifference(20110101, 20110101, 0);
        checkDayDifference(20110101, 20110102, 1);
        checkDayDifference(20110101, 20110103, 2);
        checkDayDifference(20110101, 20110201, 31);
        checkDayDifference(20110101, 20110301, 59);
        checkDayDifference(20110102, 20110101, -1);
        checkDayDifference(20110103, 20110101, -2);
        checkDayDifference(20110201, 20110101, -31);
        checkDayDifference(20110301, 20110101, -59);
        checkDayDifference(20111231, 20120101, 1);
    }

    private void checkDayDifference(int day1, int day2, int expectedDifference) {
        int actualDifference = DateUtils.getDayDifferenceOfReadableInts(day1, day2);
        Assert.assertEquals(expectedDifference, actualDifference);
    }
}
