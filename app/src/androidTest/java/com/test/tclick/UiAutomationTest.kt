package com.test.tclick

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiAutomationTest {

    @Test
    fun testUiAutomation() {
        // Obtain UiDevice instance
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        try {
            // Wait for the app to open
            device.waitForIdle()

            // "혜택" 텍스트가 포함된 요소 클릭
            val 혜택 = device.findObject(UiSelector().text("혜택"))
            if (혜택.exists()) {
                혜택.click()
            } else {
                throw UiObjectNotFoundException("‘혜택’ 텍스트가 포함된 요소를 찾을 수 없습니다.")
            }

            // "페이" 텍스트가 포함된 요소 클릭
            val 토스페이 = device.findObject(UiSelector().text("페이"))
            if (토스페이.exists()) {
                토스페이.click()
            } else {
                throw UiObjectNotFoundException("‘토스페이’ 텍스트가 포함된 요소를 찾을 수 없습니다.")
            }

            // 스와이프 작업 예제
            device.swipe(500, 1500, 500, 500, 10)

        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }
}
