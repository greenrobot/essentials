package de.greenrobot.common.checksum;

import org.junit.Test;

public class FNVJ32Test extends AbstractChecksumTest {
    public FNVJ32Test() {
        super(new FNVJ32());
    }

    @Test
    public void testExpectedHash() {
        testExpectedHash(0x46B0F16BL, 0x252B0A1FL, 0x8AF88E69L);
    }

}
