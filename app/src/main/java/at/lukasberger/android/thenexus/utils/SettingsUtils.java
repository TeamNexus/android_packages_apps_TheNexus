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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class SettingsUtils {

    private static Context mContext;
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;

    @SuppressLint("CommitPrefEdits")
    public static void begin(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences("TheNexus", 0);
        mPrefsEditor = mPrefs.edit();
    }

    public static void set(String name, boolean value) {

        // global settings
        Settings.Secure.putInt(mContext.getContentResolver(), "at.lukasberger.android.thenexus." + name, (value ? 1 : 0));

        // private settings
        mPrefsEditor.putBoolean(name, value);
        mPrefsEditor.apply();
    }

    public static void set(String name, int value) {
        // global settings
        Settings.Secure.putInt(mContext.getContentResolver(), "at.lukasberger.android.thenexus." + name, value);

        // private settings
        mPrefsEditor.putInt(name, value);
        mPrefsEditor.apply();
    }

    public static boolean getBoolean(String name, boolean def) {
        return mPrefs.getBoolean(name, def);
    }

}
