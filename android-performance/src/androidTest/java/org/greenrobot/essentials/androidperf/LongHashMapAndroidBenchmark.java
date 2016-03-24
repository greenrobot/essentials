package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.LongHashMapBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class LongHashMapAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}:{1}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new LongHashMapBenchmark.StdImpl(), 10},
            {new LongHashMapBenchmark.LibImpl(), 10},
            {new LongHashMapBenchmark.PreallocStdImpl(), 10},
            {new LongHashMapBenchmark.PreallocLibImpl(), 10},
        });
    }
}
