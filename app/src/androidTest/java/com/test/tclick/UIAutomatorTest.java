package com.test.tclick;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UIAutomatorTest {
    private static final String TARGET_APP_PACKAGE = "viva.republica.toss";  // 목표 앱의 패키지 이름
    private static final int LAUNCH_TIMEOUT = 5000;  // 대기 시간을 충분히 늘림
    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());
        //device.pressHome();

        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage("viva.republica.toss");

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

            // 로그 추가
            System.out.println("앱을 시작했습니다. 대기 중...");

            boolean appLaunched = device.wait(Until.hasObject(By.pkg(TARGET_APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
            if (appLaunched) {
                System.out.println("앱이 성공적으로 실행되었습니다.");
            } else {
                System.out.println("앱 실행 대기 시간 초과.");
            }
        } else {
            System.out.println("앱을 실행할 수 없습니다. 인텐트가 null입니다.");
        }
    }


    @Test
    public void testButtonClick() {
        // 충분한 대기 시간을 추가
        device.wait(Until.hasObject(By.text("토스페이")), LAUNCH_TIMEOUT);

        // UiObject2 사용 예제
        UiObject2 button = device.findObject(By.text("토스페이"));
        if (button != null) {
            System.out.println("토스페이 버튼을 찾았습니다.");
            button.click();
        } else {
            System.out.println("토스페이 버튼을 찾지 못했습니다.");
        }
    }
}
