/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.greenrobot.essentials.hash;

import org.greenrobot.essentials.collections.LongHashSet;

import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/** Test hash functions; prints out collisions and time. */
public class HashCollider {
    private final static boolean COUNT_BITS = true;

    //    @Test
    public void hashColliderTotalRandom() throws Exception {
        //        hashCollider("Adler32", new Adler32());
        //        hashCollider("CRC32", new CRC32());
        //
        //        hashCollider("FNV1a", new FNV32());
        //        hashCollider("FNV1a-64", new FNV64());

        //        hashCollider("Murmur2", new Murmur2Checksum());
        //        // Murmur2b is faster than Murmur2, hashes match Murmur2
        //        hashCollider("Murmur2b", new Murmur2bChecksum());
        //        hashCollider("Murmur3A-32 (Guava)", new Murmur3aGuavaChecksum());
        //        hashCollider("Murmur3A-32 (yonik)", new MurmurHash3YonikChecksum());
        //        hashCollider("Murmur3A-32", new Murmur3aChecksum());
        //        hashCollider("Murmur3F-128 (Guava)", new Murmur3fGuavaChecksum());
        //        hashCollider("Murmur3F-128", new Murmur3fChecksum());

        //        The implementation of XXHash seems to be pretty broken with high number of collisions
        //        Checksum xxChecksum = XXHashFactory.fastestJavaInstance().newStreamingHash32(0).asChecksum();
        //        hashCollider("xxHash", xxChecksum);
        //        hashCollider("MD5", new MessageDigestChecksum("MD5"));
        //        hashCollider("SHA-1", new MessageDigestChecksum("SHA-1"));
    }

    //    @Test
    public void hashColliderSmallChanges() {
        hashColliderSmallChanges("Adler32", new Adler32());
        hashColliderSmallChanges("FNV1a", new FNV32());
        hashColliderSmallChanges("FNV1a-64", new FNV64());
        hashColliderSmallChanges("CRC32", new CRC32());
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
        Random random = new Random(31);

        // SecureRandom is rather slow, but potentially useful for testing bit distribution
        // byte[] seed = {1, 2, 3, 4};
        // SecureRandom random = new SecureRandom(seed);

        byte[] bytes = new byte[byteLength];
        int[] bitOneCounts = new int[64];

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
            long hash = checksum.getValue();
            totalTime += System.nanoTime() - start;
            if (!values.add(hash)) {
                collisions++;
                if (firstCollision == 0) {
                    firstCollision = i + 1;
                }
            }
            if (COUNT_BITS) {
                for (int bitPos = 0; bitPos < 64; bitPos++) {
                    if (((hash >> bitPos) & 1) == 1) {
                        bitOneCounts[bitPos]++;
                    }
                }
            }

            if (logCount > count || (i + 1) % (count / logCount) == 0) {
                long ms = totalTime / 1000000;
                int mbs = (int) (1000000000d * i * byteLength / 1024 / 1024 / totalTime + 0.5f);
                System.out.println(name + "\t" + (i + 1) + "\t\t" + "collisions: " + collisions + "\t\tms: " + ms +
                        "\t\tMB/s: " + mbs + "\t\thash: " + hash);
            }
        }
        // System.out.println(name + "\tfirst collision at: " + (firstCollision == 0 ? "none" : firstCollision));

        checkBitStats(name, bitOneCounts, count);
    }

    private void checkBitStats(String name, int[] bitOneCounts, int count) {
        if (COUNT_BITS) {
            boolean is64 = bitOneCounts[32] + bitOneCounts[33] > 0;
            int bits = is64 ? 64 : 32;
            int perfectBitCount = count / 2;
            long offSum = 0;
            long offSumQ = 0;
            double q = 0;
            for (int bitPos = 0; bitPos < bits; bitPos++) {
                int bitOneCount = bitOneCounts[bitPos];
                int delta = Math.abs(perfectBitCount - bitOneCount);
                //                System.out.println(name + "\tBit " + (bitPos < 10 ? "0" + bitPos : bitPos) + ": " + bitOneCount +
                //                        "\t\tdelta perfect: " + delta);
                offSum += delta;
                offSumQ += ((long) delta) * delta;
                q += ((double) delta) * delta / count / bits;
            }
            System.out.println(name + "\tQuality - off sum: " + offSum + "\t\toff² sum: " + offSumQ +
                    "\t\tnegQ: " + q);
        }
    }

}
