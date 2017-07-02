/*
 * The Nexus - ROM-Control for ROMs made by TeamNexus
 * Copyright (C) 2017  TeamNexus, Luca Polesel
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import at.lukasberger.android.thenexus.FragmentHelper;
import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.adapters.PropertiesAdapter;
import at.lukasberger.android.thenexus.models.Property;
import at.lukasberger.android.thenexus.utils.PropertiesUtil;

public class PropertiesFragment extends Fragment {

    private List<Property> properties;
    private PropertiesAdapter propsAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_properties, container, false);
        FragmentHelper.begin(view, R.id.fragment_props_loader, R.id.fragment_props_layout);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        updateProperties();

        FragmentHelper.finish();
    }

    private void updateProperties() {
        properties = PropertiesUtil.readProperties();

        propsAdapter = new PropertiesAdapter(getContext(), properties);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(propsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.properties_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_prop:
                final Property property = new Property();
                new MaterialDialog.Builder(getContext())
                        .title(getString(R.string.menu_properties_add))
                        .positiveText(getString(R.string.dialog_properties_ok_button))
                        .neutralText(getString(R.string.dialog_properties_cancel_button))
                        .input(getString(R.string.dialog_properties_property_name), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                property.setTitle(input.toString());
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new MaterialDialog.Builder(getContext())
                                        .title(getString(R.string.menu_properties_add))
                                        .positiveText(getString(R.string.dialog_properties_save_button))
                                        .neutralText(getString(R.string.dialog_properties_cancel_button))
                                        .input(getString(R.string.dialog_properties_input_value), "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                // Nothing here
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Editable text = dialog.getInputEditText().getText();
                                                if (text != null) {
                                                    String value = text.toString();
                                                    if (!value.trim().isEmpty()) {
                                                        property.setValue(value);
                                                        PropertiesUtil.addProperty(property);
                                                        properties.add(property);
                                                        propsAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }).show();
                            }
                        }).show();
                return true;
        }
        return onOptionsItemSelected(item);
    }
}
