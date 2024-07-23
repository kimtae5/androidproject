package com.test.tclick;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UIAutomatorTest {
    private static final String TARGET_APP_PACKAGE = "com.example.otherapp";  // 목표 앱의 패키지 이름
    private static final int LAUNCH_TIMEOUT = 5000;

    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        // UiDevice 인스턴스 가져오기
        device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());

        // 홈 화면으로 이동
        device.pressHome();

        // 목표 앱의 인텐트를 생성하고 실행
        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(TARGET_APP_PACKAGE);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  // 기존의 Task를 지우고 새로운 Task로 시작
            context.startActivity(intent);

            // 목표 앱이 나타날 때까지 기다리기
            device.wait(Until.hasObject(By.pkg(TARGET_APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        }
    }

    @Test
    public void testButtonClick() throws UiObjectNotFoundException {
        // 버튼을 찾고 클릭하는 예제 (텍스트로 찾기)
        device.findObject(new UiSelector().text("Button Text")).click();
    }
}
