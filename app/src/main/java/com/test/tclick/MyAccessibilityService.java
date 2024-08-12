package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            performClickOnTarget();
        }
    }

    private void performClickOnTarget() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            Log.d(TAG, "Root node available. Searching for target node...");
            AccessibilityNodeInfo targetNode = findNodeByText(rootNode, "혜택");
            if (targetNode != null) {
                Log.d(TAG, "Target node found. Performing click...");
                targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.d(TAG, "Target node not found.");
            }
        } else {
            Log.d(TAG, "Root node is null.");
        }
    }

    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo rootNode, String text) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }


    @Override
    public void onInterrupt() {
        // Do nothing
    }
}
