package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.collections.LongHashSet;

import java.util.HashSet;
import java.util.Random;

public class LongHashSetBenchmark {
    static final int N = 100000;
    static final int WARM_UP_TIME_S = 5;
    static final int RUN_COUNT = 100;

    // this is only for development purposes or to run tests separately. For automated benchmarking use gradle
    public static void main(String[] args) {
        BenchmarkRunner.runWallTime(new LibImpl(), RUN_COUNT, WARM_UP_TIME_S);
    }

    private LongHashSetBenchmark() {
    }

    private static class BaseImpl {
        final long[] values;

        public BaseImpl() {
            values = new long[N];
            final Random random = new Random(657483918);
            for (int i = 0; i < N; i++) {
                values[i] = random.nextLong();
            }
        }
    }

    public static class LibImpl extends BaseImpl implements Runnable {
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

            final long[] values = this.values;

            for (int i = 0; i < N; i++) {
                set.add(values[i]);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = values[i] + i % 2;
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = values[i];
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
        }

        @Override
        public String toString() {
            return "LongHashSet (Dynamic)/Lib";
        }
    }

    public static class StdImpl extends BaseImpl implements Runnable {
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

            final long[] values = this.values;

            for (int i = 0; i < N; i++) {
                set.add(values[i]);
            }
            // check contains on every second key and remove it
            for (int i = 0; i < N; i++) {
                final long key = values[i] + i % 2;
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
            // remove the rest
            for (int i = 0; i < N; i++) {
                final long key = values[i];
                if (set.contains(key)) {
                    set.remove(key);
                }
            }
        }

        @Override
        public String toString() {
            return "LongHashSet (Dynamic)/Std";
        }
    }

    public static class PreallocLibImpl extends LibImpl {
        public PreallocLibImpl() {
            super(N);
        }

        @Override
        public String toString() {
            return "LongHashSet (Prealloc)/Lib";
        }
    }

    public static class PreallocStdImpl extends StdImpl {
        public PreallocStdImpl() {
            super(N);
        }

        @Override
        public String toString() {
            return "LongHashSet (Prealloc)/Std";
        }
    }
}
