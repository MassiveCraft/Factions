package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private final static String UTF8 = "UTF-8";

    // -------------------------------------------- //
    // BYTE
    // -------------------------------------------- //


    public static byte[] readBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] output = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        while (offset < length) {
            offset += in.read(output, offset, (length - offset));
        }
        in.close();
        return output;
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    // -------------------------------------------- //
    // STRING
    // -------------------------------------------- //

    public static void write(File file, String content) throws IOException {
        writeBytes(file, utf8(content));
    }

    public static String read(File file) throws IOException {
        return utf8(readBytes(file));
    }

    // -------------------------------------------- //
    // CATCH
    // -------------------------------------------- //

    private static HashMap<String, Lock> locks = new HashMap<String, Lock>();

    public static boolean writeCatch(final File file, final String content, boolean sync) {
        final byte[] bytes = utf8(content);
        String name = file.getName();
        final Lock lock;

        // Create lock for each file if there isn't already one.
        if (locks.containsKey(name)) {
            lock = locks.get(name);
        } else {
            ReadWriteLock rwl = new ReentrantReadWriteLock();
            lock = rwl.writeLock();
            locks.put(name, lock);
        }

        if (sync) {
            lock.lock();
            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(bytes);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(bytes);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        return true; // don't really care but for some reason this is a boolean.
    }

    public static String readCatch(File file) {
        try {
            return read(file);
        } catch (IOException e) {
            return null;
        }
    }

    // -------------------------------------------- //
    // UTF8 ENCODE AND DECODE
    // -------------------------------------------- //

    public static byte[] utf8(String string) {
        try {
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String utf8(byte[] bytes) {
        try {
            return new String(bytes, UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}