/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class DateUtilsTest {

    @Test
    public void testGetDayDifferenceOfReadableIntsPlusMinusNDays() {
        testGetDayDifferenceOfReadableIntsPlusMinusNDays(1);
        testGetDayDifferenceOfReadableIntsPlusMinusNDays(-1);
    }

    private void testGetDayDifferenceOfReadableIntsPlusMinusNDays(int sign) {
        Calendar calendar = Calendar.getInstance();
        DateUtils.setTime(calendar, 12, 0, 0, 0);
        int today = DateUtils.getDayAsReadableInt(calendar);
        for (int i = 1; i <= 5000; i++) {
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
