package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.collections.LongHashSet;

import java.util.HashSet;
import java.util.Random;

public class LongHashSetBenchmark {
    static final int N = 100000;
    static final int WARM_UP_TIME_S = 5;
    static final int RUN_COUNT = 100;
    static final long SEED = 657483918;

    // this is only for development purposes or to run tests separately. For automated benchmarking use gradle
    public static void main(String[] args) {
        BenchmarkRunner.runWallTime(new LibImpl(), RUN_COUNT, WARM_UP_TIME_S);
    }

    public static class LibImpl implements Runnable {
        private final int initialCapacity;

        public LibImpl(int initialCapacity) {
            this.initialCapacity = initialCapacity;
        }

        public LibImpl() {
            this(16);
        }

        @Override
        public void run() {
            final LongHashSet set = new LongHashSet(initialCapacity);

            final Random random1 = new Random(SEED);
            final Random random2 = new Random(SEED);
            final Random random3 = new Random(SEED);

            for (int i = 0; i < N; i++) {
                final long key = random1.nextLong();
                set.add(key);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = random2.nextLong() + i % 2;
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = random3.nextLong();
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
        }
    }

    public static class StdImpl implements Runnable {
        private final int initialCapacity;

        public StdImpl(int initialCapacity) {
            this.initialCapacity = initialCapacity;
        }

        public StdImpl() {
            this(16);
        }

        @Override
        public void run() {
            final HashSet<Long> set = new HashSet<>(initialCapacity);

            final Random random1 = new Random(SEED);
            final Random random2 = new Random(SEED);
            final Random random3 = new Random(SEED);

            for (int i = 0; i < N; i++) {
                final long key = random1.nextLong();
                set.add(key);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = random2.nextLong() + i % 2;
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = random3.nextLong();
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
        }
    }

    public static class LibImplPrealloc extends LibImpl {
        public LibImplPrealloc() {
            super(N);
        }
    }

    public static class StdImplPrealloc extends StdImpl {
        public StdImplPrealloc() {
            super(N);
        }
    }
}
