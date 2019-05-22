package com.esraa.android.plannertracker.BroadCastRecievers;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.esraa.android.plannertracker.R;




import java.util.Locale;

public class AlarmDialog extends AppCompatActivity {
    public static final int NOTIFICATION_ALARM = 1;
    Ringtone ringtone;
    public static final String PREFS_NAME ="MyPrefsFile";
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!flag) {
            playSound();
        }
        showAlarmDialog();
        flag = true;
    }

    private void playSound() {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),sound);
        ringtone.play();

    }

    private void showAlarmDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder To your Trip")
                .setMessage("Check out your trip")
                .setPositiveButton("Ok Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ringtone.stop();
                        Toast.makeText(AlarmDialog.this, "Starting", Toast.LENGTH_SHORT).show();
                        openMap();
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ringtone.stop();
                finish();

            }
        })
                .setNeutralButton("later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ringtone.stop();
                Toast.makeText(AlarmDialog.this, "sending Notification", Toast.LENGTH_SHORT).show();
                sendNotification();
                finish();
            }
        }).create().show();
    }

    private void openMap() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME , 0);
        String startPoint=(settings.getString("start",null));
        String endPoint=(settings.getString("end",null));


        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr="+startPoint+"&daddr="+endPoint);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void sendNotification() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME , 0);
        String tripName = settings.getString("tripName","No trip");

        PendingIntent again = PendingIntent.getActivity(this,0,
                new Intent(this,AlarmDialog.class),PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.map)
                .setContentTitle("Your trip " + tripName)
                .setContentText("Check out your Trip")
                .setContentInfo("will Start soon ")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentIntent(again);


        NotificationManager manager = (NotificationManager) this.
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ALARM, builder.build());
    }
}
