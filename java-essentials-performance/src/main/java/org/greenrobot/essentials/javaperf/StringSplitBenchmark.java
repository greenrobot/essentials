package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.StringUtils;

public class StringSplitBenchmark {
    static int SHORT_REPEAT_COUNT = 1000;
    static String SHORT_STRING = "The quick brown fox jumps over the lazy dog";
    static String LONG_STRING = generateLongString(10000);

    private static String generateLongString(int wordsCount) {
        StringBuilder builder = new StringBuilder();
        String[] words = StringUtils.split(SHORT_STRING, ' ');
        for (int i = 0; i < wordsCount; i++) {
            builder.append(words[i % words.length]).append(' ');
        }
        return builder.toString();
    }

    public static class ShortLibImpl implements Runnable {
        @Override
        public void run() {
            int count = 0;
            for (int i = 0; i < SHORT_REPEAT_COUNT; i++) {
                final String[] strings = StringUtils.split(SHORT_STRING, ' ');
                count += strings.length;
            }
            System.err.println("count: " + count);
        }
    }

    public static class ShortStdImpl implements Runnable {
        @Override
        public void run() {
            int count = 0;
            for (int i = 0; i < SHORT_REPEAT_COUNT; i++) {
                final String[] strings = SHORT_STRING.split(" ");
                count += strings.length;
            }
            System.err.println("count: " + count);
        }
    }

    public static class LongLibImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = StringUtils.split(LONG_STRING, ' ');
            System.err.println("count: " + strings.length);
        }
    }

    public static class LongStdImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = LONG_STRING.split(" ");
            System.err.println("count: " + strings.length);
        }
    }
}
