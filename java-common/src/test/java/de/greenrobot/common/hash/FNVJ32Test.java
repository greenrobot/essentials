package de.greenrobot.common.hash;

import org.junit.Assert;
import org.junit.Test;

public class FNVJ32Test extends AbstractChecksumTest {
    public FNVJ32Test() {
        super(new FNVJ32());
    }

    @Test
    public void testExpectedHash() {
        testExpectedHash(0x46B0F16BL, 0x252B0A1FL, 0x8AF88E69L);
    }

    @Test
    public void testSeed() {
        checksum.update(23);
        long value = checksum.getValue();

        FNVJ32 fnvj = new FNVJ32(1);
        fnvj.update(23);
        long valueSeeded = fnvj.getValue();
        Assert.assertNotEquals(value, valueSeeded);

        fnvj.reset();
        fnvj.update(23);
        Assert.assertEquals(valueSeeded, fnvj.getValue());
    }

}
