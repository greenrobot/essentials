package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.LongHashSetBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class LongHashSetAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new LongHashSetBenchmark.StdImpl(), 1},
            {new LongHashSetBenchmark.LibImpl(), 1},
            {new LongHashSetBenchmark.StdImplPrealloc(), 1},
            {new LongHashSetBenchmark.LibImplPrealloc(), 1},
        });
    }
}
