package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 접근성 서비스가 활성화되어 있는지 확인
        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            // 접근성 서비스 활성화 화면으로 이동
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }

        // toss 버튼을 수동으로 클릭할 때의 이벤트 리스너
        Button tossButton = findViewById(R.id.buttonToss);
        tossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 클릭 시, MyAccessibilityService에 알림
                triggerAccessibilityService();
            }
        });
    }

    // 접근성 서비스에 작업을 트리거하는 메서드
    private void triggerAccessibilityService() {
        Intent intent = new Intent(this, MyAccessibilityService.class);
        intent.setAction("com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY");
        Log.d("MainActivity", "Sending broadcast: " + intent.getAction());
        startService(intent);
    }

    // 접근성 서비스 활성화 여부 확인
    private boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        ComponentName expectedComponentName = new ComponentName(context, service);
        String enabledServices = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        String colonSplitter = ":";
        if (enabledServices != null) {
            String[] componentNames = enabledServices.split(colonSplitter);
            for (String componentName : componentNames) {
                ComponentName enabledService = ComponentName.unflattenFromString(componentName);
                if (enabledService != null && enabledService.equals(expectedComponentName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
