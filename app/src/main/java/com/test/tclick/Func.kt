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

    private val width: Int
    private val height: Int

    init {
        // Get the screen width and height
        val size = getScreenSize(service)
        width = size.first
        height = size.second
    }

    private fun getScreenSize(service: AccessibilityService): Pair<Int, Int> {
        val windowManager = service.getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        return Pair(width, height)
    }

    fun getScreenResolution() {
        Log.d("Func", "Screen resolution: ${width}x${height}")
    }

    fun scrollMove(move: String = "up", num: Int = 1, back: Int = 0) {
        val swipeYStart = if (move == "up") height / 2 else height / 4
        val swipeYEnd = if (move == "up") height / 4 else height / 2

        repeat(num) {
            Log.d("Func", "Scroll $move, $num time")
            // Implement swipe action
            Thread.sleep(300)
        }

        repeat(back) {
            Log.d("Func", "$back time back")
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        }
    }

    private fun findText(text: String): Boolean {
        val rootNode = service.rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        return nodes.isNotEmpty()
    }

    fun clickElement(
        element: AccessibilityNodeInfo,
        longClick: Boolean
    ): Boolean {
        return if (element.isVisibleToUser) {
            if (longClick) {
                element.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
            } else {
                element.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            true
        } else {
            false
        }
    }


    fun clickElement2(text: String, sleepTime: Long = 500, clicks: Int = 1, vararg args: String): Boolean {
        val element = findElement(*args) ?: return false
        return if (element.isVisibleToUser) {
            Log.d("Func", "$text click $clicks times")
            repeat(clicks) {
                element.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Thread.sleep(sleepTime)
            }
            true
        } else {
            false
        }
    }

    fun findAndClick(
        text: String, // 찾을 텍스트
        contains: Boolean = false, // 부분 일치 여부
        sleepTime: Long = 500, // 대기 시간
        clicks: Int = 1, // 클릭 횟수
        needElements: Int = 1, // 필요한 요소 수
        longClick: Boolean = false, // 롱 클릭 여부
        word: Boolean = true // 텍스트 일치 여부
    ): Boolean {
        val rootNode = service.rootInActiveWindow ?: return false

        // ProgressBar나 로딩 텍스트를 감지
        repeat(20) {
            val progressBars = rootNode.findAccessibilityNodeInfosByViewId("android:id/progress")
            val loadingTexts = rootNode.findAccessibilityNodeInfosByText("Loading")
            val navBackgrounds = rootNode.findAccessibilityNodeInfosByViewId("com.android.systemui:id/navigationBarBackground")

            val totalElementsFound = progressBars.size + loadingTexts.size + navBackgrounds.size
            if (totalElementsFound < needElements) {
                SystemClock.sleep(500)
                return@repeat
            } else {
                Log.d("Func", "Found progress: ${progressBars.size}, loading: ${loadingTexts.size}, nav: ${navBackgrounds.size}. Waiting...")
                SystemClock.sleep(1000)
            }
        }

        // 텍스트 검색 (부분 일치 또는 전체 일치)
        val elements = rootNode.findAccessibilityNodeInfosByText(text)
        Log.d("Func", "Found ${elements.size} elements with text '$text'")

        val element: AccessibilityNodeInfo? = if (contains) {
            elements.firstOrNull() // 부분 일치
        } else {
            elements.firstOrNull { it.text.toString() == text } // 전체 일치
        }

        if (element != null) {
            Log.d("Func", "Found element with text '$text'")
            if (word) {
                val elementText = element.text.toString()

                // 형제 요소나 텍스트에서 특정 단어 체크
                if (hasSiblingWithText(element, "완료") || elementText.contains("완료") || elementText.contains("후에")) {
                    Log.d("Func", "Element $text contains '완료' or '후에', skipping click.")
                    return false
                } else {
                    repeat(clicks) {
                        clickElement(element, longClick)
                        SystemClock.sleep(sleepTime)
                    }
                    return true
                }
            } else {
                repeat(clicks) {
                    clickElement(element, longClick)
                    SystemClock.sleep(sleepTime)
                }
                return true
            }
        }

        Log.d("Func", "Element with text $text not found.")
        return false
    }

    // 형제 요소에서 특정 텍스트가 있는지 확인하는 함수
    fun hasSiblingWithText(node: AccessibilityNodeInfo, text: String): Boolean {
        val parentNode = node.parent ?: return false
        for (i in 0 until parentNode.childCount) {
            val siblingNode = parentNode.getChild(i)
            if (siblingNode != null && siblingNode.text?.toString() == text) {
                return true
            }
        }
        return false
    }

    fun mainBack(
        text2: String = "",  // 눌러야 할 텍스트
        text1: String = "",  // 루프 종료 조건 텍스트
        sleepTime: Long = 300,
        maxAttempts: Int = 25
    ) {
        var cnt = 0
        while (cnt < maxAttempts) {
            // 종료 조건: text1이 화면에 있으면 루프 종료
            if (findText(text1)) {
                Log.d("Func", "Found $text1, stopping back press at attempt $cnt")
                break
            }

            // 클릭할 text2가 있는 경우 클릭
            if (text2.isNotEmpty()) {
                findAndClick(text2)
            }

            // 백 버튼 누르기
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            SystemClock.sleep(sleepTime)

            cnt++
            Log.d("Func", "Performed back press $cnt time(s)")
        }
    }

    fun backKey(num: Int = 1) {
        Log.d("Func", "$num time back")
        repeat(num) {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            Thread.sleep(300)
        }
    }

    fun waitElement(existElement: Boolean = true, vararg args: String) {
        repeat(8) {
            val exists = findElement(*args)?.isVisibleToUser == existElement
            if (exists) {
                Thread.sleep(1000)
                return
            } else {
                Thread.sleep(2000)
                Log.d("Func", "waiting.... $args not found")
            }
        }
    }

    fun findAndClickSiblingChild(
        parentText: String, siblingClass: String, childClass: String,
        contains: Boolean = false, sleepTime: Long = 500, clicks: Int = 1
    ): Boolean {
        val parentElement = if (contains) findElementByTextContains(parentText) else findElementByText(parentText)
        return if (parentElement?.isVisibleToUser == true) {
            val siblingElement = parentElement.findSiblingByClass(siblingClass)
            if (siblingElement?.isVisibleToUser == true) {
                val childElement = siblingElement.findChildByClass(childClass)
                if (childElement?.isVisibleToUser == true) {
                    clickElement(childElement, longClick = false)
                    true
                } else {
                    Log.d("Func", "Child element with class $childClass not found under sibling with class $siblingClass")
                    false
                }
            } else {
                Log.d("Func", "Sibling element with class $siblingClass not found")
                false
            }
        } else {
            Log.d("Func", "Parent element with text $parentText not found")
            false
        }
    }

    fun findAndClickWithinBounds(
        textContains: String, endText: String, interval: Int = 120,
        startX: Float = 200f, startY: Float = 0.74f
    ) {
        val element = findElementByTextContains(textContains)
        if (element?.isVisibleToUser == true) {
            val bounds = Rect()
            element.getBoundsInScreen(bounds)
            Log.d("Func", "Element bounds: $bounds")

            for (offset in 0 until (bounds.bottom - bounds.top) step interval) {
                val clickX = startX + offset
                val clickY = startY
                Log.d("Func", "Clicking at ($clickX, $clickY)")
                // Implement click action
                Thread.sleep(500)
                if (findElementByTextContains(endText)?.isVisibleToUser == false) {
                    Log.d("Func", "Screen transition detected")
                    break
                }
            }
        }
    }

    // Implementation for helper methods
    fun findElementByText(text: String): AccessibilityNodeInfo? {
        return service.rootInActiveWindow?.findAccessibilityNodeInfosByText(text)?.firstOrNull()
    }

    fun findElementByTextContains(text: String): AccessibilityNodeInfo? {
        return service.rootInActiveWindow?.findAccessibilityNodeInfosByText(text)?.firstOrNull()
    }

    private fun findElement(vararg args: String): AccessibilityNodeInfo? {
        // Implement a method to search for an element based on multiple arguments (text, resource ID, etc.)
        // Example: Find element by text or other attributes
        return service.rootInActiveWindow?.findAccessibilityNodeInfosByText(args.firstOrNull() ?: "")?.firstOrNull()
    }

    private fun AccessibilityNodeInfo.findSiblingByClass(className: String): AccessibilityNodeInfo? {
        val parent = this.parent ?: return null
        for (i in 0 until parent.childCount) {
            val sibling = parent.getChild(i)
            if (sibling != null && sibling.className == className && sibling != this) {
                return sibling
            }
        }
        return null
    }

    private fun AccessibilityNodeInfo.findChildByClass(className: String): AccessibilityNodeInfo? {
        for (i in 0 until this.childCount) {
            val child = this.getChild(i)
            if (child != null && child.className == className) {
                return child
            }
        }
        return null
    }

    private val AccessibilityNodeInfo.isVisibleToUser: Boolean
        get() = this.isVisibleToUser

    private val AccessibilityNodeInfo.bounds: Rect
        get() {
            val rect = Rect()
            this.getBoundsInScreen(rect)
            return rect
        }
}
