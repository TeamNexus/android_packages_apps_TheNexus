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
