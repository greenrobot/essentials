# Essentials

[![Build Status](https://travis-ci.org/greenrobot/essentials.svg?branch=main)](https://travis-ci.org/greenrobot/essentials) 

Essentials are a collection of general-purpose classes we found useful in many occasions.

- Beats standard Java API performance, e.g. `LongHashMap` can be twice as fast as `HashMap<Long, Object>`.
- Adds missing pieces without pulling in heavy-weights like Guava  
- Improved convenience: do more with less code
- Super lightweight: < 100k in size
- Compatible with Android and Java

This project is bare bones compared to a rich menu offered by Guava or Apache Commons. Essentials is not a framework, it's rather a small set of utilities to make Java standard approaches more convenient or more efficient.

[Website][1] | [JavaDoc](http://greenrobot.org/files/essentials/javadoc/3.0/) | [Changelog][13]

## Features

- [Hash set][6] and [map][7] for primitive long keys outperform the generic versions of the Java Collection APIs
- [Multimaps][9] provide a map of lists or sets to simplify storing multiple values for a single key
- [Object cache][10] with powerful configuration options: soft/weak/strong references, maximum size, and time-based expiration
- [IO utilities][2] help with streams (byte and character based)
- [File utilities][3] simplify reading and writing strings/bytes/objects from or to files. Also includes getting hashes from files and copying files.
- [String utilities][4] allow efficient splitting and joining of strings, fast hex creation, and other useful string helpers.
- [Date utilities][5]
- [Better hash functions][12]: our Murmur3 implementation provides superior hash quality and outperforms standard [Java hash functions](web-resources/hash-functions-benchmark.pdf)
- Specialized Streams: for example an optimized [PipedOutputStream replacement][8] (based on a circular byte buffer)

Read more on our [website][14].

## Performance

Some classes where motivated by less than optimal performance offered by standard Java.

For long keys (also works for int), Essentials provides a specialized implementation, that can be twice as fast.

Here are some (completely non-scientific) benchmarking results running on Ubuntu 20.04 LTS using OpenJDK 11.0.9:   

| Essentials Class        | Java (seconds) | Essentials (seconds)  | Speed up |
|-------------------------|---------------:|----------------------:|:--------:|
| LongHashSet (Dynamic)   |         19.756 |                13.079 |   + 51%  |
| LongHashSet (Prealloc)  |         16.480 |                 8.171 |  + 102%  |
| LongHashMap (Dynamic)   |         20.311 |                14.659 |   + 39%  |
| LongHashMap (Prealloc)  |         17.496 |                 8.677 |  + 102%  |
| PipelineStream (1024KB) |          8.036 |                 1.424 |  + 564%  |
| StringHex (vs. Guava)   |          6.849 |                 3.732 |   + 84%  |

The benchmarking sources are available in the java-essentials-performance directory.

## Add the dependency to your project

For Gradle, you add this dependency (from repository `mavenCentral()`):
 
    implementation 'org.greenrobot:essentials:3.1.0'

And for Maven:
    
    <dependency>
        <groupId>org.greenrobot</groupId>
        <artifactId>essentials</artifactId>
        <version>3.1.0</version>
    </dependency>

## Code samples

Example code for some of the utility classes: 

```Java
// Get all bytes from stream and close the stream safely
byte[] bytes = IoUtils.readAllBytesAndClose(inputStream);

// Read the contents of an file as a string (use readBytes to get byte[])
String contents = FileUtils.readUtf8(file);

// How many days until new year's eve?
long time2 = DateUtils.getTimeForDay(2015, 12, 31);
int daysToNewYear = DateUtils.getDayDifference(time, time2);
```

Multimaps:
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

// Hash for the previous update calls. No conversion to byte[] necessary.
hash = murmur.getValue();
```
 
The utility classes are straight forward and don't have dependencies, so you should be fine to grasp them by having a look at their source code. Most of the method names should be self-explaining, and often you'll find JavaDocs where needed.

## Build setup

We use Gradle as a primary build system.
Previously, Maven is used to build greenrobot-common. Inside of [build-common](build-common), there are two parent POMs defined that might be useful: parent-pom and parent-pom-with-checks. The latter integrates FindBugs and Checkstyle in your build. Use it like this: 

    <parent>
        <groupId>de.greenrobot</groupId>
        <artifactId>parent-pom-with-checks</artifactId>
        <version>2.0.0</version>
        <relativePath></relativePath>
    </parent>

## License

Copyright (C) 2012-2020 Markus Junginger, greenrobot (https://greenrobot.org)

EventBus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).

# More by greenrobot

[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other. 
 
[__ObjectBox__](https://github.com/objectbox/objectbox-java) super-fast object database.

[1]: https://greenrobot.org/essentials
[2]: java-essentials/src/main/java/org/greenrobot/essentials/io/IoUtils.java
[3]: java-essentials/src/main/java/org/greenrobot/essentials/io/FileUtils.java
[4]: java-essentials/src/main/java/org/greenrobot/essentials/StringUtils.java
[5]: java-essentials/src/main/java/org/greenrobot/essentials/DateUtils.java
[6]: java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashSet.java
[7]: java-essentials/src/main/java/org/greenrobot/essentials/collections/LongHashMap.java
[8]: java-essentials/src/main/java/org/greenrobot/essentials/io/PipelineOutputStream.java
[9]: java-essentials/src/main/java/org/greenrobot/essentials/collections/Multimap.java
[10]: java-essentials/src/main/java/org/greenrobot/essentials/ObjectCache.java
[12]: https://greenrobot.org/essentials/features/performant-hash-functions-for-java/
[13]: https://greenrobot.org/essentials/changelog
[14]: https://greenrobot.org/essentials/features
