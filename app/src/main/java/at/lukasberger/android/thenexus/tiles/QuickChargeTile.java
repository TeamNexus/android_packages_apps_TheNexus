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

import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.TileService;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.AsyncFileUtils;

public class QuickChargeTile extends TileService {

    private boolean mQuickCharge;
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;

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
        this.updateQuickCharge(!mQuickCharge);
    }

    private void initializeTile() {
        // initialize
        mPrefs = this.getSharedPreferences("TheNexus", 0);
        mPrefsEditor = mPrefs.edit();
        mQuickCharge = mPrefs.getBoolean("battery.quick_charge", false);

        // update tile
        updateQuickCharge(this.mQuickCharge);
    }

    private void updateQuickCharge(boolean quickCharge) {
        int chargeSpeed;
        mQuickCharge = quickCharge;

        if (mQuickCharge) {
            chargeSpeed = 1500;
            this.setIcon(R.drawable.ic_menu_battery);
        } else {
            chargeSpeed = 900;
            this.setIcon(R.drawable.ic_battery_full_black);
        }

        // save settings
        mPrefsEditor.putInt("battery.max_charging_limit_current", chargeSpeed);
        mPrefsEditor.putBoolean("battery.quick_charge", mQuickCharge);
        mPrefsEditor.apply();
        AsyncFileUtils.write("/sys/class/power_supply/max77843-charger/current_max_tunable", chargeSpeed);
    }

    private void setIcon(int resId) {
        this.getQsTile().setIcon(Icon.createWithResource(this, resId));
        this.getQsTile().updateTile();
    }

}
