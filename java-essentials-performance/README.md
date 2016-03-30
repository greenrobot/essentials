Essentials Performance Benchmarks
=================================

There are performance benchmarks for some of the library classes.

Each benchmark is running in separate JVM with warming up. 

Running
-------

To run all benchmarks from the root project use:

    ./gradlew java-essentials-performance:allBenchmarks

Report
------
After running benchmarks you can find report in `java-essentials-performance/build/reports/performance.tsv`

Configuration
-------------
To change number of runs or time for warming up see [benchmarks.gradle](benchmarks.gradle).

