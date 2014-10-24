package de.greenrobot.common.checksum;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import de.greenrobot.common.LongHashSet;
import org.junit.Test;

import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/** Test hash functions; prints out collisions and time. */
public class HashCollider {
    //@Test
    public void hashColliderTotalRandom() {
        hashCollider("Adler32", new Adler32());
        hashCollider("FNV1a", new FNV32());
        hashCollider("FNV1a-64", new FNV64());
        hashCollider("CRC32", new CRC32());
        hashCollider("Combined", new CombinedChecksum(new Adler32(), new CRC32()));
        hashCollider("Murmur3A-32", new Murmur32Checksum());
    }

    //    @Test
    public void hashColliderSmallChanges() {
        hashColliderSmallChanges("Adler32", new Adler32());
        hashColliderSmallChanges("FNV1a", new FNV32());
        hashColliderSmallChanges("FNV1a-64", new FNV64());
        hashColliderSmallChanges("CRC32", new CRC32());
        hashColliderSmallChanges("Combined", new CombinedChecksum(new Adler32(), new CRC32()));
        hashColliderSmallChanges("Murmur3A-32", new Murmur32Checksum());
    }

    public void hashCollider(String name, Checksum checksum) {
        hashCollider(name, checksum, 1000000, 1024, 10, true);
    }

    public void hashColliderSmallChanges(String name, Checksum checksum) {
        hashCollider(name, checksum, 1000000, 1024, 10, false);
    }

    public void hashCollider(String name, Checksum checksum, int count, int byteLength, int logCount,
                             boolean totalRandom) {
        System.out.println(name + "\t-----------------------------------------------------------");

        // Provide seed (42) to have reproducible results
        Random random = new Random(42);
        byte[] bytes = new byte[byteLength];

        LongHashSet values = new LongHashSet(count);
        int collisions = 0;
        long totalTime = 0;
        int firstCollision = 0;
        int indexToChange = -1; // used if !totalRandom
        for (int i = 0; i < count; i++) {
            if (totalRandom) {
                random.nextBytes(bytes);
            } else {
                if (indexToChange != -1) {
                    byte existing = bytes[indexToChange];
                    byte newValue;
                    do {
                        newValue = (byte) random.nextInt();
                    } while (existing == newValue);
                    bytes[indexToChange] = newValue;
                }
                indexToChange++;
                if (indexToChange == byteLength) {
                    indexToChange = 0;
                }
            }
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

    class Murmur32Checksum implements Checksum {
        HashFunction x = Hashing.murmur3_32();
        Long hash;

        @Override
        public void update(int b) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void update(byte[] b, int off, int len) {
            if (hash != null) {
                throw new RuntimeException("No hash building available");
            }
            hash = 0xffffffffL & x.hashBytes(b, off, len).asInt();
        }

        @Override
        public long getValue() {
            return hash;
        }

        @Override
        public void reset() {
            hash = null;
        }
    }
}
