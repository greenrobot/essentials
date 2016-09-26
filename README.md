# Essentials

[![Build Status](https://travis-ci.org/greenrobot/essentials.svg?branch=master)](https://travis-ci.org/greenrobot/essentials) 

Essentials are a collection of general-purpose classes we found useful in many occasions.

- Write less code in your projects
- Specialized implementations for better performance compared to standard APIs
- Super light weight: < 100k in size
- Compatible with Android and Java

This project is bare bones compared to a rich menu offered by Guava or Apache Commons. Essentials is not a framework, it's rather a small set of utilities to make Java standard approaches more convenient or more efficient.

[Website][1]

[JavaDoc (3.0 release candidate)](http://greenrobot.org/files/essentials/javadoc/3.0/)

[Changelog][13]

## Features

- [IO utilities][2] help with streams (byte and character based) and digests (e.g. MD5 and SHA-1).
- [File utilities][3] simplify reading and writing strings/bytes/objects from or to files. Also includes getting hashes from files and copying files.
- [String utilities][4] allow efficient splitting and joining of strings, hex and MD5 creation, and other useful string helpers.
- [Date utilities][5].
- [Better hash functions][12]: our Murmur3 implementation provides superior hash quality and outperforms standard [Java hash functions](web-resources/hash-functions-benchmark.pdf)
- Specialized Streams: for example an optimized [PipedOutputStream replacement][8] (based on a circular byte buffer)
- [Hash set][6] and [map][7] for primitive long keys outperform the generic versions of the Java Collection APIs
- [Multimaps][9] provide a map of lists or sets to simplify storing multiple values for a single key
- [Object cache][10] with powerful configuration options: soft/weak/strong references, maximum size, and time-based expiration
- [Base64][11] implementation (bundled from iharder.net) for lower versions of Java & Android (includes input/output streams)

Read more on our [website][14].

## Add the dependency to your project

You may also try the 3.0 release candidate with additional features and a cleaned up API:
 
    compile 'org.greenrobot:essentials:3.0.0-RC1'

Or, grab the older version:

    compile 'de.greenrobot:java-common:2.3.1'

And for Maven:
    
    <dependency>
        <groupId>de.greenrobot</groupId>
        <artifactId>java-common</artifactId>
        <version>2.3.1</version>
    </dependency>

## Code samples (V2.3.1)

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

[Our hash functions][12] implement [java.util.zip.Checksum](http://docs.oracle.com/javase/8/docs/api/java/util/zip/Checksum.html), so this code might look familiar to you:

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
 
The utility classes are straight forward and don't have dependencies, so you should be fine to grasp them by having a look at their source code. Most of the method names should be self-explaining, and often you'll find JavaDocs where needed.

## Build setup

Currently, Maven is used to build greenrobot-common. Inside of [build-common](build-common), there are two parent POMs defined that might be useful: parent-pom and parent-pom-with-checks. The latter integrates FindBugs and Checkstyle in your build. Use it like this: 

    <parent>
        <groupId>de.greenrobot</groupId>
        <artifactId>parent-pom-with-checks</artifactId>
        <version>2.0.0</version>
        <relativePath></relativePath>
    </parent>

## License

Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)

EventBus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).

# More Open Source by greenrobot

[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other. 
 
[__greenDAO__](https://github.com/greenrobot/greenDAO) is an ORM optimized for Android: it maps database tables to Java objects and uses code generation for optimal speed.

[Follow us on Google+](https://plus.google.com/b/114381455741141514652/+GreenrobotDe/posts) to stay up to date.


[1]: http://greenrobot.org/essentials
[2]: java-essentials/src/main/java/org/greenrobot/essentials/io/IoUtils.java
[3]: java-essentials/src/main/java/org/greenrobot/essentials/io/FileUtils.java
[4]: java-essentials/src/main/java/org/greenrobot/essentials/StringUtils.java
[5]: java-essentials/src/main/java/org/greenrobot/essentials/DateUtils.java
[6]: java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashSet.java
[7]: java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashMap.java
[8]: java-essentials/src/main/java/org/greenrobot/essentials/io/PipelineOutputStream.java
[9]: java-essentials/src/main/java/org/greenrobot/essentials/collections/Multimap.java
[10]: java-essentials/src/main/java/org/greenrobot/essentials/ObjectCache.java
[11]: java-essentials/src/main/java/org/greenrobot/essentials/Base64.java
[12]: http://greenrobot.org/essentials/features/performant-hash-functions-for-java/
[13]: http://greenrobot.org/essentials/changelog
[14]: http://greenrobot.org/essentials/features
