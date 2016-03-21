package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.LongHashMapBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class LongHashMapAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new LongHashMapBenchmark.StdImpl(), 1},
            {new LongHashMapBenchmark.LibImpl(), 1},
            {new LongHashMapBenchmark.StdImplPrealloc(), 1},
            {new LongHashMapBenchmark.LibImplPrealloc(), 1},
        });
    }
}
