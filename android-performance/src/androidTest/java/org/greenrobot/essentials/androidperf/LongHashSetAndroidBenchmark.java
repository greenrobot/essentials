package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.LongHashSetBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class LongHashSetAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}:{1}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new LongHashSetBenchmark.StdImpl(), 10},
            {new LongHashSetBenchmark.LibImpl(), 10},
            {new LongHashSetBenchmark.PreallocStdImpl(), 10},
            {new LongHashSetBenchmark.PreallocLibImpl(), 10},
        });
    }
}
