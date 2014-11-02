greenrobot-common
=================
greenrobot-common provides general purpose utilities for Android and Java projects. Having its root in the early days of Android development, the library is minimalistic, tiny in size (jar < 100k), and focuses on efficiency and performance. 

Features
--------
The Utility classes cover [stream-based IO](java-common/src/main/java/de/greenrobot/common/io/IoUtils.java), [files](java-common/src/main/java/de/greenrobot/common/io/FileUtils.java), [strings](java-common/src/main/java/de/greenrobot/common/StringUtils.java), and [date/time](java-common/src/main/java/de/greenrobot/common/DateUtils.java). There are also efficient [hash map](java-common/src/main/java/de/greenrobot/common/LongHashMap.java) and [hash set](java-common/src/main/java/de/greenrobot/common/LongSetMap.java) implementation for primitive long keys.
   
Another important part of greenrobot-common are [hash functions](hash-functions.md). Our Murmur3A&F implementations are the fastest Java implementations known to us. Murmur3 are one of today's best hash functions available with excellent properties. At 3,6 GByte/s, we measured our Murmur3F implementation to outperform Guava's by factor 10. More information: [hash functions](hash-functions.md).

Speaking of Guava, this project is bare bones compared to a rich menu offered by Guava, or, let's say, Apache Commons. greenrobot-common is not a framework, it's rather a small set of utilities to make Java standard approaches more convenient or more efficient.

How to integrate
----------------
Just grab it from Maven Central:

    <dependency>
        <groupId>de.greenrobot</groupId>
        <artifactId>java-common</artifactId>
        <version>2.0.0</version>
    </dependency>

Code samples
============

```Java

// Get all bytes from and stream and close the stream safely
byte[] bytes = IoUtils.readAllBytesAndClose(inputStream);

// Read the contents of an file as a string (another method provides byte[])
String contents = FileUtils.readUtf8(file);

// How many days until new year's eve?
long time2 = DateUtils.getTimeForDay(2015, 12, 31);
int daysToNewYear = DateUtils.getDayDifference(time, time2);

```

We won't write much additional documentation for this project. Most of the method names should be self-explaining. Also, it's usually straight forward to look at the code and JavaDocs. 

Build system
============
Currently, Maven is used to build greenrobot-common. Inside of [build-common](build-common), there are some parent POMs defined that might be useful. One of those integrates FindBugs and Checkstyle in your build. Use it like this: 

    <parent>
        <groupId>de.greenrobot.build</groupId>
        <artifactId>parent-pom-with-checks</artifactId>
        <version>1.4.0</version>
        <relativePath></relativePath>
    </parent>

History
=======

You may notice that the version numbers are already above 1.0 for the initial release. That's because we were already having this project inside of greenrobot. It started as a general purpose library for Android around 2009, which was used for many Android projects since then. The Android library grew and matured over the years. In 2013, we noticed that parts of our Android library would be useful for some Java projects we do. So we moved the parts that don't depend on Android into a new project. Internally, we referred to this as java-common, which is still the artifact name. In late 2014, we decided to open source it as greenrobot-common.

Changelog
=========
Version 2.0.0
-------------
First open source release.
