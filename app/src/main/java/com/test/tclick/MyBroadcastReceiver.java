package com.test.tclick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY".equals(intent.getAction())) {
            Log.d("MyBroadcastReceiver", "Broadcast received: Triggering Toss App Launch");
            Intent serviceIntent = new Intent(context, MyAccessibilityService.class);
            context.startService(serviceIntent);
        }
    }
}
