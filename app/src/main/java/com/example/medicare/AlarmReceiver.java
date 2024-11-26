package com.example.medicare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private static Ringtone ringtone;
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medication_name");
        String medicationAmount = intent.getStringExtra("medication_amount");

        // Create a notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "medication_reminder_channel";

        // For Android 8.0+ (Oreo), create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Medication Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for medication reminders");
            notificationManager.createNotificationChannel(channel);
        }

        // PendingIntent to stop the ringtone when notification is clicked
        Intent stopIntent = new Intent(context, StopRingtoneReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.app) // Replace with your app icon
                .setContentTitle("Medication Reminder")
                .setContentText("Time to take: " + medicationName + " (" + medicationAmount + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Display the notification
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

        // Optionally, play a sound
        try {
             ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stop the ringtone
    public static void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }
}
