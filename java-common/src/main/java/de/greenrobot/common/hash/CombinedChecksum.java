/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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

package de.greenrobot.common.hash;

import java.util.zip.Checksum;

/** Combines two 32 bit hashes into a 64 bit hash.*/
public class CombinedChecksum implements Checksum {
    private final Checksum checksum1;
    private final Checksum checksum2;

    public CombinedChecksum(Checksum checksum1, Checksum checksum2) {
        this.checksum1 = checksum1;
        this.checksum2 = checksum2;
    }

    @Override
    public void update(int b) {
        checksum1.update(b);
        checksum2.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        checksum1.update(b, off, len);
        checksum2.update(b, off, len);
    }

    @Override
    public long getValue() {
        return (checksum2.getValue() << 32) | (checksum1.getValue() & 0xffffffffL);
    }

    @Override
    public void reset() {
        checksum1.reset();
        checksum2.reset();
    }
}
