package de.greenrobot.common.hash;

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public abstract class AbstractAllChecksumTest {
    @Parameterized.Parameter
    public Checksum checksum;

    @Parameterized.Parameters(name = "{0}")
    public static Collection alignments() {
        return Arrays.asList(new Object[][]{
                {new Adler32()},
                {new FNV32()},
                {new FNV64()},
                {new FNVJ32()},
                {new FNVJ64()},
                {new Murmur3A()},
                {new Murmur3F()}});
    }
}
