package org.greenrobot.essentials.javaperf;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.err;
import static java.lang.System.out;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        final String className = args[0];
        final int times = Integer.parseInt(args[1]);
        final int warmUpSeconds = Integer.parseInt(args[2]);
        final boolean useWallTime = args[3].equals("wall");

        final Runnable test = (Runnable) Class.forName(className).newInstance();
        final double median;
        if (useWallTime) {
            median = runWallTime(test, times, warmUpSeconds);
        } else {
            median = run(test, times, warmUpSeconds);
        }
        out.println(test + ":" + median);
    }

    public static double run(Runnable test, int times, int warmUpSeconds) {
        final long warmUpNs = warmUpSeconds * 1_000_000_000L;
        final String testName = getTestName(test);
        err.println("Running " + testName + " " + times + " times on Java " + System.getProperty("java.version") +
                " (" + System.getProperty("java.vendor") + ")");
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        err.println("Warming up for " + warmUpSeconds + " seconds...");
        final long warmStartCpuTime = threadMXBean.getCurrentThreadCpuTime();
        // warm up
        while (threadMXBean.getCurrentThreadCpuTime() < warmStartCpuTime + warmUpNs) {
            test.run();
        }
        err.println("Run testing");
        final List<Double> results = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            final long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
            test.run();
            final long endCpuTime = threadMXBean.getCurrentThreadCpuTime();
            results.add((endCpuTime - startCpuTime) / 1e6);
        }

        return getMedian(results);
    }

    public static double runWallTime(Runnable test, int times, int warmUpSeconds) {
        final long warmUpNs = warmUpSeconds * 1_000_000_000L;
        final String testName = getTestName(test);
        err.println("Running " + testName + " " + times + " times on Java " + System.getProperty("java.version") +
                " (" + System.getProperty("java.vendor") + ")");
        err.println("Warming up for " + warmUpSeconds + " seconds...");
        final long warmStartCpuTime = System.nanoTime();
        // warm up
        while (System.nanoTime() < warmStartCpuTime + warmUpNs) {
            test.run();
        }
        err.println("Run testing");
        final List<Double> results = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            final long startCpuTime = System.nanoTime();
            test.run();
            final long endCpuTime = System.nanoTime();
            results.add((endCpuTime - startCpuTime) / 1e6);
        }

        return getMedian(results);
    }

    private static String getTestName(Runnable test) {
        return test.getClass().getName().replace("org.greenrobot.essentials.javaperf.", "").replace("$", "/");
    }

    private static double getMedian(List<Double> values) {
        // sort ascending
        Collections.sort(values);
        // get median
        int middle = values.size() / 2;
        if (values.size() % 2 == 1) {
            return values.get(middle);
        } else {
            return (values.get(middle - 1) + values.get(middle)) / 2.0;
        }
    }
}
