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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import at.lukasberger.android.thenexus.R;

public class StartFragment extends Fragment {

    private CardView batteryCardView;
    private TextView batteryPercentageTextView;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView deviceNameTextView = (TextView) view.findViewById(R.id.device_name);
        TextView romNameTextView = (TextView) view.findViewById(R.id.rom_name);
        TextView androidVersionTextView = (TextView) view.findViewById(R.id.android_version);
        TextView buildDateTextView = (TextView) view.findViewById(R.id.build_date);
        batteryCardView = (CardView) view.findViewById(R.id.battery_card_view);
        batteryPercentageTextView = (TextView) view.findViewById(R.id.battery_percentage);
        batteryStatusTextView = (TextView) view.findViewById(R.id.battery_status);

        deviceNameTextView.setText(getString(R.string.fragment_start_device_name, Build.DEVICE));
        romNameTextView.setText(getString(R.string.fragment_start_rom, Build.DISPLAY));
        androidVersionTextView.setText(getString(R.string.fragment_start_android_version, Build.VERSION.RELEASE));
        buildDateTextView.setText(getString(R.string.fragment_start_kernel, System.getProperty("os.version")));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        getActivity().registerReceiver(batteryReceiver, intentFilter);
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

            if (level != -1 && scale != -1) {
                int percentage = (int) ((level / (float) scale) * 100f);
                batteryPercentageTextView.setText(percentage + " %");

                if (percentage < 15) {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_red_800));
                } else if (percentage < 80) {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_yellow_800));
                } else {
                    batteryCardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_800));
                }
            }

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

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
