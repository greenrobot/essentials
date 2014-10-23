package de.greenrobot.common.checksum;

import de.greenrobot.common.LongHashSet;
import org.junit.Test;

import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/** Test hash functions; prints out collisions and time. */
public class HashCollider {
    @Test
    public void hashCollider() {
        hashCollider("Adler32", new Adler32());
        hashCollider("FNV1a", new FNV32());
        hashCollider("FNV1a-64", new FNV64());
        hashCollider("CRC32", new CRC32());
        hashCollider("Combined", new CombinedChecksum(new Adler32(), new CRC32()));
    }

    public void hashCollider(String name, Checksum checksum) {
        hashCollider(name, checksum, 1000000, 1024, 10);
    }

    public void hashCollider(String name, Checksum checksum, int count, int byteLength, int logCount) {
        System.out.println(name + " -----------------------------------------------------------");

        // Provide seed (42) to have reproducible results
        Random random = new Random(42);
        byte[] bytes = new byte[byteLength];

        LongHashSet values = new LongHashSet(count);
        int collisions = 0;
        long totalTime = 0;
        int firstCollision = 0;
        for (int i = 0; i < count; i++) {
            random.nextBytes(bytes);
            checksum.reset();
            long start = System.nanoTime();
            checksum.update(bytes, 0, bytes.length);
            totalTime += System.nanoTime() - start;
            if (!values.add(checksum.getValue())) {
                collisions++;
                if (firstCollision == 0) {
                    firstCollision = i + 1;
                }
            }

            if ((i + 1) % (count / logCount) == 0) {
                System.out.println(name + "\t" + (i + 1) + "\t\t" + "collisions: " + collisions + "\t\tms: " +
                        (totalTime / 1000000) + "\t\thash: " + checksum.getValue());
            }
        }
        System.out.println(name + "\tfirst collision at: " + (firstCollision == 0 ? "none" : firstCollision));
    }
}
