package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.StringSplitBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class StringSplitAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new StringSplitBenchmark.ShortStdImpl(), 100},
            {new StringSplitBenchmark.ShortLibImpl(), 100},
            {new StringSplitBenchmark.LongStdImpl(), 100},
            {new StringSplitBenchmark.LongLibImpl(), 100},
        });
    }
}
