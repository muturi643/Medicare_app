package com.example.medicare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopRingtoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the ringtone
        AlarmReceiver.stopRingtone();
    }
}

