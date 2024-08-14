package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;

public class Func extends AccessibilityService {

    private int width;
    private int height;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        System.out.println("Screen resolution: " + width + "x" + height);
    }

    // 스크롤 이동 메서드
    public void scrollMove(String move, int num, int back) {
        int action = move.equals("up") ? AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD : AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
        for (int i = 0; i < num; i++) {
            performGlobalAction(action);
            sleep(300);
        }
        for (int i = 0; i < back; i++) {
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    // 요소 클릭 메서드
    public boolean clickElement(AccessibilityNodeInfo element, String text, long sleepTime, int clicks, boolean longClick) {
        if (element != null) {
            for (int i = 0; i < clicks; i++) {
                if (longClick) {
                    element.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                } else {
                    element.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                sleep(sleepTime);
            }
            return true;
        } else {
            System.out.println("Element not found: " + text);
            return false;
        }
    }

    // 텍스트로 요소 클릭 메서드
    public boolean clickElement2(String text, long sleepTime, int clicks, String... attributes) {
        AccessibilityNodeInfo element = findElementByAttributes(attributes);
        return clickElement(element, text, sleepTime, clicks, false);
    }

    // 텍스트로 요소 찾기
    private AccessibilityNodeInfo findNodeByText(String text, boolean contains) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            if (contains) {
                return findNodeByTextContains(root, text);
            } else {
                return findNodeByTextEquals(root, text);
            }
        }
        return null;
    }

    // 텍스트 포함 요소 찾기
    private AccessibilityNodeInfo findNodeByTextContains(AccessibilityNodeInfo root, String text) {
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null && child.getText() != null && child.getText().toString().contains(text)) {
                return child;
            }
            AccessibilityNodeInfo result = findNodeByTextContains(child, text);
            if (result != null) return result;
        }
        return null;
    }

    // 텍스트 일치 요소 찾기
    private AccessibilityNodeInfo findNodeByTextEquals(AccessibilityNodeInfo root, String text) {
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null && text.equals(child.getText())) {
                return child;
            }
            AccessibilityNodeInfo result = findNodeByTextEquals(child, text);
            if (result != null) return result;
        }
        return null;
    }

    // 형제 요소의 자식 찾기 및 클릭
    public boolean findAndClickSiblingChild(String parentText, String siblingClass, String childClass, boolean contains, long sleepTime, int clicks) {
        AccessibilityNodeInfo parentNode = findNodeByText(parentText, contains);
        if (parentNode != null) {
            AccessibilityNodeInfo siblingNode = findSiblingNode(parentNode, siblingClass);
            if (siblingNode != null) {
                AccessibilityNodeInfo childNode = findChildNode(siblingNode, childClass);
                return clickElement(childNode, childNode.getText().toString(), sleepTime, clicks, false);
            }
        }
        return false;
    }

    // 형제 요소 찾기
    private AccessibilityNodeInfo findSiblingNode(AccessibilityNodeInfo parent, String siblingClass) {
        AccessibilityNodeInfo parentNode = parent.getParent();
        if (parentNode != null) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                AccessibilityNodeInfo sibling = parentNode.getChild(i);
                if (sibling != null && siblingClass.equals(sibling.getClassName())) {
                    return sibling;
                }
            }
        }
        return null;
    }

    // 자식 요소 찾기
    private AccessibilityNodeInfo findChildNode(AccessibilityNodeInfo sibling, String childClass) {
        for (int i = 0; i < sibling.getChildCount(); i++) {
            AccessibilityNodeInfo child = sibling.getChild(i);
            if (child != null && childClass.equals(child.getClassName())) {
                return child;
            }
        }
        return null;
    }

    // 특정 텍스트 포함 요소 클릭
    public void findAndClickWithinBounds(String textContains, String endText, int interval, int startX, float startY) {
        AccessibilityNodeInfo element = findNodeByText(textContains, true);
        if (element != null) {
            Rect bounds = new Rect();
            element.getBoundsInScreen(bounds);
            for (int offset = 0; offset < (bounds.bottom - bounds.top); offset += interval) {
                int clickX = startX + offset;
                int clickY = (int) (startY * height);
                performClickAtPosition(clickX, clickY);
                sleep(500);
                if (findNodeByText(endText, true) == null) {
                    break;
                }
            }
        }
    }

    // 특정 위치 클릭 메서드
    private void performClickAtPosition(int x, int y) {
        // X, Y 좌표에서 클릭을 수행하는 로직 구현 필요
        // 예: AccessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    // 대기 메서드
    private void sleep(long millis) {
        new Handler().postDelayed(() -> {}, millis);
    }

    // 여러 속성을 가진 요소 찾기
    private AccessibilityNodeInfo findElementByAttributes(String... attributes) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            for (String attribute : attributes) {
                AccessibilityNodeInfo element = findNodeByText(attribute, false);
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }
}
