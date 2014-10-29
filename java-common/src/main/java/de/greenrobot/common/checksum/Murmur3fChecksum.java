package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

import java.util.zip.Checksum;

/** TODO */
public class Murmur3fChecksum implements Checksum {
    private static final long C1 = 0x87c37b91114253d5L;
    private static final long C2 = 0x4cf5ad432745937fL;

    private final long seed;

    private long h1;
    private long h2;
    private int length;

    private int partialPos;
    private long partialK1;
    private long partialK2;

    private boolean finished;
    private long finishedH1;
    private long finishedH2;

    public Murmur3fChecksum() {
        seed = 0;
    }

    public Murmur3fChecksum(int seed) {
        this.seed = seed & (0xffffffffL); // unsigned
        h1 = seed;
        h2 = seed;
    }

    @Override
    public void update(int b) {
        finished = false;
        switch (partialPos) {
            case 0:
                partialK1 = 0xff & b;
                break;
            case 1:
                partialK1 |= (0xff & b) << 8;
                break;
            case 2:
                partialK1 |= (0xff & b) << 16;
                break;
            case 3:
                partialK1 |= (0xffL & b) << 24;
                break;
            case 4:
                partialK1 |= (0xffL & b) << 32;
                break;
            case 5:
                partialK1 |= (0xffL & b) << 40;
                break;
            case 6:
                partialK1 |= (0xffL & b) << 48;
                break;
            case 7:
                partialK1 |= (0xffL & b) << 56;
                break;
            case 8:
                partialK2 = 0xff & b;
                break;
            case 9:
                partialK2 |= (0xff & b) << 8;
                break;
            case 10:
                partialK2 |= (0xff & b) << 16;
                break;
            case 11:
                partialK2 |= (0xffL & b) << 24;
                break;
            case 12:
                partialK2 |= (0xffL & b) << 32;
                break;
            case 13:
                partialK2 |= (0xffL & b) << 40;
                break;
            case 14:
                partialK2 |= (0xffL & b) << 48;
                break;
            case 15:
                partialK2 |= (0xffL & b) << 56;
                break;

        }
        partialPos++;
        if (partialPos == 16) {
            applyKs(partialK1, partialK2);
            partialPos = 0;
        }
        length++;

    }

    @Override
    public void update(byte[] b, int off, int len) {
        finished = false;
        while (partialPos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }

        int remainder = len & 0xF;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 16) {
            long k1 = ByteArrayUtils.getLongLE(b, i);
            long k2 = ByteArrayUtils.getLongLE(b, i + 8);
            applyKs(k1, k2);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    private void applyKs(long k1, long k2) {
        k1 *= C1;
        k1 = Long.rotateLeft(k1, 31); // k1 << 31) | (k1 >>> -31); // ROTL64(k1, 31);
        k1 *= C2;
        h1 ^= k1;

        h1 = Long.rotateLeft(h1, 27);//(h1 << 27) | (h1 >>> -27);//ROTL64(h1, 27);
        h1 += h2;
        h1 = h1 * 5 + 0x52dce729;

        k2 *= C2;
        k2 = Long.rotateLeft(k2, 33); //(k2 << 33) | (k2 >>> -33);//ROTL64(k2, 33);
        k2 *= C1;
        h2 ^= k2;

        h2 = Long.rotateLeft(h2, 31); //(h2 << 31) | (h2 >>> -31);//ROTL64(h2, 31);
        h2 += h1;
        h2 = h2 * 5 + 0x38495ab5;
    }

    @Override
    public long getValue() {
        checkFinished();
        return finishedH1;
    }

    private void checkFinished() {
        if (!finished) {
            finished = true;
            finishedH1 = h1;
            finishedH2 = h2;
            if (partialPos > 0) {
                if (partialPos > 8) {
                    long k2 = partialK2 * C2;
                    k2 = (k2 << 33) | (k2 >>> -33);//ROTL64(k2, 33);
                    k2 *= C1;
                    finishedH2 ^= k2;
                }
                long k1 = partialK1 * C1;
                k1 = (k1 << 31) | (k1 >>> -31); // ROTL64(k1, 31);
                k1 *= C2;
                finishedH1 ^= k1;
            }

            finishedH1 ^= length;
            finishedH2 ^= length;

            finishedH1 += finishedH2;
            finishedH2 += finishedH1;

            finishedH1 = fmix64(finishedH1);
            finishedH2 = fmix64(finishedH2);

            finishedH1 += finishedH2;
            finishedH2 += finishedH1;
        }
    }

    public long getValue2() {
        checkFinished();
        return finishedH2;
    }

    //    public BigInteger getBigValue() {
    //    }

    @Override
    public void reset() {
        h1 = seed;
        h2 = seed;
        length = 0;
        partialPos = 0;
        finished = false;
    }

    private long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;

        return k;
    }

}
