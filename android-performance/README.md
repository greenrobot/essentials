Essentials Performance Benchmarks on Android
============================================

Benchmarks implemented as unit tests for Android.

It's possible to run them in Android Studio/IntelliJ Idea (open statistics window in unit tests panel to see timings).

If you would like to create a beautiful table, you can run benchmarks and generate complete TSV-report, using:

    ./gradlew android-performance:measurePerformance

The report can be found then in **android-performance/build/reports/android-performance.tsv** file. It can be imported into Google Spread Sheets, for example.