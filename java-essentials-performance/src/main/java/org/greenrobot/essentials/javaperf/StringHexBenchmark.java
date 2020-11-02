package org.greenrobot.essentials.javaperf;

import com.google.common.hash.HashCode;
import org.greenrobot.essentials.StringUtils;

import java.util.Random;

public class StringHexBenchmark {
    static final int BYTES_COUNT = 1024 * 1024; // 1 MB
    static final byte[] bytes = generateBytes();

    private static byte[] generateBytes() {
        final byte[] bytes = new byte[BYTES_COUNT];
        Random random = new Random(409342832);
        random.nextBytes(bytes);
        return bytes;
    }

    private StringHexBenchmark() {
    }

    public static class LibImpl implements Runnable {
        @Override
        public void run() {
            final String hex = StringUtils.hex(bytes);
            // avoid optimization by HotSpot
            if (hex.length() != bytes.length * 2) {
                throw new RuntimeException();
            }
        }

        @Override
        public String toString() {
            return "StringHex/Lib";
        }
    }

    public static class StdImpl implements Runnable {
        @Override
        public void run() {
            final String hex = HashCode.fromBytes(bytes).toString();
            // avoid optimization by HotSpot
            if (hex.length() != bytes.length * 2) {
                throw new RuntimeException();
            }
        }

        @Override
        public String toString() {
            return "StringHex/Std";
        }
    }
}
