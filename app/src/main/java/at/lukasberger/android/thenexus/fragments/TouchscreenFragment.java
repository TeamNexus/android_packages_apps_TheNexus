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
import android.widget.Switch;

import at.lukasberger.android.thenexus.FragmentHelper;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.AsyncFileUtils;
import at.lukasberger.android.thenexus.utils.SettingsUtils;

public class TouchscreenFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_touchscreen, container, false);
        FragmentHelper.begin(view, R.id.fragment_touchscreen_loader, R.id.fragment_touchscreen_layout);

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
                 * Enable DT2W
                 */
                Switch dt2wSwitch = (Switch)view.findViewById(R.id.fragment_touchscreen_dt2w);
                FragmentHelper.setChecked(dt2wSwitch.getId(), AsyncFileUtils.readBoolean("/data/power/dt2w"));
                dt2wSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SettingsUtils.set("touchscreen.dt2w", isChecked);
                        AsyncFileUtils.write("/sys/android_touch/doubletap2wake", isChecked);
                        AsyncFileUtils.write("/data/power/dt2w", isChecked);
                    }

                });

                FragmentHelper.finish();
            }
        });
        thread.start();
    }

}
