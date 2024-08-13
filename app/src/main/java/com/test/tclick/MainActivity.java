package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button tossButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tossButton = findViewById(R.id.buttonToss);
        tossButton.setOnClickListener(v -> triggerAccessibilityService());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 접근성 서비스가 활성화되어 있는지 확인
        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            // 접근성 서비스 활성화 안내 다이얼로그 표시
            new AlertDialog.Builder(this)
                    .setTitle("접근성 서비스 필요")
                    .setMessage("이 기능을 사용하려면 접근성 서비스를 활성화해야 합니다.")
                    .setPositiveButton("설정으로 이동", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("취소", null)
                    .show();
            tossButton.setEnabled(false);
        } else {
            tossButton.setEnabled(true);
        }
    }

    // 접근성 서비스에 작업을 트리거하는 메서드
    private void triggerAccessibilityService() {
        Intent intent = new Intent(this, MyAccessibilityService.class);
        intent.setAction("com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY");
        Log.d("MainActivity", "Starting service: " + intent.getAction());
        startForegroundService(intent);
    }

    // 접근성 서비스 활성화 여부 확인
    private boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        ComponentName expectedComponentName = new ComponentName(context, service);
        String enabledServices = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServices != null) {
            String[] componentNames = enabledServices.split(":");
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
