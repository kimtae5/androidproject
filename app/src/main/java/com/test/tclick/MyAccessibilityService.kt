package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";
    private static final String CHANNEL_ID = "my_accessibility_service_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();  // Notification channel 생성
    }

    private void scheduleServiceShutdown() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            disableSelf();
        }, 5000); // 5초 후 서비스 종료
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Event Type: " + event.getEventType());
        Log.d(TAG, "Package Name: " + event.getPackageName());

        // 특정 이벤트 타입을 감지
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//            if (rootNode != null) {
//                logAllNodes(rootNode, 0);
//            }
            scheduleServiceShutdown();

            if (event.getPackageName().equals("viva.republica.toss")) {
                // Toss 앱 실행 후 Click 클래스의 메서드 실행
                Func func = new Func(this); // Func 클래스 인스턴스를 생성 (가정)
                Click click = new Click(func);
                click.execute();
//                String[] searchTexts = {"홈", "혜택", "토스페이"};
//                for (String searchText : searchTexts) {
//                    AccessibilityNodeInfo node = findNodeByText(rootNode, searchText);
//                    if (node != null) {
//                        Log.d(TAG, "찾은 요소: " + node.getText());
//                        performClick(node);
//                        try {
//                            Thread.sleep(1000); // 1초 대기
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.d(TAG, searchText + " 텍스트를 가진 요소를 찾을 수 없습니다.");
//                    }
//                }
            }
            // 모든 작업이 완료되면 서비스 종료
            stopForeground(true);
            disableSelf();
        }
    }

    private void performClick(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            boolean clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (clicked) {
                Log.d(TAG, "요소 클릭 성공");
            } else {
                Log.d(TAG, "요소 클릭 실패");
            }
        } else {
            Log.d(TAG, "요소가 클릭할 수 없습니다. 부모 노드를 클릭 시도합니다.");
            AccessibilityNodeInfo parent = node.getParent();
            while (parent != null) {
                if (parent.isClickable()) {
                    boolean clicked = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    if (clicked) {
                        Log.d(TAG, "부모 요소 클릭 성공 : " + node.getText());
                        break;
                    }
                }
                parent = parent.getParent();
            }
        }
    }

    private void logAllNodes(AccessibilityNodeInfo node, int level) {
        if (node == null) {
            return;
        }

        // 로그 출력
        String indent = " ".repeat(level * 2);  // 들여쓰기
        String className = node.getClassName() != null ? node.getClassName().toString() : "null";
        String text = node.getText() != null ? node.getText().toString() : "null";
        String contentDescription = node.getContentDescription() != null ? node.getContentDescription().toString() : "null";

        Log.d(TAG, indent + "ClassName: " + className);
        if (node.getText() != null) {
            Log.d(TAG, indent + "Text: " + text);
        }
        if (node.getContentDescription() != null) {
            Log.d(TAG, indent + "ContentDescription: " + contentDescription);
        }

        // 자식 노드 재귀 호출
        for (int i = 0; i < node.getChildCount(); i++) {
            logAllNodes(node.getChild(i), level + 1);
        }
    }


    @Override
    public void onInterrupt() {
        // 필요 시 인터럽트 처리
        stopForeground(true);
        disableSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service Unbound");
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service Connected");

        // Foreground 서비스로 설정
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");

        // Foreground 서비스로 설정
        startForeground(NOTIFICATION_ID, createNotification());

        if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY".equals(intent.getAction())) {
            Log.d(TAG, "Triggering Toss App Launch");
            launchTossApp();
        } else {
            // 인텐트가 없으면 서비스 종료
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY; // 서비스가 종료된 후 다시 시작하지 않도록 설정
    }


    private void launchTossApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
        Log.d(TAG, "Launch Intent: " + (launchIntent != null));
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);

            // 앱 실행 후 특정 시간 대기
            new Handler().postDelayed(() -> {
                // 작업이 완료되면 서비스 종료
                stopForeground(true);
                stopSelf();
            }, 3000); // 예를 들어 3초 후
        } else {
            Log.d(TAG, "Toss 앱을 찾을 수 없습니다.");
            Toast.makeText(this, "Toss 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) return null;

        if (text.equals(root.getText())) {
            return root;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByText(root.getChild(i), text);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Accessibility Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("서비스 실행 중")
                .setContentText("MyAccessibilityService가 실행 중입니다.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
    }

    private void startForegroundServiceWithType() {
        Notification notification = createNotification();

        // Android Q(API 레벨 29) 이상에서는 FOREGROUND_SERVICE_TYPE_MANIFEST를 지정합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();  // 서비스가 종료되도록 확실히 처리
        Log.d(TAG, "Service Destroyed");
    }


}
