package com.test.tclick;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class TestTriggerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Run UiAutomatorTest
                    Runtime.getRuntime().exec("am instrument -w -r -e debug false -e class com.test.tclick.UiAutomatorTest com.test.tclick.test/androidx.test.runner.AndroidJUnitRunner");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        }).start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
