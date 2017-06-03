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
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.utils.FileUtils;
import at.lukasberger.android.thenexus.utils.SystemUtils;
import eu.chainfire.libsuperuser.Shell;

public class UpdatesFragment extends Fragment {

    private final String BROADCAST_KEY = "threnexus-updates-broadcast";

    private final int BROADCAST_HIDE_SEARCHING_FOR_UPDATES_DIALOG = 0x00000001;
    private final int BROADCAST_SEARCHING_FOR_UPDATES_FAILED      = 0x00000002;
    private final int BROADCAST_UPDATE_OTA_INFORMATIONS           = 0x00000003;
    private final int BROADCAST_UPDATE_DOWNLOAD_PROGRESS          = 0x00000004;
    private final int BROADCAST_DOWNLOAD_AND_INSTALL_FAILED       = 0x00000005;
    private final int BROADCAST_ASK_FOR_REBOOT                    = 0x00000006;

    private View view;
    private MaterialDialog dialog;
    private JSONObject otaInformation;
    private boolean abortDownload;

    private Intent broadcastIntent = new Intent(BROADCAST_KEY);
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message;
            int action              = intent.getIntExtra("action", 0);
            SharedPreferences prefs = context.getSharedPreferences("TheNexus", 0);

            switch (action) {
                case BROADCAST_HIDE_SEARCHING_FOR_UPDATES_DIALOG:
                    dialog.hide();
                    break;

                case BROADCAST_SEARCHING_FOR_UPDATES_FAILED:
                    message = intent.getStringExtra("message");

                    dialog.hide();
                    dialog.setTitle(R.string.fragment_updates_searching_for_updates_failed);
                    dialog.setContent(message);
                    dialog.setActionButton(DialogAction.NEGATIVE, R.string.common_close);
                    dialog.setCancelable(true);
                    dialog.show();

                    break;

                case BROADCAST_UPDATE_OTA_INFORMATIONS:
                    try {
                        String channelName   = (prefs.getBoolean("updates.testing_ota_channel", false) ? "testing" : "release");
                        String deviceOtaName = SystemUtils.getSystemProperty("ro.nexus.otaname");

                        int buildTimestamp = Integer.parseInt(SystemUtils.getSystemProperty("ro.build.date.utc")) - 1;
                        int otaTimestamp   = otaInformation
                                .getJSONObject(deviceOtaName)
                                .getJSONObject(channelName)
                                .getInt("timestamp");

                        if (buildTimestamp < otaTimestamp) {
                            ((TextView)view.findViewById(R.id.fragment_updates_info_title)).setText(R.string.fragment_updates_info_new_update);
                            ((TextView)view.findViewById(R.id.fragment_updates_info_subtitle)).setText(R.string.fragment_updates_info_new_update_subtitle);
                            ((Button)view.findViewById(R.id.fragment_updates_download_and_install)).setVisibility(View.VISIBLE);
                        } else {
                            ((TextView)view.findViewById(R.id.fragment_updates_info_title)).setText(R.string.fragment_updates_info_no_update);
                            ((TextView)view.findViewById(R.id.fragment_updates_info_subtitle)).setText(R.string.fragment_updates_info_no_update_subtitle);
                            ((Button)view.findViewById(R.id.fragment_updates_download_and_install)).setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case BROADCAST_UPDATE_DOWNLOAD_PROGRESS:
                    int maximum = intent.getIntExtra("maximum", 0);
                    int current = intent.getIntExtra("current", 0);

                    dialog.setMaxProgress(maximum);
                    dialog.setProgress(current);
                    dialog.setProgressNumberFormat(FileUtils.convertBytesToReadable(current) + " / " + FileUtils.convertBytesToReadable(maximum));

                    break;

                case BROADCAST_DOWNLOAD_AND_INSTALL_FAILED:
                    message = intent.getStringExtra("message");

                    dialog.hide();
                    dialog.setTitle(R.string.fragment_updates_download_and_install_failed);
                    dialog.setContent(message);
                    dialog.setActionButton(DialogAction.NEGATIVE, R.string.common_close);
                    dialog.setCancelable(true);
                    dialog.show();

                    break;

                case BROADCAST_ASK_FOR_REBOOT:
                    dialog.hide();

                    dialog = new MaterialDialog.Builder(context)
                            .title(R.string.fragment_updates_ask_for_reboot)
                            .content(R.string.fragment_updates_ask_for_reboot_descr)
                            .positiveText(R.string.fragment_updates_ask_for_reboot_yes)
                            .negativeText(R.string.fragment_updates_ask_for_reboot_no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {

                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        Shell.SU.run("/system/bin/reboot recovery");
                                    }
                                }

                            })
                            .build();

                    dialog.show();

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.fragment_updates, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        final Context context                    = this.getContext();
        final SharedPreferences prefs            = context.getSharedPreferences("TheNexus", 0);
        final SharedPreferences.Editor prefsEdit = context.getSharedPreferences("TheNexus", 0).edit();

        Button checkForUpdates = (Button)view.findViewById(R.id.fragment_updates_check_for_updates);
        checkForUpdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new MaterialDialog.Builder(context)
                        .title(R.string.fragment_updates_searching_for_updates)
                        .content(R.string.this_can_take_a_while_internet)
                        .cancelable(false)
                        .build();

                dialog.show();
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            // download update-index
                            String deviceOtaName                 = SystemUtils.getSystemProperty("ro.nexus.otaname");
                            URL deviceOtaUrl                     = new URL("https://nexus-roms.eu/files/ota/ota.php?device=" + deviceOtaName);
                            HttpURLConnection otaIndexConnection = (HttpURLConnection)deviceOtaUrl.openConnection();

                            BufferedReader in = new BufferedReader(new InputStreamReader(otaIndexConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();

                            String line;
                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                            }

                            otaInformation = new JSONObject(sb.toString());

                            // update UI-informations
                            broadcastIntent.putExtra("action", BROADCAST_UPDATE_OTA_INFORMATIONS);
                            context.sendBroadcast(broadcastIntent);

                            // close dialog
                            broadcastIntent.putExtra("action", BROADCAST_HIDE_SEARCHING_FOR_UPDATES_DIALOG);
                            context.sendBroadcast(broadcastIntent);

                        } catch (Exception e) {
                            e.printStackTrace();

                            // fail
                            broadcastIntent.putExtra("action", BROADCAST_SEARCHING_FOR_UPDATES_FAILED);
                            broadcastIntent.putExtra("message", e.toString());
                            context.sendBroadcast(broadcastIntent);
                        }
                    }

                });
                thread.start();
            }

        });

        Button downloadAndInstall = (Button)view.findViewById(R.id.fragment_updates_download_and_install);
        downloadAndInstall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new MaterialDialog.Builder(context)
                        .title(R.string.fragment_updates_download_and_install_title)
                        .content(R.string.this_can_take_a_while_internet)
                        .progress(false, 1, true)
                        .cancelable(true)
                        .negativeText(R.string.common_cancel)
                        .cancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Log.e("TheNexus", "Download abort [1]");
                                abortDownload = true;
                            }

                        })
                        .build();

                dialog.show();
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (!SystemUtils.isExternalStorageWritable()) {
                                // fail
                                broadcastIntent.putExtra("action", BROADCAST_DOWNLOAD_AND_INSTALL_FAILED);
                                broadcastIntent.putExtra("message", "Cannot write to external storage");
                                context.sendBroadcast(broadcastIntent);
                                return;
                            }

                            String channelName         = (prefs.getBoolean("updates.testing_ota_channel", false) ? "testing" : "release");
                            String deviceOtaName       = SystemUtils.getSystemProperty("ro.nexus.otaname");
                            JSONObject otaChannelInfos = otaInformation
                                    .getJSONObject(deviceOtaName)
                                    .getJSONObject(channelName);
                            URL otaDownloadUrl  = new URL(otaChannelInfos.getString("url"));
                            int otaDownloadSize = otaChannelInfos.getInt("size");

                            HttpURLConnection otaIndexConnection = (HttpURLConnection)otaDownloadUrl.openConnection();
                            InputStream otaRemoteStream          = otaIndexConnection.getInputStream();
                            String otaLocalStreamFileName        = FilenameUtils.getName(otaDownloadUrl.getPath());
                            FileOutputStream otaLocalStream      = context.openFileOutput(otaLocalStreamFileName, Context.MODE_PRIVATE);

                            Field otaLocalStreamPathField = FileOutputStream.class.getDeclaredField("path");
                            otaLocalStreamPathField.setAccessible(true);
                            String otaLocalStreamPath = (String)otaLocalStreamPathField.get(otaLocalStream);

                            byte[] buffer = new byte[8192];
                            long lastUpdateTime = 0;
                            int current = 0;

                            abortDownload = false;

                            do {
                                int read = otaRemoteStream.read(buffer, 0, buffer.length);
                                otaLocalStream.write(buffer, 0, read);

                                // broadcast progress
                                if (lastUpdateTime + 1000 < System.currentTimeMillis()) {
                                    broadcastIntent.putExtra("action", BROADCAST_UPDATE_DOWNLOAD_PROGRESS);
                                    broadcastIntent.putExtra("current", current);
                                    broadcastIntent.putExtra("maximum", otaDownloadSize);
                                    context.sendBroadcast(broadcastIntent);

                                    lastUpdateTime = System.currentTimeMillis();
                                }

                                current += read;
                            } while (current < otaDownloadSize && !abortDownload);

                            if (abortDownload) {
                                Log.e("TheNexus", "Download abort [1]");
                                return;
                            }

                            boolean backup_data   = prefs.getBoolean("updates.backup_data", false);
                            boolean backup_system = prefs.getBoolean("updates.backup_system", false);
                            boolean backup_boot   = prefs.getBoolean("updates.backup_boot", false);

                            List<String> recovery_script = new ArrayList<>();
                            if (backup_data || backup_system ||backup_boot) {
                                recovery_script.add("backup " +
                                        (backup_system ? "S" : "") +
                                        (backup_data   ? "D" : "") +
                                        (backup_boot   ? "B" : ""));
                            }

                            recovery_script.add("install /sdcard/.nexusota/" + otaLocalStreamFileName);
                            recovery_script.add("wipe cache");
                            recovery_script.add("wipe dalvik");
                            recovery_script.add("cmd reboot system");

                            Shell.SU.run("mkdir -p /cache/recovery/");
                            Shell.SU.run("mkdir -p /sdcard/.nexusota/");
                            Shell.SU.run("rm -f /sdcard/.nexusota/*");
                            Shell.SU.run("mv -f " + otaLocalStreamPath + " /sdcard/.nexusota/" + otaLocalStreamFileName);
                            FileUtils.setRequireRoot(true);
                            FileUtils.writeLines("/cache/recovery/openrecoveryscript", recovery_script.toArray(new String[] { }));

                            // request user to accept reboot
                            broadcastIntent.putExtra("action", BROADCAST_ASK_FOR_REBOOT);
                            context.sendBroadcast(broadcastIntent);

                        } catch (Exception e) {
                            e.printStackTrace();

                            // fail
                            broadcastIntent.putExtra("action", BROADCAST_DOWNLOAD_AND_INSTALL_FAILED);
                            broadcastIntent.putExtra("message", e.getMessage());
                            context.sendBroadcast(broadcastIntent);
                        }
                    }

                });
                thread.start();
            }

        });

        Switch testingOtaChannel = (Switch)view.findViewById(R.id.fragment_updates_testing_ota_channel);
        testingOtaChannel.setChecked(prefs.getBoolean("updates.testing_ota_channel", false));
        testingOtaChannel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefsEdit.putBoolean("updates.testing_ota_channel", isChecked);
                prefsEdit.apply();
            }

        });

        Switch backupSystem = (Switch)view.findViewById(R.id.fragment_updates_backup_system);
        backupSystem.setChecked(prefs.getBoolean("updates.backup_system", false));
        backupSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefsEdit.putBoolean("updates.backup_system", isChecked);
                prefsEdit.apply();
            }

        });

        Switch backupData = (Switch)view.findViewById(R.id.fragment_updates_backup_data);
        backupData.setChecked(prefs.getBoolean("updates.backup_data", false));
        backupData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefsEdit.putBoolean("updates.backup_data", isChecked);
                prefsEdit.apply();
            }

        });

        Switch backupBoot = (Switch)view.findViewById(R.id.fragment_updates_backup_boot);
        backupBoot.setChecked(prefs.getBoolean("updates.backup_boot", false));
        backupBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefsEdit.putBoolean("updates.backup_boot", isChecked);
                prefsEdit.apply();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getContext().registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_KEY));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getContext().unregisterReceiver(broadcastReceiver);
    }

}
