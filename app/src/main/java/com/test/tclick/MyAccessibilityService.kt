package com.test.tclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // event가 null인지 확인
        if (event != null) {
            // source를 안전하게 캐스팅
            val source: AccessibilityNodeInfo? = event.source

            // source가 null이 아닌지 확인
            if (source != null) {
                // 예시: 특정 텍스트를 가진 버튼을 찾아 클릭
                if (source.className == "android.widget.Button" && source.text == "특정 버튼 텍스트") {
                    source.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        }
    }

    override fun onInterrupt() {
        // 서비스가 중단될 때 필요한 작업
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            packageNames = null // 모든 앱에서 이벤트를 수신
        }
        serviceInfo = info
    }

    fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
