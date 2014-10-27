package de.greenrobot.common;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.NoSuchElementException;

public class ByteArrayUtils {
    private static final boolean BIG_ENDIAN;
    private static final boolean UNALIGNED;
    /** Set only if UNALIGNED == true. */
    private static final Unsafe UNSAFE;
    /** Set only if UNALIGNED == true. */
    private static final long BYTE_ARRAY_OFFSET;

    static {
        BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
        UNALIGNED = initUnaligned();

        if (UNALIGNED) {
            UNSAFE = initUnsafe();
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
        } else {
            UNSAFE = null;
            BYTE_ARRAY_OFFSET = 0;
        }
    }

    private static boolean initUnaligned() {
        boolean unaligned;
        try {
            Class<?> bitsClass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
            Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
            unalignedMethod.setAccessible(true);
            unaligned = Boolean.TRUE.equals(unalignedMethod.invoke(null));
        } catch (Throwable t) {
            String arch = System.getProperty("os.arch");
            // TODO some ARMs support it: http://infocenter.arm.com/help/index.jsp?topic=/com.arm.doc.faqs/ka15414.html
            unaligned = arch != null && arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
        }
        return unaligned;
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
    public static int getIntLE(byte[] bytes, int index) {
        if (UNSAFE != null) {
            int value = UNSAFE.getInt(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return Integer.reverseBytes(value);
            } else {
                return value;
            }
        } else {
            return getIntLEPlainJava(bytes, index);
        }
    }

    // separate method to enable unit test
    static int getIntLEPlainJava(byte[] bytes, int index) {
        return (bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8) |
                ((bytes[index + 2] & 0xff) << 16) | (bytes[index + 3] << 24);
    }

    /** Big endian. */
    public static int getIntBE(byte[] bytes, int index) {
        if (UNSAFE != null) {
            int value = UNSAFE.getInt(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return value;
            } else {
                return Integer.reverseBytes(value);
            }
        } else {
            return getIntBEPlainJava(bytes, index);
        }
    }

    // separate method to enable unit test
    static int getIntBEPlainJava(byte[] bytes, int index) {
        return (bytes[index + 3] & 0xff) | ((bytes[index + 2] & 0xff) << 8) |
                ((bytes[index + 1] & 0xff) << 16) | (bytes[index] << 24);
    }

    /** Little endian. */
    public static long getLongLE(byte[] bytes, int index) {
        if (UNSAFE != null) {
            long value = UNSAFE.getLong(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return Long.reverseBytes(value);
            } else {
                return value;
            }
        } else {
            return getLongLEPlainJava(bytes, index);
        }
    }

    // separate method to enable unit test
    static long getLongLEPlainJava(byte[] bytes, int index) {
        return (bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8) |
                ((bytes[index + 2] & 0xff) << 16) | ((bytes[index + 3] & 0xffL) << 24) |
                ((bytes[index + 4] & 0xffL) << 32) | ((bytes[index + 5] & 0xffL) << 40) |
                ((bytes[index + 6] & 0xffL) << 48) | (((long) bytes[index + 7]) << 56);
    }

    /** Big endian. */
    public static long getLongBE(byte[] bytes, int index) {
        if (UNSAFE != null) {
            long value = UNSAFE.getLong(bytes, BYTE_ARRAY_OFFSET + index);
            if (BIG_ENDIAN) {
                return value;
            } else {
                return Long.reverseBytes(value);
            }
        } else {
            return getLongBEPlainJava(bytes, index);
        }
    }

    // separate method to enable unit test
    static long getLongBEPlainJava(byte[] bytes, int index) {
        return (bytes[index + 7] & 0xff) | ((bytes[index + 6] & 0xff) << 8) |
                ((bytes[index + 5] & 0xff) << 16) | ((bytes[index + 4] & 0xffL) << 24) |
                ((bytes[index + 3] & 0xffL) << 32) | ((bytes[index + 2] & 0xffL) << 40) |
                ((bytes[index + 1] & 0xffL) << 48) | (((long) bytes[index]) << 56);
    }

}
