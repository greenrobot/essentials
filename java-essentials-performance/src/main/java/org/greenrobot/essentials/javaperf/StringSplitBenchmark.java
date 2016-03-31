package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.StringUtils;

public class StringSplitBenchmark {
    static final int TINY_REPEAT_COUNT = 2000;
    static final int SHORT_REPEAT_COUNT = 1000;
    static final int LONG_STRING_WORDS_COUNT = 10000;
    static String TINY_STRING = "John Doe";
    static String SHORT_STRING = "The quick brown fox jumps over the lazy dog";
    static String LONG_STRING = generateLongString(LONG_STRING_WORDS_COUNT);

    public static void main(String[] args) {
        BenchmarkRunner.run(new ShortLibImpl(), 100, 3);
    }

    private StringSplitBenchmark() {
    }

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
            if (count != 9 * SHORT_REPEAT_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Short/Lib";
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
            if (count != 9 * SHORT_REPEAT_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Short/Std";
        }
    }

    public static class TinyLibImpl implements Runnable {
        @Override
        public void run() {
            int count = 0;
            for (int i = 0; i < TINY_REPEAT_COUNT; i++) {
                final String[] strings = StringUtils.split(TINY_STRING, ' ');
                count += strings.length;
            }
            if (count != 2 * TINY_REPEAT_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Tiny/Lib";
        }
    }

    public static class TinyStdImpl implements Runnable {
        @Override
        public void run() {
            int count = 0;
            for (int i = 0; i < TINY_REPEAT_COUNT; i++) {
                final String[] strings = TINY_STRING.split(" ");
                count += strings.length;
            }
            if (count != 2 * TINY_REPEAT_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Tiny/Std";
        }
    }

    public static class LongLibImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = StringUtils.split(LONG_STRING, ' ');
            if (strings.length != LONG_STRING_WORDS_COUNT + 1) { // "+ 1" for the last closing space
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Long/Lib";
        }
    }

    public static class LongStdImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = LONG_STRING.split(" ");
            if (strings.length != LONG_STRING_WORDS_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return "StringSplit/Long/Std";
        }
    }
}
