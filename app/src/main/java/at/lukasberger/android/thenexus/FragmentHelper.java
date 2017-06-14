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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class FragmentHelper {

    private static final String BROADCAST_KEY = "at.lukasberger.android.thenexus.FRAGMENTHELPER_BROADCAST";
    private static final int BROADCAST_FINISHED = 0x00000001;
    private static final int BROADCAST_SET_TEXT = 0x00000002;
    private static final int BROADCAST_SET_PROGRESS = 0x00000003;
    private static final int BROADCAST_SET_CHECKED = 0x00000004;
    private static final int BROADCAST_SET_CARD_BACKGROUND_COLOR = 0x00000005;

    private static View mView;
    private static int mLoaderId;
    private static int mLayoutId;
    private static boolean broadcastRegistered = false;

    private static Intent broadcastIntent = new Intent(BROADCAST_KEY);
    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra("action", 0);
            int id, progress, color;
            String text;
            boolean checked;

            switch (action) {
                case BROADCAST_FINISHED:
                    if (mLoaderId != -1 && mLayoutId != -1) {
                        mView.findViewById(mLoaderId).setVisibility(View.GONE);
                        mView.findViewById(mLayoutId).setVisibility(View.VISIBLE);
                        unregisterBroadcast();
                    }
                    break;

                case BROADCAST_SET_TEXT:
                    id = intent.getIntExtra("id", -1);
                    text = intent.getStringExtra("text");

                    if (id != -1)
                        ((TextView)mView.findViewById(id)).setText(text);

                    break;

                case BROADCAST_SET_PROGRESS:
                    id = intent.getIntExtra("id", -1);
                    progress = intent.getIntExtra("progress", -1);

                    if (id != -1 && progress != -1)
                        ((SeekBar)mView.findViewById(id)).setProgress(progress);

                    break;

                case BROADCAST_SET_CHECKED:
                    id = intent.getIntExtra("id", -1);
                    checked = intent.getBooleanExtra("checked", false);

                    if (id != -1)
                        ((Switch)mView.findViewById(id)).setChecked(checked);

                    break;

                case BROADCAST_SET_CARD_BACKGROUND_COLOR:
                    id = intent.getIntExtra("id", -1);
                    color = intent.getIntExtra("color", -1);

                    if (id != -1 && color != -1)
                        ((CardView)mView.findViewById(id)).setCardBackgroundColor(mView.getContext().getColor(color));

                    break;
            }
        }

    };

    private static void unregisterBroadcast() {
        mView.getContext().unregisterReceiver(broadcastReceiver);
        broadcastRegistered = false;
    }

    public static void begin(View view) {
        mView = view;
        mLoaderId = -1;
        mLayoutId = -1;

        if (broadcastRegistered)
            unregisterBroadcast();

        mView.getContext().registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_KEY));
    }

    public static void begin(View view, int loaderId, int layoutId) {
        mView = view;
        mLoaderId = loaderId;
        mLayoutId = layoutId;

        mView.findViewById(mLoaderId).setVisibility(View.VISIBLE);
        mView.findViewById(mLayoutId).setVisibility(View.GONE);

        if (broadcastRegistered)
            unregisterBroadcast();

        mView.getContext().registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_KEY));
    }

    public static void setText(int viewId, String text) {
        broadcastIntent.putExtra("action", BROADCAST_SET_TEXT);
        broadcastIntent.putExtra("id", viewId);
        broadcastIntent.putExtra("text", text);
        mView.getContext().sendBroadcast(broadcastIntent);
    }

    public static void setProgress(int viewId, int progress) {
        broadcastIntent.putExtra("action", BROADCAST_SET_PROGRESS);
        broadcastIntent.putExtra("id", viewId);
        broadcastIntent.putExtra("progress", progress);
        mView.getContext().sendBroadcast(broadcastIntent);
    }

    public static void setChecked(int viewId, boolean checked) {
        broadcastIntent.putExtra("action", BROADCAST_SET_CHECKED);
        broadcastIntent.putExtra("id", viewId);
        broadcastIntent.putExtra("checked", checked);
        mView.getContext().sendBroadcast(broadcastIntent);
    }

    public static void setCardBackgroundColor(int viewId, int color) {
        broadcastIntent.putExtra("action", BROADCAST_SET_CARD_BACKGROUND_COLOR);
        broadcastIntent.putExtra("id", viewId);
        broadcastIntent.putExtra("color", color);
        mView.getContext().sendBroadcast(broadcastIntent);
    }

    public static void finish() {
        broadcastIntent.putExtra("action", BROADCAST_FINISHED);
        mView.getContext().sendBroadcast(broadcastIntent);
    }

}
