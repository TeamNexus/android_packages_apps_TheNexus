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

package at.lukasberger.android.thenexus.tiles;

import android.service.quicksettings.TileService;

import at.lukasberger.android.thenexus.R;
import cyanogenmod.power.PerformanceManager;

public class PowerProfileTile extends TileService {

    @Override
    public void onTileAdded() {
        this.initializeTile();
        super.onTileAdded();
    }

    @Override
    public void onStartListening() {
        this.initializeTile();
        super.onStartListening();
    }

    @Override
    public void onClick() {
        this.updatePowerProfile();
    }

    private void initializeTile() {
        initializeTile(0);
    }

    private void initializeTile(int profile) {
        if (profile <= 0)
            profile = PerformanceManager.getInstance(this).getPowerProfile();

        switch (profile) {
            case 0: this.getQsTile().setLabel(this.getString(R.string.power_profile_power_save)); break;
            case 1: this.getQsTile().setLabel(this.getString(R.string.power_profile_balanced)); break;
            case 2: this.getQsTile().setLabel(this.getString(R.string.power_profile_performance)); break;
            case 3: this.getQsTile().setLabel(this.getString(R.string.power_profile_efficiency)); break;
            case 4: this.getQsTile().setLabel(this.getString(R.string.power_profile_quick)); break;
        }

        this.getQsTile().updateTile();
    }

    private void updatePowerProfile() {
        int profile = PerformanceManager.getInstance(this).getPowerProfile();

        switch (profile) {
            case 0: profile = 3; break;
            case 3: profile = 1; break;
            case 1: profile = 4; break;
            case 4: profile = 2; break;
            case 2: profile = 0; break;
        }

        PerformanceManager.getInstance(this).setPowerProfile(profile);
        initializeTile(profile);
    }

}
