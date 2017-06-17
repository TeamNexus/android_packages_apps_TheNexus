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
import android.content.SharedPreferences;
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
import at.lukasberger.android.thenexus.utils.FileUtils;

public class FingerprintFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_fingerprint, container, false);
        FragmentHelper.begin(view, R.id.fragment_fingerprint_loader, R.id.fragment_fingerprint_layout);

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
                 * Always-On Fingerprint
                 */
                Switch alwaysOnFPSwitch = (Switch)view.findViewById(R.id.fragment_fingerprint_always_on_fp);
                FragmentHelper.setChecked(alwaysOnFPSwitch.getId(), AsyncFileUtils.readBoolean("/data/power/always_on_fp"));
                alwaysOnFPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        prefsEdit.putBoolean("fingerprint.always_on_fp", isChecked);
                        prefsEdit.apply();

                        AsyncFileUtils.write("/data/power/always_on_fp", isChecked);
                    }

                });

                FragmentHelper.finish();
            }
        });
        thread.start();
    }

}
