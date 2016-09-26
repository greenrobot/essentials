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

package org.greenrobot.essentials;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.NoSuchElementException;

/**
 * Retrieves int and long values from byte arrays using sun.misc.Unsafe for fast access.
 */
// TODO Test on a big endian machine with Unsafe
public abstract class PrimitiveArrayUtils {
    private final static PrimitiveArrayUtils instance;
    private final static PrimitiveArrayUtils instanceSafe;

    static {
        instanceSafe = new SafeImpl();
        PrimitiveArrayUtils unsafeImpl = null;
        try {
            if (UnsafeImpl.UNSAFE != null) {
                unsafeImpl = new UnsafeImpl();
            }
        } catch (Throwable th) {
            // Ignore
        }
        instance = unsafeImpl != null ? unsafeImpl : instanceSafe;
    }

    public static PrimitiveArrayUtils getInstance() {
        return instance;
    }

    public static PrimitiveArrayUtils getInstanceSafe() {
        return instanceSafe;
    }

    public abstract int getIntLE(byte[] bytes, int index);

    public abstract int getIntBE(byte[] bytes, int index);

    public abstract long getLongLE(byte[] bytes, int index);

    public abstract long getLongBE(byte[] bytes, int index);

    public abstract int getIntLE(char[] chars, int index);

    private static class UnsafeImpl extends PrimitiveArrayUtils {
        private static final boolean BIG_ENDIAN;
        private static final boolean UNALIGNED;
        /** Set only if UNALIGNED == true. */
        private static final Unsafe UNSAFE;
        /** Set only if UNALIGNED == true. */
        private static final long BYTE_ARRAY_OFFSET;
        /** Set only if UNALIGNED == true. */
        private static final long CHAR_ARRAY_OFFSET;

        static {
            BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
            UNALIGNED = initUnaligned();

            if (UNALIGNED) {
                UNSAFE = initUnsafe();
                BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
                CHAR_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(char[].class);
            } else {
                UNSAFE = null;
                BYTE_ARRAY_OFFSET = 0;
                CHAR_ARRAY_OFFSET = 0;
            }
        }

        private static boolean initUnaligned() {
            // http://developer.android.com/reference/java/lang/System.html#getProperty(java.lang.String)
            String javaVendor = System.getProperty("java.vendor");
            boolean isAndroid = javaVendor != null ? javaVendor.contains("Android") : false;
            if (isAndroid) {
                // java.nio.Bits is a Java-only internal class, avoid it: some Genymotion VM did SIGSEGV on querying it
                return guessUnalignedFromOsArch();
            } else {
                try {
                    Class<?> bitsClass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
                    Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
                    unalignedMethod.setAccessible(true);
                    return Boolean.TRUE.equals(unalignedMethod.invoke(null));
                } catch (Throwable t) {
                    return guessUnalignedFromOsArch();
                }
            }
        }

        private static boolean guessUnalignedFromOsArch() {
            String arch = System.getProperty("os.arch");
            // TODO some ARM CPUs support it, too:
            // http://infocenter.arm.com/help/index.jsp?topic=/com.arm.doc.faqs/ka15414.html
            return arch != null && arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
        }

        private static Unsafe initUnsafe() {
            try {
                Field unsafeField;
                try {
                    unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                } catch (NoSuchElementException e) {
                    // For older Android version
                    unsafeField = Unsafe.class.getDeclaredField("THE_ONE");
                }
                unsafeField.setAccessible(true);
                Unsafe unsafe = (Unsafe) unsafeField.get(null);

                byte[] test = {(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
                int testInt = unsafe.getInt(test, (long) unsafe.arrayBaseOffset(byte[].class));
                if (testInt == 0xcafebabe) {
                    if (BIG_ENDIAN) {
                        return unsafe;
                    } else {
                        System.err.println("Big endian confusion");
                    }
                } else if (testInt == 0xbebafeca) {
                    if (!BIG_ENDIAN) {
                        return unsafe;
                    } else {
                        System.err.println("Little endian confusion");
                    }
                }
            } catch (Throwable e) {
                // Ignore
            }
            return null;
        }

        /** Little endian. */
        public int getIntLE(byte[] bytes, int index) {
            int value = UNSAFE.getInt(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return Integer.reverseBytes(value);
            } else {
                return value;
            }
        }

        /** Little endian. */
        public int getIntLE(char[] chars, int index) {
            int value = UNSAFE.getInt(chars, CHAR_ARRAY_OFFSET + (index << 2));
            if (BIG_ENDIAN) {
                return Integer.reverseBytes(value);
            } else {
                return value;
            }
        }


        /** Big endian. */
        public int getIntBE(byte[] bytes, int index) {
            int value = UNSAFE.getInt(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return value;
            } else {
                return Integer.reverseBytes(value);
            }
        }

        /** Little endian. */
        public long getLongLE(byte[] bytes, int index) {
            long value = UNSAFE.getLong(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return Long.reverseBytes(value);
            } else {
                return value;
            }
        }

        /** Big endian. */
        public long getLongBE(byte[] bytes, int index) {
            long value = UNSAFE.getLong(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return value;
            } else {
                return Long.reverseBytes(value);
            }
        }
    }

    private static class SafeImpl extends PrimitiveArrayUtils {
        public int getIntLE(byte[] bytes, int index) {
            return (bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8) |
                    ((bytes[index + 2] & 0xff) << 16) | (bytes[index + 3] << 24);
        }

        public int getIntBE(byte[] bytes, int index) {
            return (bytes[index + 3] & 0xff) | ((bytes[index + 2] & 0xff) << 8) |
                    ((bytes[index + 1] & 0xff) << 16) | (bytes[index] << 24);
        }

        public long getLongLE(byte[] bytes, int index) {
            return (bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8) |
                    ((bytes[index + 2] & 0xff) << 16) | ((bytes[index + 3] & 0xffL) << 24) |
                    ((bytes[index + 4] & 0xffL) << 32) | ((bytes[index + 5] & 0xffL) << 40) |
                    ((bytes[index + 6] & 0xffL) << 48) | (((long) bytes[index + 7]) << 56);
        }

        public long getLongBE(byte[] bytes, int index) {
            return (bytes[index + 7] & 0xff) | ((bytes[index + 6] & 0xff) << 8) |
                    ((bytes[index + 5] & 0xff) << 16) | ((bytes[index + 4] & 0xffL) << 24) |
                    ((bytes[index + 3] & 0xffL) << 32) | ((bytes[index + 2] & 0xffL) << 40) |
                    ((bytes[index + 1] & 0xffL) << 48) | (((long) bytes[index]) << 56);
        }

        /** Little endian. */
        public int getIntLE(char[] chars, int index) {
            return (chars[index] & 0xffff) | ((chars[index + 1] & 0xffff) << 16);
        }

    }

}
