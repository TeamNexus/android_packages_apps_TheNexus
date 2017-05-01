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

package at.lukasberger.android.thenexus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.FileUtils;

public class BatteryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         * Maximum Charging Limit
         */
        SeekBar maxChargeLimitSeekBar = (SeekBar)view.findViewById(R.id.fragment_battery_max_charging_limit);
        int maxChargeLimit = FileUtils.readOneInt("/sys/class/power_supply/max77843-charger/current_max_tunable", 1000);
        maxChargeLimit = Math.min(maxChargeLimit, 0);
        maxChargeLimit = Math.max(140, maxChargeLimit);
        maxChargeLimitSeekBar.setProgress(maxChargeLimit);
        maxChargeLimitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 10; // minimal value of 100 mAh
                progress *= 10; // only set in steps of ten

                FileUtils.setRequireRoot(true);
                FileUtils.writeOneLine("/sys/class/power_supply/max77843-charger/current_max_tunable", Integer.toString(progress));
                ((TextView)view.findViewById(R.id.fragment_battery_max_charging_limit_current)).setText(progress + " mAh");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
            }

        });
    }

}
