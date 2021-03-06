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

package at.lukasberger.android.thenexus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import at.lukasberger.android.thenexus.FragmentHelper;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.AsyncFileUtils;
import at.lukasberger.android.thenexus.utils.SettingsUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;

public class BatteryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_battery, container, false);
        FragmentHelper.begin(view, R.id.fragment_battery_loader, R.id.fragment_battery_layout);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                SettingsUtils.begin(view.getContext());

                /*
                 * Maximum Charging Limit
                 */
                SeekBar maxChargeLimitSeekBar = (SeekBar)view.findViewById(R.id.fragment_battery_max_charging_limit);
                int maxChargeLimit = AsyncFileUtils.readInteger("/sys/class/power_supply/max77843-charger/current_max_tunable", 1500);

                FragmentHelper.setText(R.id.fragment_battery_max_charging_limit_current, getString(R.string.fragment_battery_max_charging_limit_text, maxChargeLimit));

                maxChargeLimit = Math.max(maxChargeLimit, 0);
                maxChargeLimit = Math.min(2000, maxChargeLimit);
                FragmentHelper.setProgress(maxChargeLimitSeekBar.getId(), maxChargeLimit / 10);

                maxChargeLimitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress *= 10; // only set in steps of ten

                        // we need to restart the FragmentHelper here which was finished below
                        FragmentHelper.setText(R.id.fragment_battery_max_charging_limit_current, getString(R.string.fragment_battery_max_charging_limit_text, progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        progress *= 10; // only set in steps of ten

                        SettingsUtils.set("battery.max_charging_limit_current", progress);
                        AsyncFileUtils.write("/sys/class/power_supply/max77843-charger/current_max_tunable", progress);
                    }

                });

                /*
                 * Disable Critical Battery-Shutdown
                 */
                final Switch noCriticalShutdown = (Switch)view.findViewById(R.id.fragment_battery_no_critical_shutdown);
                if (SystemUtils.isNexusOS()) {
                    FragmentHelper.setChecked(noCriticalShutdown.getId(), SettingsUtils.getBoolean("battery.no_critical_shutdown", false));
                    noCriticalShutdown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SettingsUtils.set("battery.no_critical_shutdown", isChecked);
                        }

                    });
                } else {
                    noCriticalShutdown.setVisibility(View.GONE);
                }

                FragmentHelper.finish(false);
            }

        });
        thread.start();
    }

}
