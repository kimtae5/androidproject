package com.test.tclick;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UiAutomatorTest {

    private UiDevice device;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testLaunchTossAndClickBenefit() throws UiObjectNotFoundException, InterruptedException {
        // 홈 화면으로 돌아가기
        device.pressHome();

        // Toss 앱 실행
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear out any previous task
            context.startActivity(intent);

            // Toss 앱이 시작될 때까지 대기
            device.waitForIdle();

            // 5초 대기 (필요시 조정)
            Thread.sleep(2000);

            // "혜택" 버튼을 찾고 클릭
            UiObject 혜택 = device.findObject(new UiSelector().text("혜택"));
            UiObject 토스페이 = device.findObject(new UiSelector().text("토스페이"));
            UiObject 전체 = device.findObject(new UiSelector().text("전체"));
            if (혜택.exists() && 토스페이.exists() && 전체.exists()) {
                혜택.click();
                토스페이.click();
                전체.click();
            } else {
                throw new UiObjectNotFoundException("‘혜택’ 텍스트가 포함된 요소를 찾을 수 없습니다.");
            }
        } else {
            throw new UiObjectNotFoundException("Toss 앱을 찾을 수 없습니다.");
        }
    }
}
