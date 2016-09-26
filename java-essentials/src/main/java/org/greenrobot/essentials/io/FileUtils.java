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

package org.greenrobot.essentials.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.Checksum;

/**
 * Utils for dealing with files.
 *
 * @author Markus
 */
public class FileUtils {

    public static byte[] readBytes(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return IoUtils.readAllBytesAndClose(is);
    }

    public static void writeBytes(File file, byte[] content) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            out.write(content);
        } finally {
            IoUtils.safeClose(out);
        }
    }

    public static String readUtf8(File file) throws IOException {
        return readChars(file, "UTF-8");
    }

    public static String readChars(File file, String charset) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(file), charset);
        return IoUtils.readAllCharsAndClose(reader);
    }

    public static void writeUtf8(File file, CharSequence text) throws IOException {
        writeChars(file, "UTF-8", text, false);
    }

    public static void appendUtf8(File file, CharSequence text) throws IOException {
        writeChars(file, "UTF-8", text, true);
    }

    public static void writeChars(File file, String charset, CharSequence text, boolean apppend) throws IOException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file, apppend), charset);
        IoUtils.writeAllCharsAndClose(writer, text);
    }

    /** Copies a file to another location. */
    public static void copyFile(File from, File to) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(from));
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            try {
                IoUtils.copyAllBytes(in, out);
            } finally {
                IoUtils.safeClose(out);
            }
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /** Copies a file to another location. */
    public static void copyFile(String fromFilename, String toFilename) throws IOException {
        copyFile(new File(fromFilename), new File(toFilename));
    }

    /** To read an object in a quick & dirty way. Prepare to handle failures when object serialization changes! */
    public static Object readObject(File file) throws IOException,
            ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
        try {
            return in.readObject();
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /** To store an object in a quick & dirty way. */
    public static void writeObject(File file, Object object) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
        try {
            out.writeObject(object);
            out.flush();
            // Force sync
            fileOut.getFD().sync();
        } finally {
            IoUtils.safeClose(out);
        }
    }

    /** @return MD5 digest (32 characters). */
    public static String getMd5(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return IoUtils.getMd5(in);
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /** @return SHA-1 digest (40 characters). */
    public static String getSha1(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return IoUtils.getSha1(in);
        } finally {
            IoUtils.safeClose(in);
        }
    }

    public static void updateChecksum(File file, Checksum checksum) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            IoUtils.updateChecksum(in, checksum);
        } finally {
            IoUtils.safeClose(in);
        }
    }

}
