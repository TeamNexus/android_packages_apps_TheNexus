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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class AsyncFileUtils {

    private final static Object rootCommandLock = new Object();

    public static void touch(String path) {
        AsyncFileUtils.asyncRootCommand("touch \"" + escapeQuotes(path) + "\"");
    }

    public static void delete(String path) {
        AsyncFileUtils.delete(path, false);
    }

    public static void delete(String path, boolean force) {
        AsyncFileUtils.asyncRootCommand("rm " + (force ? "-f" : "") + " \"" + escapeQuotes(path) + "\"");
    }

    public static void write(String path, boolean content) {
        AsyncFileUtils.write(path, (content ? "1" : "0"));
    }

    public static void writeSync(String path, boolean content) {
        AsyncFileUtils.writeSync(path, (content ? "1" : "0"));
    }

    public static void write(String path, short content) {
        AsyncFileUtils.write(path, Short.toString(content));
    }

    public static void writeSync(String path, short content) {
        AsyncFileUtils.writeSync(path, Short.toString(content));
    }

    public static void write(String path, int content) {
        AsyncFileUtils.write(path, Integer.toString(content));
    }

    public static void writeSync(String path, int content) {
        AsyncFileUtils.writeSync(path, Integer.toString(content));
    }

    public static void write(String path, long content) {
        AsyncFileUtils.write(path, Long.toString(content));
    }

    public static void writeSync(String path, long content) {
        AsyncFileUtils.writeSync(path, Long.toString(content));
    }

    public static void write(String path, String content) {
        AsyncFileUtils.write(path, new String[] { content });
    }

    public static void writeSync(String path, String content) {
        AsyncFileUtils.writeSync(path, new String[] { content });
    }

    public static void write(String path, String[] content) {
        AsyncFileUtils.asyncRootCommand(generateWriteCommand(path, content));
    }

    public static void writeSync(String path, String[] content) {
        AsyncFileUtils.syncRootCommand(generateWriteCommand(path, content));
    }

    public static void append(String path, String content) {
        AsyncFileUtils.asyncRootCommand("echo \"" + escapeQuotes(content) + "\" >> \"" + escapeQuotes(path) + "\"");
    }

    public static void appendSync(String path, String content) {
        AsyncFileUtils.syncRootCommand("echo \"" + escapeQuotes(content) + "\" >> \"" + escapeQuotes(path) + "\"");
    }

    public static void appendSystem(String path, String content) {
        AsyncFileUtils.asyncSystemCommand("echo \"" + escapeQuotes(content) + "\" >> \"" + escapeQuotes(path) + "\"");
    }

    public static void appendSystemSync(String path, String content) {
        AsyncFileUtils.syncSystemCommand("echo \"" + escapeQuotes(content) + "\" >> \"" + escapeQuotes(path) + "\"");
    }

    public static List<String> read(String path) {
        return AsyncFileUtils.syncRootCommand("cat \"" + escapeQuotes(path) + "\"");
    }

    @Nullable
    public static String readString(String path) {
        List<String> result = AsyncFileUtils.read(path);
        return (result == null ? null : result.get(0));
    }

    public static boolean readBoolean(String path) {
        return readBoolean(path, false);
    }

    public static boolean readBoolean(String path, boolean onlyTrueOnOne) {
        String result = AsyncFileUtils.readString(path);
        if (result == null) {
            return false;
        }
        if (onlyTrueOnOne) {
            return result.equals("1");
        } else {
            return !result.equals("0");
        }
    }

    public static int readInteger(String path, int def) {
        try {
            String result = AsyncFileUtils.readString(path);
            if (result == null) {
                return def;
            }
            return Integer.parseInt(result);
        } catch (Exception ex) {
            return def;
        }
    }

    public static void replaceRootLine(String path, String str, int line) {
        AsyncFileUtils.asyncRootCommand("sed -i '" + line + "s/.*/" + escapeQuotes(str) + "/' " + escapeQuotes(path));
    }

    public static void replaceRootLineSync(String path, String str, int line) {
        AsyncFileUtils.syncRootCommand("sed -i '" + line + "s/.*/" + escapeQuotes(str) + "/' " + escapeQuotes(path));
    }

    public static void replaceSystemLine(String path, String str, int line) {
        AsyncFileUtils.asyncSystemCommand("sed -i '" + line + "s/.*/" + escapeQuotes(str) + "/' " + escapeQuotes(path));
    }

    public static void replaceSystemLineSync(String path, String str, int line) {
        AsyncFileUtils.syncSystemCommand("sed -i '" + line + "s/.*/" + escapeQuotes(str) + "/' " + escapeQuotes(path));
    }

    public static void removeRootLine(String path, int line) {
        AsyncFileUtils.asyncRootCommand("sed -i '" + line + "d' " + escapeQuotes(path));
    }

    public static void removeRootLineSync(String path, int line) {
        AsyncFileUtils.syncRootCommand("sed -i '" + line + "d' " + escapeQuotes(path));
    }

    public static void removeSystemLine(String path, int line) {
        AsyncFileUtils.asyncSystemCommand("sed -i '" + line + "d' " + escapeQuotes(path));
    }

    public static void removeSystemLineSync(String path, int line) {
        AsyncFileUtils.syncSystemCommand("sed -i '" + line + "d' " + escapeQuotes(path));
    }

    public static int getFileLinesCount(String path) {
        return Integer.parseInt(AsyncFileUtils.syncSystemCommand("sed -n '$=' " + escapeQuotes(path)).get(0));
    }

    @NonNull
    private static String generateWriteCommand(String path, String[] content) {
        String commands = "";
        commands += "echo \"" + escapeQuotes(content[0]) + "\" > \"" + escapeQuotes(path) + "\" && ";
        for (int i = 1; i < content.length; i++)
            commands += "echo \"" + escapeQuotes(content[i]) + "\" >> \"" + escapeQuotes(path) + "\" && ";

        return commands.substring(0, commands.length() - 4);
    }

    @Nullable
    private static List<String> syncRootCommand(String command) {
        try {
            synchronized (rootCommandLock) {
                Shell.SU.clearCachedResults();
                return Shell.SU.run(command);
            }
        } catch (Exception e) {
            Log.e("TheNexus", "AsyncFileUtils.syncLockedIOOperation: failed to execute command: \"" + command + "\"", e);
        }

        return null;
    }

    @Nullable
    private static List<String> syncSystemCommand(String command) {
        try {
            synchronized (rootCommandLock) {
                Shell.SU.clearCachedResults();

                /* Remount /system as read-write */
                remountSystem(true);

                /* Execute command */
                List<String> result = Shell.SU.run(command);

                /* Remount /system as read-only */
                remountSystem(false);

                return result;
            }
        } catch (Exception e) {
            Log.e("TheNexus", "AsyncFileUtils.syncLockedIOOperation: failed to execute command: \"" + command + "\"", e);
        }

        return null;
    }

    private static void asyncRootCommand(final String command) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncFileUtils.syncRootCommand(command);
            }
        });
        thread.start();
    }

    private static void asyncSystemCommand(final String command) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncFileUtils.syncSystemCommand(command);
            }
        });
        thread.start();
    }

    private static void remountSystem(boolean rw) {
        syncRootCommand("mount -o " + ((rw) ? "rw" : "ro") + ",remount /system");
    }

    private static String escapeQuotes(String path) {
        return path.replace("\"", "\\\"");
    }
}
