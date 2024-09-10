package com.test.tclick

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Rect
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import kotlin.math.roundToInt

class Func(private val service: AccessibilityService) {

    fun findTextElements(rootNode: AccessibilityNodeInfo) {
        // rootNode가 null인 경우 함수 종료
        if (rootNode == null) return

        // 찾고자 하는 텍스트 목록
        val textsToFind = listOf("혜택", "혜택", "친구와", "1원", "보너스")

        // 각 텍스트에 대해 탐색
        for (text in textsToFind) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(text)
            if (nodes.isNotEmpty()) {
                Log.d("MyAccessibilityService", "Found text: $text")

                // 첫 번째 노드 탐색
                nodes.firstOrNull()?.let { node ->
                    if (node.isClickable) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d("MyAccessibilityService", "Clicked text: $text")
                    } else {
                        // 부모 노드 중 클릭 가능한 노드 찾기
                        var parent = node.parent
                        while (parent != null) {
                            if (parent.isClickable) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                Log.d("MyAccessibilityService", "Clicked parent node for text: $text")
                                break
                            }
                            parent = parent.parent
                        }
                        if (parent == null) {
                            Log.d("MyAccessibilityService", "No clickable parent found for text: $text")
                        }
                    }
                    SystemClock.sleep(1000) // 1초 대기
                }
            } else {
                Log.d("MyAccessibilityService", "Text not found: $text")
            }
        }
    }

    fun mainBack1(
        service: AccessibilityService,  // AccessibilityService 인스턴스를 받음
        rootNode: AccessibilityNodeInfo,
        targetText: String = "",  // 찾아야 할 텍스트
        sleepTime: Long = 300,    // 버튼 클릭 후 대기 시간
        maxAttempts: Int = 25     // 최대 시도 횟수
    ) {
        if (rootNode == null) return
        var attemptCount = 0
        var textFound = false

        while (attemptCount < maxAttempts) {
            // targetText가 화면에 있는지 확인
            val nodes = rootNode.findAccessibilityNodeInfosByText(targetText)
            if (nodes.isNotEmpty()) {
                Log.d("Func", "$targetText found, stopping back press at attempt $attemptCount")
                // targetText를 찾으면 작업을 멈춤
                return
            } else {
                // targetText가 화면에 없으면 백 버튼 누르기
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                SystemClock.sleep(sleepTime)

                attemptCount++
                Log.d("Func", "Performed back press $attemptCount time(s)")
            }
        }

        Log.d("Func", "Exceeded maximum attempts of $maxAttempts without finding $targetText")
    }

    fun getScreenSize(service: AccessibilityService): Pair<Int, Int>? {
        val displayMetrics = service.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        return Pair(width, height)
    }

    fun scrollMove(service: AccessibilityService, rootNode: AccessibilityNodeInfo?, move: String = "up", num: Int = 1, back: Int = 0) {
        val (width, height) = getScreenSize(service) ?: return
        for (i in 1..num) {
            when (move) {
                "up" -> {
                    Log.d("Func", "Scrolling up $num times")
                    rootNode?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
                    SystemClock.sleep(500)
                }
                "down" -> {
                    Log.d("Func", "Scrolling down $num times")
                    rootNode?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                    SystemClock.sleep(500)
                }
                else -> {
                    Log.d("Func", "Unknown scroll direction")
                }
            }
        }
        for (i in 1..back) {
            Log.d("Func", "Pressing back button $back times")
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            SystemClock.sleep(500)
        }
    }

    fun clickElement(element: AccessibilityNodeInfo?, text: String, sleepTime: Long = 500, clicks: Int = 1, longClick: Boolean = false): Boolean {
        element?.let {
            for (i in 1..clicks) {
                if (longClick) {
                    Log.d("Func", "Long clicking on $text")
                    it.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                } else {
                    Log.d("Func", "Clicking on $text")
                    it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                SystemClock.sleep(sleepTime)
            }
            return true
        }
        Log.d("Func", "Element $text not found")
        return false
    }

    fun findAndClick(service: AccessibilityService, rootNode: AccessibilityNodeInfo?, text: String, contains: Boolean = false, sleepTime: Long = 500, clicks: Int = 1, longClick: Boolean = false): Boolean {
        rootNode?.let {
            val elements = if (contains) {
                it.findAccessibilityNodeInfosByText(text)
            } else {
                it.findAccessibilityNodeInfosByViewId(text)
            }

            elements?.let { nodes ->
                nodes.forEach { node ->
                    if (node.text.contains("완료") || node.text.contains("후에")) {
                        Log.d("Func", "Skipping '완료' or '후에'")
                        return false
                    }
                    return clickElement(node, text, sleepTime, clicks, longClick)
                }
            }
        }
        Log.d("Func", "Text $text not found")
        return false
    }

    fun mainBack(service: AccessibilityService, rootNode: AccessibilityNodeInfo,targetText: String = "", text2: String = "", sleepTime: Long = 1000) {
        var count = 0
        while (count < 25) {
            SystemClock.sleep(sleepTime)
            if (text2.isNotEmpty()) {
                findAndClick(service, rootNode, text2)
            }
            val nodes = rootNode.findAccessibilityNodeInfosByText(targetText)
            if (nodes.isNotEmpty()) {
                Log.d("Func", "$targetText found, stopping back press at attempt $count")
                // targetText를 찾으면 작업을 멈춤
                return
            } else {
                // targetText가 화면에 없으면 백 버튼 누르기
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                SystemClock.sleep(sleepTime)

                count++
                Log.d("Func", "Performed back press $count time(s)")
            }

            Log.d("Func", "$count time back")
            Log.d("Func", "Exceeded maximum attempts of $count without finding $targetText")
        }
    }

    fun backKey(service: AccessibilityService, num: Int = 1) {
        for (i in 1..num) {
            Log.d("Func", "Pressing back key")
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            SystemClock.sleep(300)
        }
    }

    fun waitElement(service: AccessibilityService, exist: Boolean = true, rootNode: AccessibilityNodeInfo?, text: String) {
        for (i in 1..8) {
            if (rootNode != null) {
                if (exist) {
                    val elementExists = rootNode.findAccessibilityNodeInfosByText(text).isNotEmpty()
                    if (elementExists) {
                        Log.d("Func", "Element $text found")
                        SystemClock.sleep(1000)
                        return  // break 대신에 return으로 함수 자체를 종료
                    } else {
                        Log.d("Func", "Waiting for $text element to appear...")
                        SystemClock.sleep(2000)
                    }
                } else {
                    val elementExists = rootNode.findAccessibilityNodeInfosByText(text).isEmpty()
                    if (elementExists) {
                        Log.d("Func", "Waiting for $text element to disappear...")
                        return  // break 대신에 return으로 함수 자체를 종료
                    } else {
                        Log.d("Func", "$text element still exists...")
                        SystemClock.sleep(2000)
                    }
                }
            }
        }
    }


    fun findAndClickSiblingChild(service: AccessibilityService, rootNode: AccessibilityNodeInfo?, parentText: String, siblingClass: String, childClass: String, contains: Boolean = false, sleepTime: Long = 500, clicks: Int = 1): Boolean {
        val parentElement = if (contains) {
            rootNode?.findAccessibilityNodeInfosByText(parentText)
        } else {
            rootNode?.findAccessibilityNodeInfosByViewId(parentText)
        }

        parentElement?.let { nodes ->
            nodes.forEach { parent ->
                val siblingElement = parent.getChild(0) // Assume sibling is the first child. Adjust if needed.
                siblingElement?.let { sibling ->
                    val childElement = sibling.getChild(0) // Adjust index accordingly.
                    if (childElement != null) {
                        return clickElement(childElement, childClass, sleepTime, clicks)
                    } else {
                        Log.d("Func", "Child element not found")
                    }
                } ?: run {
                    Log.d("Func", "Sibling element not found")
                }
            }
        }
        Log.d("Func", "Parent element with text $parentText not found")
        return false
    }

    fun findAndClickWithinBounds(service: AccessibilityService, textContains: String, endText: String, rootNode: AccessibilityNodeInfo?, interval: Int = 120, startX: Int = 200, startY: Int = 740) {
        rootNode?.let {
            val elements = it.findAccessibilityNodeInfosByText(textContains)
            elements?.forEach { element ->
                val bounds = Rect() // Rect 객체 생성
                element.getBoundsInScreen(bounds) // 경계를 가져오는 메소드 사용
                Log.d("Func", "Element bounds: $bounds")

                // 경계를 기반으로 클릭 좌표 설정
                for (offset in 0 until bounds.bottom - bounds.top step interval) {
                    val clickX = startX + offset
                    val clickY = startY
                    Log.d("Func", "Clicking at ($clickX, $clickY)")

                    // 해당 요소에 대해 클릭 동작 수행
                    element.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    SystemClock.sleep(500)

                    // 화면 전환 감지
                    if (it.findAccessibilityNodeInfosByText(endText).isEmpty()) {
                        Log.d("Func", "Screen transition detected after click")
                        break
                    }
                }
            }
        }
    }
}
