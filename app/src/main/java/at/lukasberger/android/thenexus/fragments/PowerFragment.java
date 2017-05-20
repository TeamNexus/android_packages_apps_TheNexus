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

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.FileUtils;
import at.lukasberger.android.thenexus.utils.PowerCapabilities;

public class PowerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        return inflater.inflate(R.layout.fragment_power, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         * Enable Boost
         */
        Switch enableBoostSwitch = (Switch)view.findViewById(R.id.fragment_power_enable_boost);
        if (PowerCapabilities.has(PowerCapabilities.POWER_CAPABILITY_BOOST)) {
            enableBoostSwitch.setChecked(FileUtils.readOneBoolean("/data/power/enable_boost"));
            enableBoostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FileUtils.setRequireRoot(true);
                    FileUtils.writeOneLine("/data/power/enable_boost", (isChecked ? "1" : "0"));
                }

            });
        } else {
            view.findViewById(R.id.fragment_power_enable_boost_container).setVisibility(View.GONE);
        }

        /*
         * Enable Profiles
         */
        Switch enableProfilesSwitch = (Switch)view.findViewById(R.id.fragment_power_enable_profiles);
        if (PowerCapabilities.has(PowerCapabilities.POWER_CAPABILITY_PROFILES)) {
            enableProfilesSwitch.setChecked(FileUtils.readOneBoolean("/data/power/enable_profiles"));
            enableProfilesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FileUtils.setRequireRoot(true);
                    FileUtils.writeOneLine("/data/power/enable_profiles", (isChecked ? "1" : "0"));
                }

            });
        } else {
            view.findViewById(R.id.fragment_power_enable_profiles_container).setVisibility(View.GONE);
        }

        /*
         * Powersave-Level
         */
        SeekBar powersaveLevelSeekBar = (SeekBar)view.findViewById(R.id.fragment_power_powersave_level);
        if (PowerCapabilities.has(PowerCapabilities.POWER_CAPABILITY_BOOST)) {
            int powersaveLevel = FileUtils.readOneInt("/data/power/powersave_level", 2);
            powersaveLevel = Math.min(powersaveLevel, 4);
            powersaveLevel = Math.max(0, powersaveLevel);
            powersaveLevelSeekBar.setProgress(powersaveLevel);
            powersaveLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progress = Math.min(progress, 4);
                    progress = Math.max(0, progress);

                    FileUtils.setRequireRoot(true);
                    FileUtils.writeOneLine("/data/power/powersave_level", Integer.toString(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

            });
        } else {
            view.findViewById(R.id.fragment_power_powersave_level_container).setVisibility(View.GONE);
        }

        /*
         * Debug
         */
        Switch debugSwitch = (Switch)view.findViewById(R.id.fragment_power_debug);
        debugSwitch.setChecked(FileUtils.readOneBoolean("/data/power/debug"));
        debugSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FileUtils.setRequireRoot(true);
                FileUtils.writeOneLine("/data/power/debug", (isChecked ? "1" : "0"));
            }

        });
    }
}
