package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.StringUtils;

import javax.xml.bind.DatatypeConverter;
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

    public static class LibImpl implements Runnable {
        @Override
        public void run() {
            final String hex = StringUtils.hex(bytes);
            // avoid optimization by HotSpot
            System.err.println("length: " + hex.length());
        }
    }

    public static class StdImpl implements Runnable {
        @Override
        public void run() {
            final String hex = DatatypeConverter.printHexBinary(bytes);
            // avoid optimization by HotSpot
            System.err.println("length: " + hex.length());
        }
    }
}
