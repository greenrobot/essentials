package de.greenrobot.common.checksum;

import java.io.UnsupportedEncodingException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Calculates a 64 bit checksum by combining CRC32 and Adler32 having much less collisions than CRC32 or Adler32 alone.
 * <p/>
 * Collisions based on random 1K byte blocks:
 * <table>
 * <tr> <th>Count</th> <th>Adler32</th> <th>CRC32</th> <th>Combined</th> </tr>
 * <tr> <td>100 K</td> <td>9</td> <td>3</td> <td>0</td> </tr>
 * <tr> <td>1 M</td> <td>868</td> <td>128</td> <td>0</td> </tr>
 * <tr> <td>10 M</td> <td>90214</td> <td>11665</td> <td>0</td> </tr>
 * <tr> <td>50 M</td> <td>2199431</td> <td>289261</td> <td>0</td> </tr>
 * <tr> <td>100 M</td> <td>8499427</td> <td>1154526</td> <td>0</td> </tr>
 * </table>
 */
public class DataChecksum implements Checksum {
    private final Checksum checksum;

    public DataChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public void update(int b) {
        checksum.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        checksum.update(b, off, len);
    }

    @Override
    public long getValue() {
        return checksum.getValue();
    }

    @Override
    public void reset() {
        checksum.reset();
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

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void updateUtf8(String[] strings) {
        if (strings != null) {
            for (String string : strings) {
                updateUtf8(string);
            }
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
    public void update(byte[] numbers) {
        if (numbers != null) {
            update(numbers, 0, numbers.length);
        }
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
