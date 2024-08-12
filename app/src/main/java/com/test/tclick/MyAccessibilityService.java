package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {

    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyAccessibilityService", "Service Created");

        // 브로드캐스트 리시버 설정
        IntentFilter filter = new IntentFilter("com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("MyAccessibilityService", "Broadcast received");
                if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY".equals(intent.getAction())) {
                    Log.d("MyAccessibilityService", "Broadcast received: Triggering Toss App Launch");
                    launchTossApp();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyAccessibilityService", "Service Destroyed");
        unregisterReceiver(receiver);
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("MyAccessibilityService", "Event Type: " + event.getEventType());
        Log.d("MyAccessibilityService", "Package Name: " + event.getPackageName());
        // 특정 이벤트 처리: 예를 들어, 새로운 윈도우가 열렸을 때
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            Log.d("MyAccessibilityService", "Window State Changed: " + packageName);

            if (packageName.equals("viva.republica.toss")) {
                // 혜택 버튼을 찾아 클릭
                findAndClickBenefitButton();
            }
        }
    }

    @Override
    public void onInterrupt() {
        // 인터럽트 처리 (필요한 경우)
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("MyAccessibilityService", "Service Connected");
    }

    // BroadcastReceiver를 통해 트리거 받기
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyAccessibilityService", "onStartCommand called");

        if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY".equals(intent.getAction())) {
            Log.d("MyAccessibilityService", "Broadcast received: Triggering Toss App Launch");
            launchTossApp();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // viva.republica.toss 앱 실행 메서드
    private void launchTossApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
        Log.d("MyAccessibilityService", "Launch Intent: " + (launchIntent != null));
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Log.d("MyAccessibilityService", "Toss 앱을 찾을 수 없습니다.");
        }
    }

    // 혜택 버튼을 찾아 클릭하는 메서드
    private void findAndClickBenefitButton() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            Log.d("MyAccessibilityService", "Root Node Available");
            boolean clicked = findAndPerformClick(rootNode, "혜택");
            if (!clicked) {
                Log.d("MyAccessibilityService", "Button with text '혜택' not found");
            }
        } else {
            Log.d("MyAccessibilityService", "Root Node is null");
        }
    }

    // 텍스트로 노드를 찾아 클릭하는 재귀적 메서드
    private boolean findAndPerformClick(AccessibilityNodeInfo nodeInfo, String text) {
        if (nodeInfo == null) {
            return false;
        }

        if (nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text)) {
            Log.d("MyAccessibilityService", "Found button with text: " + text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            if (findAndPerformClick(nodeInfo.getChild(i), text)) {
                return true;
            }
        }

        return false;
    }
}
