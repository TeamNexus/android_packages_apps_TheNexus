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

package at.lukasberger.android.thenexus;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import at.lukasberger.android.thenexus.fragments.BatteryFragment;
import at.lukasberger.android.thenexus.fragments.FingerprintFragment;
import at.lukasberger.android.thenexus.fragments.NoRootFragment;
import at.lukasberger.android.thenexus.fragments.PropertiesFragment;
import at.lukasberger.android.thenexus.fragments.StartFragment;
import at.lukasberger.android.thenexus.fragments.PowerFragment;
import at.lukasberger.android.thenexus.fragments.TouchscreenFragment;
import at.lukasberger.android.thenexus.fragments.UpdatesFragment;
import at.lukasberger.android.thenexus.fragments.WakelocksFragment;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private boolean firstFragmentUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.drawer_layout);

        if (!Shell.SU.available()) {
            this.updateFragment(new NoRootFragment());
            return;
        }

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firstFragmentUpdate = true;
        this.updateFragment(new StartFragment());
    }

    @Override
    public void onBackPressed() {
        if (this.drawer != null && this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        switch(item.getItemId()) {
            case R.id.nav_start:
                fragment = new StartFragment();
                setTitle("Start");
                break;
            case R.id.nav_updates:
                fragment = new UpdatesFragment();
                setTitle("Updates");
                break;
            case R.id.nav_battery:
                fragment = new BatteryFragment();
                setTitle("Battery");
                break;
            case R.id.nav_fingerprint:
                fragment = new FingerprintFragment();
                setTitle("Fingerprint");
                break;
            case R.id.nav_props:
                fragment = new PropertiesFragment();
                setTitle("Properties");
                break;
            case R.id.nav_power:
                fragment = new PowerFragment();
                setTitle("Power");
                break;
            case R.id.nav_touchscreen:
                fragment = new TouchscreenFragment();
                setTitle("Touchscreen");
                break;
            case R.id.nav_wakelocks:
                fragment = new WakelocksFragment();
                setTitle("Wakelocks");
                break;
        }

        if (fragment != null) {
            this.updateFragment(fragment);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void updateFragment(Fragment fragment) {
        FragmentTransaction transact = getFragmentManager().beginTransaction();

        if (this.firstFragmentUpdate) {
            transact.add(R.id.content, fragment);
            setTitle("Start");
        } else {
            transact.replace(R.id.content, fragment);
        }

        this.firstFragmentUpdate = false;
        transact.commit();
    }

}
