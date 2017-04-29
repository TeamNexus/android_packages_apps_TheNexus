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
import android.widget.TextView;

import at.lukasberger.android.thenexus.MainActivity;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.FileUtils;
import eu.chainfire.libsuperuser.Shell;

public class StartFragment extends Fragment {

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
        assertUpdateTextView(view, Shell.SU.available(), R.id.fragment_start_status_rooted);
        assertUpdateTextView(view, FileUtils.checkIfAppIsSytemApp(), R.id.fragment_start_status_installed_as_system);
    }

    private void assertUpdateTextView(View view, boolean flag, int viewId) {
        assertUpdateTextView(view, flag, viewId, false);
    }

    private void assertUpdateTextView(View view, boolean flag, int viewId, boolean inverted) {
        TextView v = ((TextView)view.findViewById(viewId));
        if (flag && !inverted) {
            v.setText(R.string.common_yes);
        } else {
            v.setText(R.string.common_no);
        }
    }

}
