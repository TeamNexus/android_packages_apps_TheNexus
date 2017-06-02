/*
 * The Nexus - ROM-Control application for ROMs made by the Nexus7420-team
 * Copyright (C) 2017  Team Nexus7420, Lukas Berger
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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import eu.chainfire.libsuperuser.Shell;

public final class FileUtils {

    private static boolean requestRoot;

    public static void setRequireRoot(boolean requireRoot) {
        if (requireRoot == requestRoot) {
            // state not changed, skip it
            return;
        }

        requestRoot = requireRoot;

        if (requestRoot) {
            /*
             * Check if the su-binary is available and if
             * the app can access it
             */
            if (!Shell.SU.available()) {
                Log.e("TheNexus", "setRequireRoot: failed to request root, fallback to non-root-mode");
                requestRoot = false;
                return;
            }

            Log.i("TheNexus", "setRequireRoot: root-access acquired");
        } else {
            // reset variables
            requestRoot = false;
        }
    }

    public static void writeOneLine(String path, String content) {
        if (requestRoot) {
            content = content.replaceAll("\"", "\\\"");
            try {
                Shell.SU.run("echo \"" + content + "\" > \"" + path + "\"");
            } catch (Exception e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        } else {
            try {
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.println(content);
                writer.close();
            } catch (IOException e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        }
    }

    public static void appendOneLine(String path, String content) {
        if (requestRoot) {
            content = content.replaceAll("\"", "\\\"");
            try {
                Shell.SU.run("echo \"" + content + "\" >> \"" + path + "\"");
            } catch (Exception e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        } else {
            try {
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.println(content);
                writer.close();
            } catch (IOException e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        }
    }

    public static void writeLines(String path, String[] content) {
        int i;

        writeOneLine(path, content[0]);
        for (i = 1; i < content.length; i++)
            appendOneLine(path, content[i]);
    }

    public static String readOneLine(String path) {
        if (requestRoot) {
            try {
                return Shell.SU.run("cat \"" + path + "\"").get(0);
            } catch (Exception e) {
                Log.e("TheNexus", "readOneLine: failed to read from file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        } else {
            BufferedReader reader = null;
            String result = null;
            try {
                reader = new BufferedReader(new FileReader(path));
                result = reader.readLine();
                reader.close();
                return result;
            } catch (IOException e) {
                Log.e("TheNexus", "readOneLine: failed to read from file: " + path + " (requestRoot: " + requestRoot + ")", e);
            }
        }
        return null;
    }

    public static boolean readOneBoolean(String path) {
        return readOneBoolean(path, false);
    }

    public static boolean readOneBoolean(String path, boolean onlyTrueOnOne) {
        String result = readOneLine(path);
        if (result == null) {
            return false;
        }
        if (onlyTrueOnOne) {
            return result.equals("1");
        } else {
            return !result.equals("0");
        }
    }

    public static int readOneInt(String path, int def) {
        try {
            String result = readOneLine(path);
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
