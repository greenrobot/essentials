package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.StringUtils;

public class StringSplitBenchmark {
    static final int TINY_REPEAT_COUNT = 2000;
    static final int SHORT_REPEAT_COUNT = 1000;
    static final int LONG_WORDS_COUNT = 10000;
    static String TINY_STRING = "John Doe";
    static String SHORT_STRING = "The quick brown fox jumps over the lazy dog";
    static String LONG_STRING = generateLongString(LONG_WORDS_COUNT);
    static final int TINY_WORDS_COUNT = StringUtils.split(TINY_STRING, ' ').length;
    static final int SHORT_WORDS_COUNT = StringUtils.split(SHORT_STRING, ' ').length;

    public static void main(String[] args) {
        BenchmarkRunner.run(new ShortLibImpl(), 100, 3);
    }

    static String name(int wordsCount, int times, String impl) {
        if (times > 1) {
            return "StringSplit (" + wordsCount + " words, " + times + " times)/" + impl;
        } else {
            return "StringSplit (" + wordsCount + " words)/" + impl;
        }
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
            return name(SHORT_WORDS_COUNT, SHORT_REPEAT_COUNT, "Lib");
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
            return name(SHORT_WORDS_COUNT, SHORT_REPEAT_COUNT, "Std");
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
            return name(TINY_WORDS_COUNT, TINY_REPEAT_COUNT, "Lib");
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
            return name(TINY_WORDS_COUNT, TINY_REPEAT_COUNT, "Std");
        }
    }

    public static class LongLibImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = StringUtils.split(LONG_STRING, ' ');
            if (strings.length != LONG_WORDS_COUNT + 1) { // "+ 1" for the last closing space
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return name(LONG_WORDS_COUNT, 1, "Lib");
        }
    }

    public static class LongStdImpl implements Runnable {
        @Override
        public void run() {
            final String[] strings = LONG_STRING.split(" ");
            if (strings.length != LONG_WORDS_COUNT) {
                throw new RuntimeException("Check test condition");
            }
        }

        @Override
        public String toString() {
            return name(LONG_WORDS_COUNT, 1, "Std");
        }
    }
}
