package de.greenrobot.common;

import java.io.UnsupportedEncodingException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/** Calculates a 64 bit checksum by combining CRC32 and Adler32. */
public class AdlerCrcCombinedChecksum implements Checksum {
    private final CRC32 crc32;
    private final Adler32 adler32;

    public AdlerCrcCombinedChecksum() {
        crc32 = new CRC32();
        adler32 = new Adler32();
    }

    @Override
    public void update(int b) {
        crc32.update(b);
        adler32.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        crc32.update(b, off, len);
        adler32.update(b, off, len);
    }

    @Override
    public long getValue() {
        return (adler32.getValue() << 32) | crc32.getValue();
    }

    @Override
    public void reset() {
        crc32.reset();
        adler32.reset();
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void updateUtf8(String string) {
        if (string != null) {
            byte[] bytes;
            try {
                bytes = string.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            update(bytes, 0, bytes.length);
        }
    }

    public void updateShort(short number) {
        update((number >>> 8) & 0xff);
        update(number & 0xff);
    }

    public void updateInt(int number) {
        update((number >>> 24) & 0xff);
        update((number >>> 16) & 0xff);
        update((number >>> 8) & 0xff);
        update(number & 0xff);
    }

    public void updateLong(long number) {
        update((int) (number >>> 56) & 0xff);
        update((int) (number >>> 48) & 0xff);
        update((int) (number >>> 40) & 0xff);
        update((int) (number >>> 32) & 0xff);
        update((int) (number >>> 24) & 0xff);
        update((int) (number >>> 16) & 0xff);
        update((int) (number >>> 8) & 0xff);
        update((int) (number & 0xff));
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(short[] numbers) {
        if (numbers != null) {
            for (short number : numbers) {
                updateShort(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(int[] numbers) {
        if (numbers != null) {
            for (int number : numbers) {
                updateInt(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(long[] numbers) {
        if (numbers != null) {
            for (long number : numbers) {
                updateLong(number);
            }
        }
    }

}
