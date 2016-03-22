package org.greenrobot.essentials.androidperf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractAndroidBenchmark {
    @Parameterized.Parameter(0)
    public Runnable runnable;

    @Parameterized.Parameter(1)
    public int runCount;

    @Test
    public void performance() throws Exception {
        final Runnable runnable = this.runnable;
        final int runCount = this.runCount;
        for (int i = 0; i < runCount; i++) {
            runnable.run();
        }
    }
}
