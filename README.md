greenrobot-common
=================
greenrobot-common provides general purpose utilities for Android and Java projects. Having its root in the early days of Android development, the library is minimalistic, tiny in size (jar < 100k), and focuses on efficiency and performance.

[![Build Status](https://travis-ci.org/greenrobot/greenrobot-common.svg?branch=master)](https://travis-ci.org/greenrobot/greenrobot-common)

Features
--------
The Utility classes cover [stream-based IO](java-common/src/main/java/de/greenrobot/common/io/IoUtils.java), [files](java-common/src/main/java/de/greenrobot/common/io/FileUtils.java), [strings](java-common/src/main/java/de/greenrobot/common/StringUtils.java), and [date/time](java-common/src/main/java/de/greenrobot/common/DateUtils.java). There are also efficient [hash map](java-common/src/main/java/de/greenrobot/common/LongHashMap.java) and [hash set](java-common/src/main/java/de/greenrobot/common/LongSetMap.java) implementation for primitive long keys.
   
Another important part of greenrobot-common are [hash functions](hash-functions.md). Our Murmur3A&F implementations are the fastest Java implementations known to us. Murmur3 hash functions are one of today's best hash functions available with excellent properties. At 3,6 GByte/s, we measured our Murmur3F implementation to outperform Guava's by factor 10. Find more information on the [hash functions page](hash-functions.md) or jump directly to our [Java hash function benchmark PDF](web-resources/hash-functions-benchmark.pdf).

Speaking of Guava, this project is bare bones compared to a rich menu offered by Guava, or, let's say, Apache Commons. greenrobot-common is not a framework, it's rather a small set of utilities to make Java standard approaches more convenient or more efficient.

How to integrate in your project
--------------------------------
Just grab it from Maven Central (or jCenter). For Gradle, add the following dependency:

    compile 'de.greenrobot:java-common:2.0.0'

And for Maven:
    
    <dependency>
        <groupId>de.greenrobot</groupId>
        <artifactId>java-common</artifactId>
        <version>2.0.0</version>
    </dependency>

Code samples
============
Example code on how to use some of the utility classes: 

```Java
// Get all bytes from stream and close the stream safely
byte[] bytes = IoUtils.readAllBytesAndClose(inputStream);

// Read the contents of an file as a string (use readBytes to get byte[])
String contents = FileUtils.readUtf8(file);

// How many days until new year's eve?
long time2 = DateUtils.getTimeForDay(2015, 12, 31);
int daysToNewYear = DateUtils.getDayDifference(time, time2);
```

[Our hash functions](hash-functions.md) implement [java.util.zip.Checksum](http://docs.oracle.com/javase/8/docs/api/java/util/zip/Checksum.html), so this code might look familiar to you:

```Java
Murmur3A murmur = new Murmur3A();
murmur.update(bytes);
long hash = murmur.getValue();
```

All hashes can be calculated progressively by calling update(...) multiple times. Our Murmur3A implementation goes a step further by offering updates with primitive data in a very efficient way:
```Java
// reuse the previous instance and start over to calculate a new hash
murmur.reset();

murmur.updateLong(42L);

// Varargs and arrays are supported natively, too  
murmur.updateInt(2014, 2015, 2016);

murmur.updateUtf8("And strings, of course");

// Hash for the previous update calls. No conversion to byte[] necessary.
hash = murmur.getValue();
```
 
We may not write a lot of documentation for this project. The utility classes are straight forward and don't have dependencies, so you should be fine to grasp them by having a look at their source code. Most of the method names should be self-explaining, and often you'll find JavaDocs where needed. Code is the best documentation, right? 

Build setup
===========
Currently, Maven is used to build greenrobot-common. Inside of [build-common](build-common), there are two parent POMs defined that might be useful: parent-pom and parent-pom-with-checks. The latter integrates FindBugs and Checkstyle in your build. Use it like this: 

    <parent>
        <groupId>de.greenrobot</groupId>
        <artifactId>parent-pom-with-checks</artifactId>
        <version>2.0.0</version>
        <relativePath></relativePath>
    </parent>

History
=======

You may notice that the version numbers are already above 1.0 for the initial release. That's because we were already having this project inside of greenrobot. It started as a general purpose library for Android around 2009, which was used for many Android projects since then. The Android library grew and matured over the years. In 2013, we noticed that parts of our Android library would be useful for some Java projects we do. So we moved the parts that don't depend on Android into a new project. Internally, we referred to this as java-common, which is still the artifact name. In late 2014, we decided to open source it as greenrobot-common.

More Open Source by greenrobot
==============================
[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other. 
 
[__greenDAO__](https://github.com/greenrobot/greenDAO) is an ORM optimized for Android: it maps database tables to Java objects and uses code generation for optimal speed. 

Changelog
=========
Version 2.0.0
-------------
First open source release.
