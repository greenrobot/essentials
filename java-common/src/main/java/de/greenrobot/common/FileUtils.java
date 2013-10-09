package de.greenrobot.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utils for dealing with files and streams.
 * 
 * @author Markus
 */
public class FileUtils {
    public static Object readObjectFromFile(String filename) throws IOException,
            ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
        try {
            return in.readObject();
        } finally {
            safeClose(in);
        }
    }

    public static void writeObjectToFile(String filename, Object object) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
        try {
            out.writeObject(object);
            out.flush();
            fileOut.getFD().sync();
        } finally {
            safeClose(out);
        }
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    public static byte[] readAllBytesAndClose(InputStream is) throws IOException {
        try {
            return readAllBytes(is);
        } finally {
            safeClose(is);
        }
    }

    public static byte[] readAllBytes(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return readAllBytesAndClose(is);
    }

    public static byte[] readAllBytes(String filename) throws IOException {
        FileInputStream is = new FileInputStream(filename);
        return readAllBytesAndClose(is);
    }

    public static String readAllChars(Reader reader) throws IOException {
        char[] buffer = new char[2048];
        StringBuilder builder = new StringBuilder();
        while (true) {
            int read = reader.read(buffer);
            if (read == -1) {
                break;
            }
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    public static String readAllCharsAndClose(Reader reader) throws IOException {
        try {
            return readAllChars(reader);
        } finally {
            safeClose(reader);
        }
    }

    public static void writeBytes(File file, byte[] content) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            out.write(content);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    /** @return MD5 digest (32 characters). */
    public static String getDigestMd5(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            return getDigestMd5(in);
        } finally {
            safeClose(in);
        }
    }

    /** @return SHA-1 digest (40 characters). */
    public static String getDigestSha1(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            return getDigestSha1(in);
        } finally {
            safeClose(in);
        }
    }

    /** @return MD5 digest (32 characters). */
    public static String getDigestMd5(InputStream in) throws IOException {
        byte[] digest = getDigest(in, "MD5");
        return StringUtils.toHexString(digest, 32);
    }

    /** @return SHA-1 digest (40 characters). */
    public static String getDigestSha1(InputStream in) throws IOException {
        byte[] digest = getDigest(in, "SHA-1");
        return StringUtils.toHexString(digest, 40);
    }

    public static byte[] getDigest(InputStream in, String digestAlgo) throws IOException {
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance(digestAlgo);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }

        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            digester.update(buffer, 0, read);
        }
        return digester.digest();
    }

    /**
     * Copies all available data from in to out without closing any stream.
     * 
     * @return number of bytes copied
     */
    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }


    /** Closes the given stream inside a try/catch. Does nothing if stream is null. */
    public static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    /** Closes the given stream inside a try/catch. Does nothing if stream is null. */
    public static void safeClose(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    /** Closes the given stream inside a try/catch. Does nothing if stream is null. */
    public static void safeClose(Reader in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    /** Closes the given stream inside a try/catch. Does nothing if stream is null. */
    public static void safeClose(Writer out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    /** Copies a file to another location. */
    public static void copyFile(File from, File to) throws IOException {
        InputStream in = new FileInputStream(from);
        try {
            OutputStream out = new FileOutputStream(to);
            try {
                copyAllBytes(in, out);
            } finally {
                safeClose(out);
            }
        } finally {
            safeClose(in);
        }
    }

    /** Copies a file to another location. */
    public static void copyFile(String fromFilename, String toFilename) throws IOException {
        copyFile(new File(fromFilename), new File(toFilename));
    }

}
