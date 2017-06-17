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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;

import at.lukasberger.android.thenexus.FragmentHelper;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.AsyncFileUtils;
import at.lukasberger.android.thenexus.utils.FileUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;
import eu.chainfire.libsuperuser.Shell;

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
        final SharedPreferences prefs = view.getContext().getSharedPreferences("TheNexus", 0);
        final SharedPreferences.Editor prefsEdit = prefs.edit();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                /*
                 * Maximum Charging Limit
                 */
                SeekBar maxChargeLimitSeekBar = (SeekBar)view.findViewById(R.id.fragment_battery_max_charging_limit);
                int maxChargeLimit = AsyncFileUtils.readInt("/sys/class/power_supply/max77843-charger/current_max_tunable", 1000);

                FragmentHelper.setText(R.id.fragment_battery_max_charging_limit_current, getString(R.string.fragment_battery_max_charging_limit_text, maxChargeLimit));

                maxChargeLimit = Math.max(maxChargeLimit, 0);
                maxChargeLimit = Math.min(1500, maxChargeLimit);
                FragmentHelper.setProgress(maxChargeLimitSeekBar.getId(), (maxChargeLimit / 10) - 10);

                maxChargeLimitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress += 10; // minimal value of 100 mAh
                        progress *= 10; // only set in steps of ten

                        prefsEdit.putInt("battery.max_charging_limit_current", progress);
                        prefsEdit.apply();

                        AsyncFileUtils.write("/sys/class/power_supply/max77843-charger/current_max_tunable", progress);

                        // we need to restart the FragmentHelper here which was finished below
                        FragmentHelper.setText(R.id.fragment_battery_max_charging_limit_current, getString(R.string.fragment_battery_max_charging_limit_text, progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

                FragmentHelper.finish(false);
            }

        });
        thread.start();
    }

}
