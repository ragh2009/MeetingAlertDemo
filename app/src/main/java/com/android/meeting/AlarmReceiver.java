package com.android.meeting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.meeting.models.EventsData;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventsData mData = (EventsData) intent.getSerializableExtra("MData");
        if (mData != null)
            sendEventNotification(context, mData.getTitle(), mData.geteTimeFormat(), mData);
        else
            sendEventNotification(context, "Meeting", "in 10 minutes.", mData);
//        Intent aIntent = new Intent(context, EventAlarmManagerActivity.class);
//        aIntent.putExtra("MData", mData);
//        aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(aIntent);
    }

    private void sendEventNotification(Context context, String title, String msg, EventsData mData) {
        Intent intent = new Intent(context, EventAlarmManagerActivity.class);
        intent.putExtra("MData", mData);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            String channelId = context.getString(R.string.channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(contentIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.notify(m, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}