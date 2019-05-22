package com.esraa.android.plannertracker.BroadCastRecievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.esraa.android.plannertracker.R;



public class NotificationReciever extends BroadcastReceiver {
    public static final int NOTIFICATION_ALARM = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This is Notification", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.family_son)
                .setContentTitle("Your trip")
                .setContentText("Check out your Trip")
                .setContentInfo("info")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_ALL);

        NotificationManager manager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ALARM, builder.build());
    }
}
