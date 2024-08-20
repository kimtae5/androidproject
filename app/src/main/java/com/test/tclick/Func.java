package com.test.tclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class Func {
    public AccessibilityService service;

    public Func(AccessibilityService service) {
        this.service = service;
    }

    public int[] getWindowSize() {
        return new int[]{
                service.getResources().getDisplayMetrics().widthPixels,
                service.getResources().getDisplayMetrics().heightPixels
        };
    }

    public void scrollMove(String move, int num, int back) {
        int[] size = getWindowSize();
        int width = size[0];
        int height = size[1];

        if ("up".equals(move)) {
            for (int i = 0; i < num; i++) {
                swipe(width / 2, height / 2, width / 2, height / 4);
                SystemClock.sleep(300);
            }
            Log.d("Func", "스크롤 " + move + " " + num + " 번");
        } else if ("down".equals(move)) {
            for (int i = 0; i < num; i++) {
                swipe(width / 2, height / 4, width / 2, height / 2);
                SystemClock.sleep(300);
            }
            Log.d("Func", "스크롤 " + move + " " + num + " 번");
        } else {
            for (int i = 0; i < num; i++) {
                swipe(width / 2, height / 2, width / 2, height / 4);
                swipe(width / 2, height / 4, width / 2, height / 2);
            }
            for (int i = 0; i < back; i++) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
            Log.d("Func", "스크롤 " + move + " " + num + " 번");
            Log.d("Func", "뒤로가기 " + back + " 번");
        }
    }

    private void swipe(int startX, int startY, int endX, int endY) {
        // Gesture를 사용하여 스와이프 구현이 필요합니다.
        // Android 7.0 (API 레벨 24) 이상에서 `GestureDescription` 클래스를 사용하여 구현할 수 있습니다.
    }

    public AccessibilityNodeInfo findElementByText(String text) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
            if (!nodes.isEmpty()) {
                return nodes.get(0);
            }
        }
        return null;
    }

    public AccessibilityNodeInfo findElementByTextContains(String text) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
            if (!nodes.isEmpty()) {
                for (AccessibilityNodeInfo node : nodes) {
                    if (node.getText() != null && node.getText().toString().contains(text)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    public AccessibilityNodeInfo findElementByViewId(String viewId) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(viewId);
            if (!nodes.isEmpty()) {
                return nodes.get(0);
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findSibling(AccessibilityNodeInfo parentElement, String siblingClass) {
        if (parentElement != null && parentElement.getParent() != null) {
            AccessibilityNodeInfo parent = parentElement.getParent();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo sibling = parent.getChild(i);
                if (sibling != null && sibling.getClassName().equals(siblingClass)) {
                    return sibling;
                }
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findChild(AccessibilityNodeInfo siblingElement, String childClass) {
        if (siblingElement != null) {
            int childCount = siblingElement.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = siblingElement.getChild(i);
                if (child != null && child.getClassName().equals(childClass)) {
                    return child;
                }
            }
        }
        return null;
    }

    private void clickElement(AccessibilityNodeInfo element) {
        if (element != null) {
            element.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void longClickElement(AccessibilityNodeInfo element) {
        if (element != null) {
            element.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        }
    }

    public boolean clickElement(AccessibilityNodeInfo element, String text, long sleepTime, int clicks, boolean longClick) {
        if (element != null) {
            for (int i = 0; i < clicks; i++) {
                if (longClick) {
                    longClickElement(element);
                } else {
                    clickElement(element);
                }
                Log.d("Func", text + " click");
                SystemClock.sleep(sleepTime);
            }
            return true;
        } else {
            Log.d("Func", "clickElement 함수에서 not found " + text + " element");
            return false;
        }
    }

    public boolean clickElement2(String text, long sleepTime, int clicks, String... args) {
        AccessibilityNodeInfo element = findElementByText(text);
        if (element != null) {
            for (int i = 0; i < clicks; i++) {
                clickElement(element);
                Log.d("Func", text + " 클릭");
                SystemClock.sleep(sleepTime);
            }
            return true;
        }
        return false;
    }

    public boolean findAndClick(String text, boolean contains, long sleepTime, int clicks, boolean longClick) {
        long startTime = System.currentTimeMillis();
        long timeout = 10000; // 최대 10초 동안 대기
        boolean timeoutReached = false; // 타임아웃 도달 여부

        while (true) {
            boolean isLoading = checkLoading();
            Log.d("Func", "checkLoading 결과: " + isLoading);
            if (isLoading) {
                SystemClock.sleep(500);
                break;
            } else {
                SystemClock.sleep(1000);
            }

            // 타임아웃 체크
            if (System.currentTimeMillis() - startTime > timeout) {
                Log.d("Func", "타임아웃 도달. 반복 종료");
                timeoutReached = true;
                break;
            }
        }

        // 타임아웃 도달 시 false 반환하고 종료
        if (timeoutReached) {
            return false;
        }

        AccessibilityNodeInfo element = contains ? findElementByTextContains(text) : findElementByText(text);
        if (element != null) {
            String elementText = getElementText(element);
            Log.d("Func", "Element 발견: " + elementText);

            if (isInvalidElement(element, elementText)) {
                Log.d("Func", "Element가 유효하지 않음: " + elementText);
                return false;
            } else {
                boolean clicked = clickElement(element, text, sleepTime, clicks, longClick);
                Log.d("Func", "Element 클릭 결과: " + clicked);
                return clicked;
            }
        } else {
            Log.d("Func", "findAndClick 함수에서 not found: " + text);
            return false;
        }
    }



    public void mainBack(String text, String text2, boolean contains) {
        long startTime = System.currentTimeMillis();
        long timeout = 3000;  // 최대 3초 동안 시도

        for (int i = 0; i < 10; i++) {
            SystemClock.sleep(300);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            Log.d("Func", "뒤로가기 시도 " + (i + 1));

            if (!text2.isEmpty()) {
                boolean clicked = findAndClick(text2, false, 500, 1, false);
                if (clicked) {
                    Log.d("Func", text2 + " 클릭 성공");
                } else {
                    Log.d("Func", text2 + " 클릭 실패");
                }
            }

            boolean found = (contains && findElementByTextContains(text) != null) ||
                    (!contains && findElementByText(text) != null);

            if (found) {
                Log.d("Func", text + " 발견됨");
                break;
            }

            // 타임아웃 체크
            if (System.currentTimeMillis() - startTime > timeout) {
                Log.d("Func", "타임아웃 도달. 반복 종료");
                break;
            }
        }
    }

    public void backKey(int num2) {
        for (int i = 0; i < num2; i++) {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            SystemClock.sleep(300);
        }
    }

    public void waitElement(boolean existElement, String... args) {
        for (int i = 0; i < 20; i++) {
            if (findElementByTextContains(args[0]) != null == existElement) {
                break;
            }
            SystemClock.sleep(300);
        }
    }

    private boolean checkLoading() {
        AccessibilityNodeInfo loading = findElementByTextContains("Loading");
        return loading != null;
    }

    private String getElementText(AccessibilityNodeInfo element) {
        CharSequence text = element.getText();
        return text != null ? text.toString() : "";
    }

    private boolean isInvalidElement(AccessibilityNodeInfo element, String text) {
        return text.equals("") || element.getContentDescription() != null;
    }

    public void deviceClick(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24 이상에서 GestureDescription 사용
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x, y);
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
            GestureDescription gesture = gestureBuilder.build();
            service.dispatchGesture(gesture, null, null);
        } else {
            // API 24 이하에서는 MotionEvent 사용
            MotionEvent eventDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, x, y, 0);
            MotionEvent eventUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100,
                    MotionEvent.ACTION_UP, x, y, 0);
//            service.dispatchTouchEvent(eventDown);
//            service.dispatchTouchEvent(eventUp);
            eventDown.recycle();
            eventUp.recycle();
        }
    }

    // 텍스트 필드를 찾아서 마지막 4자를 제외한 나머지 부분을 삭제하는 함수
    public void deleteTextExceptLast4(AccessibilityNodeInfo node) {
        if (node != null) {
            // 텍스트 필드 클릭
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

            // 전체 텍스트 선택
            CharSequence text = node.getText();
            if (text != null) {
                String textString = text.toString();
                int textLength = textString.length();

                if (textLength > 4) {
                    // 마지막 4자를 제외한 텍스트 선택
                    int start = 0;
                    int end = textLength - 4;
                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, start);
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, end);
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                    // 선택한 텍스트 삭제
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                } else {
                    // 텍스트 길이가 4보다 작을 경우 전체 텍스트를 삭제
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }
            }
        }
    }
}
