package org.greenrobot.essentials.androidperf;

import org.greenrobot.essentials.javaperf.PipelineStreamBenchmark;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class PipelineStreamAndroidBenchmark extends AbstractAndroidBenchmark {
    public static final int STREAM_LENGTH = 100 * 1024; // 100KB

    @Parameterized.Parameters(name = "{0}:{1}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
            {new PipelineStreamBenchmark.StdImpl(STREAM_LENGTH), 10},
            {new PipelineStreamBenchmark.LibImpl(STREAM_LENGTH), 1000},
        });
    }
}
