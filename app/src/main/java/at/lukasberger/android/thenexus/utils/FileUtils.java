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

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public final class FileUtils {

    public static void touch(String path) {
        try {
            Shell.SU.run("touch \"" + path + "\"");
        } catch (Exception e) {
            Log.e("TheNexus", "FileUtils.touch: failed to touch file: " + path, e);
        }
    }

    public static void delete(String path) {
        FileUtils.delete(path, false);
    }

    public static void delete(String path, boolean force) {
        try {
            Shell.SU.run("rm " + (force ? "-f " : " ") + "\"" + path + "\"");
        } catch (Exception e) {
            Log.e("TheNexus", "FileUtils.touch: failed to touch file: " + path, e);
        }
    }

    public static void write(String path, boolean content) {
        FileUtils.write(path, (content ? "1" : "0"));
    }

    public static void write(String path, int content) {
        FileUtils.write(path, Integer.toString(content));
    }

    public static void write(String path, String content) {
        content = content.replaceAll("\"", "\\\"");
        try {
            Shell.SU.run("echo \"" + content + "\" > \"" + path + "\"");
        } catch (Exception e) {
            Log.e("TheNexus", "FileUtils.write: failed to write to file: " + path, e);
        }
    }

    public static void write(String path, String[] content) {
        int i;

        FileUtils.write(path, content[0]);
        for (i = 1; i < content.length; i++)
            FileUtils.append(path, content[i]);
    }

    public static void append(String path, String content) {
        content = content.replaceAll("\"", "\\\"");
        try {
            Shell.SU.run("echo \"" + content + "\" >> \"" + path + "\"");
        } catch (Exception e) {
            Log.e("TheNexus", "FileUtils.append: failed to write to file: " + path, e);
        }
    }

    public static List<String> read(String path) {
        try {
            return Shell.SU.run("cat \"" + path + "\"");
        } catch (Exception e) {
            Log.e("TheNexus", "FileUtils.readLine: failed to read from file: " + path, e);
        }
        return null;
    }

    public static String readLine(String path) {
        List<String> result = FileUtils.read(path);
        return (result == null ? null : result.get(0));
    }

    public static boolean readBoolean(String path) {
        return readBoolean(path, false);
    }

    public static boolean readBoolean(String path, boolean onlyTrueOnOne) {
        String result = FileUtils.readLine(path);
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
            String result = FileUtils.readLine(path);
            if (result == null) {
                return def;
            }
            return Integer.parseInt(result);
        } catch (Exception ex) {
            return def;
        }
    }

    public static String convertBytesToReadable(int bytes) {
        return convertBytesToReadable(bytes, 2);
    }

    public static String convertBytesToReadable(int bytes, int precision) {
        double ibytes = bytes;
        String[] suffixes = new String[] { "Bytes", "KB", "MB", "GB", "TB" };
        int suffixIndex = 0;

        while (ibytes / 1024.0 > 0.9) {
            ibytes /= 1024.0;
            suffixIndex++;
        }

        return (Math.round(ibytes * Math.pow(10, precision)) / Math.pow(10, precision))
                + " " + suffixes[suffixIndex];
    }

}
