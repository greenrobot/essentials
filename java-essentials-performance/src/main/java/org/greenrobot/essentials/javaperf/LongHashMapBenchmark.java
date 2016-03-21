package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.collections.LongHashMap;

import java.util.HashMap;
import java.util.Random;

public class LongHashMapBenchmark {
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
            final LongHashMap<Object> map = new LongHashMap<>(initialCapacity);
            final Object object = new Object();

            final Random random1 = new Random(SEED);
            final Random random2 = new Random(SEED);
            final Random random3 = new Random(SEED);

            for (int i = 0; i < N; i++) {
                final long key = random1.nextLong();
                map.put(key, object);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = random2.nextLong() + i % 2;
                if (map.get(key) != null) {
                    map.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = random3.nextLong();
                if (map.get(key) != null) {
                    map.remove(key);
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
            final HashMap<Long, Object> map = new HashMap<>(initialCapacity);
            final Object object = new Object();

            final Random random1 = new Random(SEED);
            final Random random2 = new Random(SEED);
            final Random random3 = new Random(SEED);

            for (int i = 0; i < N; i++) {
                final long key = random1.nextLong();
                map.put(key, object);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = random2.nextLong() + i % 2;
                if (map.get(key) != null) {
                    map.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = random3.nextLong();
                if (map.get(key) != null) {
                    map.remove(key);
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
