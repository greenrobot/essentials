Essentials
==========
greenrobot's Essentials library provides general purpose utilities for Android and Java projects. Having its root in the early days of Android development, the library is minimalistic, tiny in size (jar < 100k), and focuses on efficiency and performance.

[![Build Status](https://travis-ci.org/greenrobot/essentials.svg?branch=master)](https://travis-ci.org/greenrobot/essentials)

Features
--------
See [Website](http://greenrobot.org/essentials/features/).

The Utility classes cover [stream-based IO](java-essentials/src/main/java/org/greenrobot/essentials/io/IoUtils.java), [files](java-essentials/src/main/java/org/greenrobot/essentials/io/FileUtils.java), [strings](java-essentials/src/main/java/org/greenrobot/essentials/StringUtils.java), and [date/time](java-essentials/src/main/java/org/greenrobot/essentials/DateUtils.java). There are also efficient [hash map](java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashMap.java) and [hash set](java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashSet.java) implementation for primitive long keys.
   
Another important part of Essentials are [hash functions](hash-functions.md). Our Murmur3A&F implementations are the fastest Java implementations known to us. Murmur3 hash functions are one of today's best hash functions available with excellent properties. At 3,6 GByte/s, we measured our Murmur3F implementation to outperform Guava's by factor 10. Find more information on the [hash functions page](hash-functions.md) or jump directly to our [Java hash function benchmark PDF](web-resources/hash-functions-benchmark.pdf).

Speaking of Guava, this project is bare bones compared to a rich menu offered by Guava or Apache Commons. Essentials is not a framework, it's rather a small set of utilities to make Java standard approaches more convenient or more efficient.

How to integrate in your project
--------------------------------
Just grab it from Maven Central (or jCenter). For Gradle, add the following dependency:

    compile 'de.greenrobot:java-common:2.3.1'

And for Maven:
    
    <dependency>
        <groupId>de.greenrobot</groupId>
        <artifactId>java-common</artifactId>
        <version>2.3.1</version>
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

Multimaps (added in V2.2):
```Java
ListMap<String,String> multimap = new ListMap<>();
multimap.putElement("a", "1");
multimap.putElement("a", "2");
multimap.putElement("a", "3");
List<String> strings = multimap.get("a"); // Contains "1", "2", and "3"
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

Homepage, Links
---------------
For more details on greenrobot Essentials please check the [Essentials' website](http://greenrobot.org/essentials/). Here are some direct links you may find useful:

[Features](http://greenrobot.org/essentials/features/)

[Changelog](http://greenrobot.org/essentials/changelog/)


License
-------
Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)

EventBus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).

More Open Source by greenrobot
==============================
[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other. 
 
[__greenDAO__](https://github.com/greenrobot/greenDAO) is an ORM optimized for Android: it maps database tables to Java objects and uses code generation for optimal speed.

[Follow us on Google+](https://plus.google.com/b/114381455741141514652/+GreenrobotDe/posts) to stay up to date.

