package de.greenrobot.common;

import org.junit.Test;

import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/** Test hash functions; prints out collisions and time. */
public class HashCollider {
//    @Test
    public void hashCollider() {
        hashCollider("Adler32", new Adler32());
        hashCollider("FNV1a", new FNV32());
        hashCollider("CRC32", new CRC32());
        hashCollider("Combined", new AdlerCrcCombinedChecksum());
    }

    public void hashCollider(String name, Checksum checksum) {
        // Provide seed (42) to have reproducible results
        Random random = new Random(42);
        byte[] bytes = new byte[1024];
        int count = 1000000;

        LongHashSet values = new LongHashSet(count);
        int collisions = 0;
        long totalTime = 0;
        for (int i = 0; i < count; i++) {
            random.nextBytes(bytes);
            checksum.reset();
            long start = System.nanoTime();
            checksum.update(bytes, 0, bytes.length);
            totalTime += System.nanoTime() - start;
            if (!values.add(checksum.getValue())) {
                collisions++;
            }

            if ((i + 1) % (count / 10) == 0) {
                System.out.println(name + "\t" + (i + 1) + "\t\t" + "collisions: " + collisions + "\t\tms: " +
                        (totalTime / 1000000) + "\t\thash: " + checksum.getValue());
            }
        }
    }
}
