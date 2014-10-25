Java hashing utilities
======================
This project aims improve the hashing situation in Java by providing:
* Comparisons of hash functions for Java
* FNV-1a implementations for 32 and 64 bit hashes
* Utility classes for convenience  
* Compatibility with java.util.zip.Checksum interface

The problem with hash functions for Java
========================================
Non-cryptographic hash function are important building block of software. The selection of available hash functions is plenty, and in the last decade, many new hash functions emerged with very good hashing properties.

Surprisingly, the core Java API just offers Adler32 and CRC32, which were designed as checksums many years ago. Of course, there are many hash implementations available outside of the core Java API. Unlike in the C world, there are hardly any comparisons available. The hashing algorithms have very different performance characteristics, when the run inside of a Java VM. Today's fastest hashes are highly optimized against CPU hardware, and can perform at several GB/s. The VM layer imposed by Java can get in the way here. For example fast native hash functions can outperform CRC32 by a magnitude. But the same hash functions implemented in Java can be several times slower than Java's CRC32 class.

64 bit hashes are totally absent in the Java core APIs. This is unfortunate, because 64 bit hashes provide much(!) less collisions than their 32 bit counterparts. In contrast to cryptographic hash functions, they are shorter and faster.        

FNV-1a hash function
====================
We chose FNV because it is very simple to implement and runs reasonable fast in the Java VM. Our FNV-1a implementation has unit tests for correctness and use primitive data types for decent speed. Other implementations we tested produced wrong hash values, or were inefficient (e.g. usage of BigDecimal).    

You will find the classes FNV32 and FNV64 in the de.greenrobot.common.checksum package. Both implement java.util.zip.Checksum interface, so they are straight forward to use.

Utility classes
===============
Hash and checksum functions usually only accept bytes as input. Our class DataChecksum transforms shorts, ints, longs, Strings, and arrays on the fly to bytes. It's a wrapper around Java's Checksum interface and can thus be used with our FNV32 and FNV64 classes (or Java's Adler32/CRC32).
 
The class CombinedChecksum takes two Checksum objects (preferably 32 bit) and combines their hashes into a 64 bit hash. CombinedChecksum implements Checksum itself. This class can be useful to work around flaws in hashing functions.

Comparison Results
==================


Other comparisons
=================
http://www.strchr.com/hash_functions
http://programmers.stackexchange.com/questions/49550/which-hashing-algorithm-is-best-for-uniqueness-and-speed
http://research.neustar.biz/2012/02/02/choosing-a-good-hash-function-part-3/

From xxHash:
<table>
  <tr>
    <th>Name</th><th>Speed</th><th>Q.Score</th><th>Author</th>
  </tr>
  <tr>
    <th>xxHash</th><th>5.4 GB/s</th><th>10</th><th>Y.C.</th>
  </tr>
  <tr>
    <th>MumurHash 3a</th><th>2.7 GB/s</th><th>10</th><th>Austin Appleby</th>
  </tr>
  <tr>
    <th>SBox</th><th>1.4 GB/s</th><th>9</th><th>Bret Mulvey</th>
  </tr>
  <tr>
    <th>Lookup3</th><th>1.2 GB/s</th><th>9</th><th>Bob Jenkins</th>
  </tr>
  <tr>
    <th>CityHash64</th><th>1.05 GB/s</th><th>10</th><th>Pike & Alakuijala</th>
  </tr>
  <tr>
    <th>FNV</th><th>0.55 GB/s</th><th>5</th><th>Fowler, Noll, Vo</th>
  </tr>
  <tr>
    <th>CRC32</th><th>0.43 GB/s</th><th>9</th><th></th>
  </tr>
  <tr>
    <th>SipHash</th><th>0.34 GB/s</th><th>10</th><th>Jean-Philippe Aumasson</th>
  </tr>
  <tr>
    <th>MD5-32</th><th>0.33 GB/s</th><th>10</th><th>Ronald L. Rivest</th>
  </tr>
  <tr>
    <th>SHA1-32</th><th>0.28 GB/s</th><th>10</th><th></th>
  </tr>
</table>

Q.Score is a measure of quality of the hash function.
It depends on successfully passing SMHasher test set.
10 is a perfect score.

http://jpountz.github.io/lz4-java/1.2.0/xxhash-benchmark/
http://blog.reverberate.org/2012/01/state-of-hash-functions-2012.html
http://floodyberry.com/noncryptohashzoo/


Hash function evaluation
========================
SMHasher was written by the author of Murmur hash to test variuos properties of hash functions. It's an excellent tool to detect flaws.
https://code.google.com/p/smhasher/wiki/SMHasher

Other interesting hash functions
--------------------------------
xxHash claims to be blazing fast for current x86 while having great hashing properties:
https://code.google.com/p/xxhash/

http://en.wikipedia.org/wiki/CityHash

http://en.wikipedia.org/wiki/Jenkins_hash_function

SipHash 64 bits has a secret 128 key and claims to be strong against DoS attacks:
http://en.wikipedia.org/wiki/SipHash

