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

package at.lukasberger.android.thenexus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import at.lukasberger.android.thenexus.utils.AsyncFileUtils;
import at.lukasberger.android.thenexus.utils.FileUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;

public class OnBootReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences prefs = context.getSharedPreferences("TheNexus", 0);
        final String romName = SystemUtils.getSystemProperty("ro.nexus.otarom");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (prefs.contains("battery.max_charging_limit_current")) {
                    int battery_max_charging_limit = prefs.getInt("battery.max_charging_limit_current", 0);
                    if (battery_max_charging_limit >= 100 && battery_max_charging_limit <= 1500) {
                        AsyncFileUtils.writeSync("/sys/class/power_supply/max77843-charger/current_max_tunable", battery_max_charging_limit);
                    }
                }

                if (prefs.contains("fingerprint.always_on_fp")) {
                    AsyncFileUtils.writeSync("/data/power/always_on_fp",
                            prefs.getBoolean("fingerprint.always_on_fp", true));
                }

                if (prefs.contains("power.profiles")) {
                    AsyncFileUtils.writeSync("/data/power/profiles",
                            prefs.getBoolean("power.profiles", true));
                }

                if (prefs.contains("touchscreen.dt2w")) {
                    boolean dt2w = prefs.getBoolean("touchscreen.dt2w", false);
                    AsyncFileUtils.writeSync("/sys/android_touch/doubletap2wake", dt2w);
                    AsyncFileUtils.writeSync("/data/power/dt2w", dt2w);
                }

                if (prefs.contains("wakelock.nfc_ese")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_nfc_ese",
                            prefs.getBoolean("wakelock.nfc_ese", false));
                }

                if (prefs.contains("wakelock.nfc_sec")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_nfc_sec",
                            prefs.getBoolean("wakelock.nfc_sec", false));
                }

                if (prefs.contains("wakelock.sensorhub_gps")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_sensorhub_gps",
                            prefs.getBoolean("wakelock.sensorhub_gps", false));
                }

                if (prefs.contains("wakelock.sensorhub_grip")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_sensorhub_grip",
                            prefs.getBoolean("wakelock.sensorhub_grip", false));
                }

                if (prefs.contains("wakelock.sensorhub_ssp")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_sensorhub_ssp",
                            prefs.getBoolean("wakelock.sensorhub_ssp", false));
                }

                if (prefs.contains("wakelock.sensorhub_ssp2")) {
                    AsyncFileUtils.write("/sys/module/wakeup/parameters/wakelock_sensorhub_ssp2",
                            prefs.getBoolean("wakelock.sensorhub_ssp2", false));
                }
            }

        });
        thread.start();
    }

}
