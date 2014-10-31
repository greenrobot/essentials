package de.greenrobot.common.hash;

import org.junit.Assert;
import org.junit.Test;

public class FNVJ64Test extends AbstractChecksumTest {
    public FNVJ64Test() {
        super(new FNVJ64());
    }

    @Test
    public void testExpectedHash() {
        testExpectedHash(-2788557096217532181L, 5189117314893555947L, 6178430581444874676L);
    }

    @Test
    public void testSeed() {
        checksum.update(23);
        long value = checksum.getValue();

        FNVJ64 fnvj = new FNVJ64(1);
        fnvj.update(23);
        long valueSeeded = fnvj.getValue();
        Assert.assertNotEquals(value, valueSeeded);

        fnvj.reset();
        fnvj.update(23);
        Assert.assertEquals(valueSeeded, fnvj.getValue());
    }
}
