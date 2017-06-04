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

public class PowerCapabilities {

    /*
     * Keep this in sync with power/power.h in TeamNexus' hardware-repo
     */
    public static final int POWER_CAPABILITY_BOOST           = (1 << 0);
    public static final int POWER_CAPABILITY_PROFILES        = (1 << 1);
    public static final int POWER_CAPABILITY_FP_WORKAROUND   = (1 << 2);

    private static int cachedFlags = 0;

    public static boolean has(int capability) {
        if (cachedFlags == 0) {
            cachedFlags = FileUtils.readInt("/data/power/capabilities", 0);
        }

        return (cachedFlags & capability) == capability;
    }

}
