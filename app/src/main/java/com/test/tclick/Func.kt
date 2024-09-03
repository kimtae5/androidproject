package com.test.tclick

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Rect
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

    fun getScreenSize(context: Context): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
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

    fun clickElement(element: AccessibilityNodeInfo, text: String, sleepTime: Long = 500, clicks: Int = 1, longClick: Boolean = false): Boolean {
        return if (element.isVisibleToUser) {
            Log.d("Func", "$text, $clicks time click")
            repeat(clicks) {
                if (longClick) {
                    element.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                } else {
                    element.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                Thread.sleep(sleepTime)
            }
            true
        } else {
            Log.d("Func", "clickElement function: not found $text element")
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
        text: String, contains: Boolean = false, sleepTime: Long = 500, clicks: Int = 1,
        needElements: Int = 1, longClick: Boolean = false, word: Boolean = true, load: Boolean = true
    ): Boolean {
//        if (load) {
//            waitForElementToLoad(needElements)
//        }

        val element = if (contains) findElementByTextContains(text) else findElementByText(text)

        return if (element?.isVisibleToUser == true) {
            if (word) {
                val elementText = element.text.toString()
                if (elementText.contains("완료") || elementText.contains("후에")) {
                    Log.d("Func", "$text element contains '완료' or '후에'")
                    false
                } else {
                    clickElement(element, text, sleepTime, clicks, longClick)
                }
            } else {
                clickElement(element, text, sleepTime, clicks, longClick)
            }
        } else {
            Log.d("Func", "findAndClick function: not found: $text")
            false
        }
    }

    fun mainBack(text: String, text2: String = "", contains: Boolean = false, sleepTime: Long = 300) {
        var cnt = 0
        repeat(25) {
            if (text2.isNotEmpty()) {
                findAndClick(text2)
            }
            val exists = if (contains) findElementByTextContains(text)?.isVisibleToUser == true else findElementByText(text)?.isVisibleToUser == true
            if (exists) {
                Log.d("Func", "find $text, $cnt time back")
                return
            }
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            Thread.sleep(sleepTime)
            cnt++
            Log.d("Func", "$cnt time back")
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
                    clickElement(childElement, childElement.text.toString(), sleepTime, clicks)
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
