package de.greenrobot.common.hash;

import de.greenrobot.common.io.IoUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

/**
 * Tests compatibility with CheckedOutputStream and CheckedInputStream.
 */
@RunWith(Parameterized.class)
public class ChecksumStreamTest extends AbstractAllChecksumTest {
    @Test
    public void testChecksumStreams() throws IOException {
        byte[] content = new byte[33333];
        new Random().nextBytes(content);

        Murmur3F murmur3F = new Murmur3F();
        murmur3F.update(content);
        String hash = murmur3F.getValueHexString();

        murmur3F.reset();
        CheckedOutputStream out = new CheckedOutputStream(new ByteArrayOutputStream(), murmur3F);
        out.write(content);
        Assert.assertEquals(hash, murmur3F.getValueHexString());

        murmur3F.reset();
        CheckedInputStream in = new CheckedInputStream(new ByteArrayInputStream(content), murmur3F);
        IoUtils.readAllBytes(in);
        Assert.assertEquals(hash, murmur3F.getValueHexString());
    }

}
