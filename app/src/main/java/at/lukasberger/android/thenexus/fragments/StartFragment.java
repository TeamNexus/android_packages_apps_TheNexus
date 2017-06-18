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
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import at.lukasberger.android.thenexus.FragmentHelper;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.AsyncFileUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;
import cyanogenmod.power.PerformanceManager;

public class StartFragment extends Fragment {

    private View baseView;
    private CardView batteryCardView;
    private TextView batteryPercentageTextView;
    private TextView batterySpeedTextView;
    private TextView batteryStatusTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_start, container, false);
        FragmentHelper.begin(view);

        return (this.baseView = view);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView deviceNameTextView = (TextView)view.findViewById(R.id.fragment_start_device);
        TextView romNameTextView = (TextView)view.findViewById(R.id.fragment_start_rom);
        TextView androidVersionTextView = (TextView)view.findViewById(R.id.fragment_start_version);
        TextView kernelTextView = (TextView)view.findViewById(R.id.fragment_start_kernel);

        final TextView powerProfileTextView = (TextView)view.findViewById(R.id.fragment_start_power_profile);
        SeekBar powerProfileSeekBar = (SeekBar)view.findViewById(R.id.fragment_start_power_profile_bar);

        batteryCardView = (CardView) view.findViewById(R.id.fragment_start_battery_card_view);
        batteryPercentageTextView = (TextView) view.findViewById(R.id.fragment_start_battery_percentage);
        batterySpeedTextView = (TextView) view.findViewById(R.id.fragment_start_battery_speed);
        batteryStatusTextView = (TextView) view.findViewById(R.id.fragment_start_battery_status);

        deviceNameTextView.setText(getString(R.string.fragment_start_device_name, Build.BRAND, Build.PRODUCT));
        romNameTextView.setText(getString(R.string.fragment_start_rom, SystemUtils.getSystemProperty("ro.nexus.otarom")));
        androidVersionTextView.setText(getString(R.string.fragment_start_android_version, Build.VERSION.RELEASE, Build.VERSION.SECURITY_PATCH));
        kernelTextView.setText(getString(R.string.fragment_start_kernel, System.getProperty("os.version").split("-")[0]));

        IntentFilter batteryIntentFilter = new IntentFilter();
        batteryIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        batteryIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        getActivity().registerReceiver(batteryReceiver, batteryIntentFilter);

        IntentFilter performanceManagerIntentFilter = new IntentFilter();
        performanceManagerIntentFilter.addAction(PerformanceManager.POWER_PROFILE_CHANGED);

        getActivity().registerReceiver(performanceManagerReceiver, performanceManagerIntentFilter);

        updateCurrentProfile(PerformanceManager.getInstance(view.getContext()).getPowerProfile());
        powerProfileSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        powerProfileTextView.setText(R.string.fragment_start_power_profile_power_save);
                        PerformanceManager.getInstance(view.getContext()).setPowerProfile(0);
                        break;

                    case 1:
                        powerProfileTextView.setText(R.string.fragment_start_power_profile_balanced);
                        PerformanceManager.getInstance(view.getContext()).setPowerProfile(1);
                        break;

                    case 2:
                        powerProfileTextView.setText(R.string.fragment_start_power_profile_performance);
                        PerformanceManager.getInstance(view.getContext()).setPowerProfile(2);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(batteryReceiver);
        getActivity().unregisterReceiver(performanceManagerReceiver);
    }

    private void updateCurrentProfile(int profile) {
        if (baseView == null)
            return;

        if (profile < 0 || profile > 3)
            return;

        TextView powerProfileTextView = (TextView)baseView.findViewById(R.id.fragment_start_power_profile);
        SeekBar powerProfileSeekBar = (SeekBar)baseView.findViewById(R.id.fragment_start_power_profile_bar);

        powerProfileSeekBar.setProgress(profile);
        switch (profile) {
            case 0: powerProfileTextView.setText(R.string.fragment_start_power_profile_power_save); break;
            case 1: powerProfileTextView.setText(R.string.fragment_start_power_profile_balanced); break;
            case 2: powerProfileTextView.setText(R.string.fragment_start_power_profile_performance); break;
        }
    }

    private BroadcastReceiver performanceManagerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    updateCurrentProfile(PerformanceManager.getInstance(context).getPowerProfile());
                }

            });
            thread.start();
        }

    };

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);

                    if (level != -1 && scale != -1) {
                        int percentage = (int) ((level / (float) scale) * 100f);
                        int speed = AsyncFileUtils.readInteger("/sys/class/power_supply/max77843-charger/current_now", -1);

                        FragmentHelper.setText(batteryPercentageTextView.getId(), getString(R.string.fragment_start_battery_precentage, percentage));

                        if (isCharging)
                            FragmentHelper.setText(batterySpeedTextView.getId(), getString(R.string.fragment_start_battery_speed, speed));
                        else
                            FragmentHelper.setText(batterySpeedTextView.getId(), "");

                        if (percentage < 15 && !isCharging) {
                            FragmentHelper.setCardBackgroundColor(batteryCardView.getId(), R.color.md_red_800);
                        } else if (percentage < 75 && !isCharging) {
                            FragmentHelper.setCardBackgroundColor(batteryCardView.getId(), R.color.md_yellow_800);
                        } else {
                            FragmentHelper.setCardBackgroundColor(batteryCardView.getId(), R.color.md_green_800);
                        }
                    }

                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            FragmentHelper.setText(batteryStatusTextView.getId(), getString(R.string.fragment_start_battery_status, "Charging"));
                            break;

                        case BatteryManager.BATTERY_STATUS_FULL:
                            FragmentHelper.setText(batteryStatusTextView.getId(), getString(R.string.fragment_start_battery_status, "Full"));
                            break;

                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            FragmentHelper.setText(batteryStatusTextView.getId(), getString(R.string.fragment_start_battery_status, "Unknown"));
                            break;

                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        default:
                            FragmentHelper.setText(batteryStatusTextView.getId(), getString(R.string.fragment_start_battery_status, "Discharging"));
                            break;
                    }
                }
            });
            thread.start();
        }

    };

}
