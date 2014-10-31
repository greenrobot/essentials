package de.greenrobot.common.checksum;

import java.math.BigInteger;
import java.util.zip.Checksum;

/** Checksum interface to access 128 bit in various ways. */
public interface Checksum128 extends Checksum {
    /** Returns the higher 64 bits of the 128 bit hash. */
    public long getValueHigh();

    /** Positive value. */
    public BigInteger getValueBigInteger();

    /** Padded with leading 0s to ensure length of 32. */
    public String getValueHexString();

    /** Big endian is the default in Java / network byte order. */
    public byte[] getValueBytesBigEndian();

    /** Big endian is used by most machines natively. */
    public byte[] getValueBytesLittleEndian();
}
