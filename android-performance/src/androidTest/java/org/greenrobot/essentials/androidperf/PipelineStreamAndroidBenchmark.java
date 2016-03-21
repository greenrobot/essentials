package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.PipelineStreamBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class PipelineStreamAndroidBenchmark extends AbstractAndroidBenchmark {
    @Parameterized.Parameters(name = "{0}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new PipelineStreamBenchmark.StdImpl(), 1},
            {new PipelineStreamBenchmark.LibImpl(), 1},
        });
    }
}
