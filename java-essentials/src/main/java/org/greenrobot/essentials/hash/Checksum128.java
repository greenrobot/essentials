/*
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.greenrobot.essentials.hash;

import java.math.BigInteger;
import java.util.zip.Checksum;

/** Checksum interface to access 128 bit in various ways. */
public interface Checksum128 extends Checksum {
    /** Returns the higher 64 bits of the 128 bit hash. */
    long getValueHigh();

    /** Positive value. */
    BigInteger getValueBigInteger();

    /** Padded with leading 0s to ensure length of 32. */
    String getValueHexString();

    /** Big endian is the default in Java / network byte order. */
    byte[] getValueBytesBigEndian();

    /** Big endian is used by most machines natively. */
    byte[] getValueBytesLittleEndian();
}
