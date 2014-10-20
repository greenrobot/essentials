package de.greenrobot.common;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class LongHashSetTest {

    Random random;
    private String traceName;
    private long start;

    public LongHashSetTest() {
        this.random = new Random();
    }

    @Test
    public void testLongHashSetSimple() {
        LongHashSet set = new LongHashSet();

        set.add(1l << 33);
        assertFalse(set.contains(0));
        assertTrue(set.contains(1l << 33));

        long keyLong = 0x7fffffffl << 33l + 14;
        assertFalse(set.remove(keyLong));
        set.add(keyLong);
        assertTrue(set.contains(keyLong));
        assertTrue(set.remove(keyLong));
        assertFalse(set.remove(keyLong));

        keyLong = Long.MAX_VALUE;
        set.add(keyLong);
        assertTrue(set.contains(keyLong));

        keyLong = 8064216579113853113l;
        set.add(keyLong);
        assertTrue(set.contains(keyLong));
    }

    @Test
    public void testLongHashMapRandom() {
        LongHashSet set = new LongHashSet();
        for (int i = 0; i < 5000; i++) {
            long key = random.nextLong();
            set.add(key);
            assertTrue(set.contains(key));

            int keyInt = (int) key;
            set.add(keyInt);
            assertTrue(set.contains(keyInt));

            assertTrue(set.remove(key));
            if(key!=keyInt) {
                assertTrue(set.remove(keyInt));
            }

            assertFalse(set.remove(key));
            assertFalse(set.remove(keyInt));
        }
    }

}
