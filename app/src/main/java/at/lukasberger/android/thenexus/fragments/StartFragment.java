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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.FileUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;
import cyanogenmod.power.PerformanceManager;

public class StartFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_start, container, false);
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
        final PowerManager mPowerManager = (PowerManager)view.getContext().getSystemService(Context.POWER_SERVICE);

        batteryCardView = (CardView) view.findViewById(R.id.fragment_start_battery_card_view);
        batteryPercentageTextView = (TextView) view.findViewById(R.id.fragment_start_battery_percentage);
        batterySpeedTextView = (TextView) view.findViewById(R.id.fragment_start_battery_speed);
        batteryStatusTextView = (TextView) view.findViewById(R.id.fragment_start_battery_status);

        deviceNameTextView.setText(getString(R.string.fragment_start_device_name, Build.BRAND, Build.PRODUCT));
        romNameTextView.setText(getString(R.string.fragment_start_rom, SystemUtils.getSystemProperty("ro.nexus.otarom")));
        androidVersionTextView.setText(getString(R.string.fragment_start_android_version, Build.VERSION.RELEASE, Build.VERSION.SECURITY_PATCH));
        kernelTextView.setText(getString(R.string.fragment_start_kernel, System.getProperty("os.version").split("-")[0]));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        getActivity().registerReceiver(batteryReceiver, intentFilter);

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
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);

            if (level != -1 && scale != -1) {
                int percentage = (int) ((level / (float) scale) * 100f);
                int speed = FileUtils.readInt("/sys/class/power_supply/max77843-charger/current_now", -1);

                batteryPercentageTextView.setText(getString(R.string.fragment_start_battery_precentage, percentage));

                if (isCharging)
                    batterySpeedTextView.setText(getString(R.string.fragment_start_battery_speed, speed));
                else
                    batterySpeedTextView.setText("");

                if (percentage < 15 && !isCharging) {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_red_800));
                } else if (percentage < 75 && !isCharging) {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_yellow_800));
                } else {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_800));
                }
            }

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    batteryStatusTextView.setText(getString(R.string.fragment_start_battery_status, "Charging"));
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    batteryStatusTextView.setText(getString(R.string.fragment_start_battery_status, "Full"));
                    break;

                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    batteryStatusTextView.setText(getString(R.string.fragment_start_battery_status, "Unknown"));
                    break;

                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    batteryStatusTextView.setText(getString(R.string.fragment_start_battery_status, "Discharging"));
                    break;
            }
        }
    };
}
