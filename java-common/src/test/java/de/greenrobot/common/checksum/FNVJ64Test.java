package de.greenrobot.common.checksum;

import org.junit.Test;

public class FNVJ64Test extends AbstractChecksumTest {
    public FNVJ64Test() {
        super(new FNVJ64());
    }

    @Test
    public void testExpectedHash() {
        testExpectedHash(-2788557096217532181L, 5189117314893555947L, 6178430581444874676L);
    }

}
