package com.android.meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.meeting.models.EventsData;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       Intent aIntent = new Intent(context , EventAlarmManagerActivity.class);
       EventsData mData = (EventsData) intent.getSerializableExtra("MData");
       aIntent.putExtra("MData", mData);
       aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       context.startActivity(aIntent);
    }
}