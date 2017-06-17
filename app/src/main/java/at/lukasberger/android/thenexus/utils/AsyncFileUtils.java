/*
 * The Nexus - ROM-Control for ROMs made by TeamNexus
 * Copyright (C) 2017  TeamNexus, Lukas Berger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.lukasberger.android.thenexus.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.chainfire.libsuperuser.Shell;

public class AsyncFileUtils {

    private final static Map<String, Object> filePathLocks = new HashMap<>();

    public static void touch(String path) {
        AsyncFileUtils.asyncIOOperation(path, "touch \"" + escapePath(path) + "\"");
    }

    public static void delete(String path) {
        delete(path, false);
    }

    public static void delete(String path, boolean force) {
        AsyncFileUtils.asyncIOOperation(path, "rm " + (force ? "-f" : "") + " \"" +escapePath(path) + "\"");
    }

    public static void write(String path, String content) {
        AsyncFileUtils.writeInternal(path, ">", content);
    }

    public static void writeSync(String path, String content) {
        AsyncFileUtils.writeSyncInternal(path, ">", content);
    }

    public static void write(String path, boolean content) {
        AsyncFileUtils.write(path, (content ? "1" : "0"));
    }

    public static void writeSync(String path, boolean content) {
        AsyncFileUtils.writeSync(path, (content ? "1" : "0"));
    }

    public static void write(String path, int content) {
        AsyncFileUtils.write(path, Integer.toString(content));
    }

    public static void writeSync(String path, int content) {
        AsyncFileUtils.writeSync(path, Integer.toString(content));
    }

    public static void write(String path, String[] content) {
        AsyncFileUtils.write(path, content[0]);
        for (int i = 1; i < content.length; i++)
            AsyncFileUtils.append(path, content[i]);
    }

    public static void writeSync(String path, String[] content) {
        AsyncFileUtils.writeSync(path, content[0]);
        for (int i = 1; i < content.length; i++)
            AsyncFileUtils.appendSync(path, content[i]);
    }

    public static void append(String path, String content) {
        AsyncFileUtils.writeInternal(path, ">>", content);
    }

    public static void appendSync(String path, String content) {
        AsyncFileUtils.writeSyncInternal(path, ">>", content);
    }

    public static List<String> read(String path) {
        return AsyncFileUtils.syncLockedIOOperation(path, "cat \"" + escapePath(path) + "\"");
    }

    public static String readLine(String path) {
        List<String> result = AsyncFileUtils.read(path);
        return (result == null ? null : result.get(0));
    }

    public static boolean readBoolean(String path) {
        return readBoolean(path, false);
    }

    public static boolean readBoolean(String path, boolean onlyTrueOnOne) {
        String result = AsyncFileUtils.readLine(path);
        if (result == null) {
            return false;
        }
        if (onlyTrueOnOne) {
            return result.equals("1");
        } else {
            return !result.equals("0");
        }
    }

    public static int readInt(String path, int def) {
        try {
            String result = AsyncFileUtils.readLine(path);
            if (result == null) {
                return def;
            }
            return Integer.parseInt(result);
        } catch (Exception ex) {
            return def;
        }
    }

    private static void writeInternal(String path, String operator, String content) {
        content = content.replace("\"", "\\\"");
        AsyncFileUtils.asyncIOOperation(path, "echo \"" + content + "\" " + operator + " \"" + escapePath(path) + "\"");
    }

    private static void writeSyncInternal(String path, String operator, String content) {
        content = content.replace("\"", "\\\"");
        AsyncFileUtils.syncLockedIOOperation(path, "echo \"" + content + "\" " + operator + " \"" + escapePath(path) + "\"");
    }

    private static List<String> syncLockedIOOperation(String path, String command) {
        final Object lock;

        synchronized (filePathLocks)
        {
            if (!filePathLocks.containsKey(path))
                filePathLocks.put(path, new Object());

            lock = filePathLocks.get(path);
        }

        synchronized (lock)
        {
            try {
                return Shell.SU.run(command);
            } catch (Exception e) {
                Log.e("TheNexus", "AsyncFileUtils.syncLockedIOOperation: failed to execute command: \"" + command + "\"", e);
            }
        }

        return null;
    }

    private static void asyncIOOperation(final String path, final String command) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                final Object lock;

                synchronized (filePathLocks)
                {
                    if (!filePathLocks.containsKey(path))
                        filePathLocks.put(path, new Object());

                    lock = filePathLocks.get(path);
                }

                synchronized (lock)
                {
                    try {
                        Shell.SU.run(command);
                    } catch (Exception e) {
                        Log.e("TheNexus", "AsyncFileUtils.asyncIOOperation: failed to execute command: \"" + command + "\"", e);
                    }
                }
            }

        });
        thread.start();
    }

    private static String escapePath(String path) {
        return path.replace("\"", "\\\"");
    }

}
