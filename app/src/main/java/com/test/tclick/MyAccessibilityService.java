package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Event Type: " + event.getEventType());
        Log.d(TAG, "Package Name: " + event.getPackageName());

        // 특정 이벤트 타입을 감지 (여기서는 모든 이벤트에서 탐색할 수 있도록 설정)
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                logAllNodes(rootNode, 0);
            }
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            String searchText = "홈";
            AccessibilityNodeInfo node = findNodeByText(rootNode, searchText);
            if (node != null) {
                Log.d(TAG, "찾은 요소: " + node.getText());

                // 요소 클릭
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
                                Log.d(TAG, "부모 요소 클릭 성공");
                                break;
                            }
                        }
                        parent = parent.getParent();
                    }
                }

            } else {
                Log.d(TAG, searchText + " 텍스트를 가진 요소를 찾을 수 없습니다.");
            }
        }

//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            String packageName = event.getPackageName().toString();
//            Log.d(TAG, "Window State Changed: " + packageName);
//
//            if (packageName.equals("viva.republica.toss")) {
//                findAndClickBenefitButton();
//            }
//        }
    }

    private void logAllNodes(AccessibilityNodeInfo nodeInfo, int depth) {
        // 현재 노드 정보 출력
        StringBuilder logPrefix = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            logPrefix.append("--");
        }

        // 노드의 텍스트, 클래스명, 리소스 ID 등의 정보 출력
        Log.d(TAG, logPrefix + "ClassName: " + nodeInfo.getClassName());
        if (nodeInfo.getText() != null) {
            Log.d(TAG, logPrefix + "Text: " + nodeInfo.getText());
        }
        if (nodeInfo.getContentDescription() != null) {
            Log.d(TAG, logPrefix + "ContentDescription: " + nodeInfo.getContentDescription());
        }
        if (nodeInfo.getViewIdResourceName() != null) {
            Log.d(TAG, logPrefix + "ViewId: " + nodeInfo.getViewIdResourceName());
        }

        // 자식 노드 탐색
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            logAllNodes(nodeInfo.getChild(i), depth + 1);
        }
    }

    @Override
    public void onInterrupt() {
        // 필요 시 인터럽트 처리
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service Connected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");

        if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY".equals(intent.getAction())) {
            Log.d(TAG, "Triggering Toss App Launch");
            launchTossApp();
        }
        return START_STICKY;
    }

    private void launchTossApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
        Log.d(TAG, "Launch Intent: " + (launchIntent != null));
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
        } else {
            Log.d(TAG, "Toss 앱을 찾을 수 없습니다.");
            Toast.makeText(this, "Toss 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void findAndClickBenefitButton() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                Log.d(TAG, "Root Node Available");
                boolean clicked = findAndPerformClick(rootNode, "혜택");
                if (!clicked) {
                    Log.d(TAG, "Button with text '혜택' not found");
                }
            } else {
                Log.d(TAG, "Root Node is null");
            }
            if (rootNode != null) {
                Log.d(TAG, "Root Node Available");
                boolean clicked = findAndPerformClick(rootNode, "토스페이");
                if (!clicked) {
                    Log.d(TAG, "Button with text '토스페이' not found");
                }
            } else {
                Log.d(TAG, "Root Node is null");
            }
            if (rootNode != null) {
                Log.d("MyAccessibilityService", "Root Node Available");
                boolean clicked = findAndPerformClick(rootNode, "전체");
                if (!clicked) {
                    Log.d(TAG, "Button with text '전체' not found");
                }
            } else {
                Log.d(TAG, "Root Node is null");
            }
        }, 3000);  // 2초 대기
    }


    private boolean findAndPerformClick(AccessibilityNodeInfo nodeInfo, String text) {
        if (nodeInfo == null) {
            return false;
        }

        // 텍스트로 UI 요소 찾기
        if (nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text)) {
            Log.d(TAG, "Found button with text: " + text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }

//        // 컨텐츠 설명으로 UI 요소 찾기
//        if (nodeInfo.getContentDescription() != null && nodeInfo.getContentDescription().toString().contains(text)) {
//            Log.d(TAG, "Found button with content description: " + text);
//            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            return true;
//        }
//
//        // 클래스 이름으로 UI 요소 찾기
//        if ("android.widget.Button".contentEquals(nodeInfo.getClassName()) && nodeInfo.getText() != null && nodeInfo.getText().toString().contains(text)) {
//            Log.d(TAG, "Found button of class android.widget.Button with text: " + text);
//            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            return true;
//        }

        // 자식 노드 탐색 (재귀적 접근)
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            if (findAndPerformClick(nodeInfo.getChild(i), text)) {
                return true;
            }
        }

        return false;
    }

    public AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) return null;

        // 텍스트가 일치하는 경우
        if (text.equals(root.getText())) {
            return root;
        }

        // 자식 요소 탐색
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByText(root.getChild(i), text);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

}
