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
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public final class FileUtils {

    private static boolean requestRoot;
    private static boolean useSuBinary;
    private static PackageManager packageManager;
    private static ApplicationInfo applicationInfo;

    private static Shell.Interactive rootSession = null;
    private static int rootSessionIdentifier = 0;

    public static void setPackageManager(PackageManager pm) {
        packageManager = pm;
    }

    public static boolean checkIfAppIsSytemApp() {
        if (applicationInfo == null) {
            try {
                applicationInfo = packageManager.getApplicationInfo("at.lukasberger.android.thenexus", 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
    }

    private static void openRootSession() {
        Log.e("TheNexus", "openRootSession: required to re-open root-session: " + (rootSession == null ? "true" : "false"));

        // check if an open root-session is required
        if (requestRoot && useSuBinary && rootSession == null) {
            Log.i("TheNexus", "openRootSession: try to open new root-session");
            rootSession = new Shell.Builder()
                    .useSU()
                    .setWantSTDERR(true)
                    .setWatchdogTimeout(5)
                    .setMinimalLogging(true)
                    .open();

            Log.i("TheNexus", "openRootSession: " + (rootSession == null ? "failed" : "succeeded"));
        }
    }

    public static void setRequireRoot(boolean requireRoot) {
        if (requireRoot == requestRoot) {
            // state not changed, skip it
            return;
        }

        requestRoot = requireRoot;

        if (requestRoot) {

            /*
             * Check if the app is placed in /system/
             */
            if (checkIfAppIsSytemApp()) {
                Log.i("TheNexus", "setRequireRoot: installed as system-appliction, try to switch to non-su mode");

                try {
                    new BufferedReader(new FileReader("/data/.nonexistent"));

                    Log.i("TheNexus", "setRequireRoot: able to access /data/-partition, continue using system-method");
                    useSuBinary = false;
                    return;
                } catch (Exception ex) {
                    Log.i("TheNexus", "setRequireRoot: failed to access protected /data/-partition, fall back to root-method");
                    useSuBinary = true;
                }
            }

            /*
             * Check if the su-binary is available and if
             * the app can access it
             */
            if (!Shell.SU.available()) {
                Log.e("TheNexus", "setRequireRoot: failed to request root, fallback to non-root-mode");
                requestRoot = false;
            }

            openRootSession();
        } else {
            // reset variables
            requestRoot = false;
            useSuBinary = false;

            // destroy open root-session
            rootSession.close();
            rootSession = null;
        }
    }

    public static void writeOneLine(String path, String content) {
        if (requestRoot && useSuBinary) {
            content = content.replaceAll("\"", "\\\"");
            try {
                Shell.SU.run("echo \"" + content + "\" > \"" + path + "\"");
            } catch (Exception e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: true)", e);
            }
        } else {
            try {
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.println(content);
                writer.close();
            } catch (IOException e) {
                Log.e("TheNexus", "writeOneLine: failed to write to file: " + path + " (requestRoot: false)", e);
            }
        }
    }

    public static String readOneLine(String path) {
        if (requestRoot && useSuBinary) {
            try {
                return Shell.SU.run("cat \"" + path + "\"").get(0);
            } catch (Exception e) {
                Log.e("TheNexus", "readOneLine: failed to read from file: " + path + " (requestRoot: true)", e);
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
                Log.e("TheNexus", "readOneLine: failed to read from file: " + path + " (requestRoot: false)", e);
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

}
