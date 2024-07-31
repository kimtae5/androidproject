package com.test.tclick;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestTriggerService extends Service {
    private static final String TAG = "TestTriggerService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TestTriggerService created");

        // 여기에 테스트 환경을 자동으로 준비하는 로직을 추가합니다.
        // 실제로 UiAutomator 테스트를 실행할 수는 없지만, 테스트를 자동으로 준비하는 기능을 구현할 수 있습니다.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "TestTriggerService started");

        // 실제 테스트 실행 로직을 추가할 수 있습니다.
        // UiAutomator 테스트를 직접 호출하는 것은 불가능하지만, 테스트를 자동으로 준비하거나 실행하는 방법을 고려할 수 있습니다.

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
