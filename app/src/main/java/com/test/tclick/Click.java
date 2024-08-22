package com.test.tclick;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class Click {

    private Func func;

    public Click(Func func) {
        this.func = func;
    }

    public void execute() {
        new ClickTask().execute();
    }

    private class ClickTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // '혜택'이라는 텍스트를 가진 요소 찾기
            func.mainBack("혜택", "", false);
            SystemClock.sleep(500);
            func.findAndClick("혜택", false, 500, 2, false);

            if (func.findAndClick("친구와 함께", true, 5000, 1, false)) {
                func.waitElement(true, "보너스");
                for (int i = 0; i < 10; i++) {
                    if (func.findAndClick("1원 더", false, 3000, 1, false)) {
                        func.backKey(1);
                        SystemClock.sleep(2000);
                    } else {
                        break;
                    }
                }
                if (func.findAndClick("보너스", true, 3000, 1, false)) {
                    func.backKey(1);
                }
                func.backKey(1);
            }

            // '방송 중'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("방송 중", false, 500, 1, false)) {
                func.waitElement(true, "포인트");
                Log.d("MainTask", "thumbnails.count 개의 thumbnail 찾음");
                for (int i = 0; i < 10; i++) {
                    // 썸네일 탐색 및 클릭 로직 추가 가능
                }
            }

            // '행운복권'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("행운복권", true, 3500, 1, false)) {
                func.clickElement2("행운복권", 2000, 1, "행운복권");
                func.mainBack("혜택", "", false);
            }

            func.scrollMove("up", 2, 0);

            // '이번주 미션'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("미션", true, 1500, 1, false)) {
                handleWeeklyMission();
            }

            func.scrollMove("up", 2, 0);

            // '토스쇼핑'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("토스쇼핑", true, 1500, 1, false)) {
                if (func.findAndClick("받기", true, 500, 1, false)) {
                    func.scrollMove("up", 26, 0);
                }
                func.mainBack("혜택", "", false);
            }

            func.scrollMove("up", 2, 0);

            // '만보기'라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("만보기", true, 500, 1, false)) {
                handleStepCounter();
            }

            // '일주일 방문 미션'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("일주일 방문 미션", false, 1000, 1, false)) {
                handleWeeklyVisitMission();
            }

            func.scrollMove("up", 2, 0);

            // '게시물'이라는 텍스트를 가진 요소 찾기
            if (func.findAndClick("게시물", true, 2000, 1, false)) {
                handlePostMission();
            }

            func.scrollMove("up", 2, 0);

            return null;
        }

        private void handleWeeklyMission() {
            boolean receivedMissionFound = false;
            for (int i = 0; i < 10; i++) {
                if (func.findAndClick("페이지", true, 500, 1, false)) {
                    if (func.findElementByText("구경가기") == null) {
                        func.findAndClick("10원 받기", false, 1000, 1, false);
                    }
                    func.mainBack("페이지", "확인", true);
                }
                func.scrollMove("down", 3, 0);

                if (func.findElementByText("받은 미션") != null) {
                    receivedMissionFound = true;
                    break;
                }
            }
            if (receivedMissionFound) {
                func.mainBack("혜택", "", false);
            }
        }

        private void handleStepCounter() {
            func.waitElement(true, "선물");
            SystemClock.sleep(2000);
            for (int i = 0; i < 5; i++) {
                if (func.findElementByText("선물 상자를 눌러 선물을 받으세요") != null) {
                    func.findAndClick("선물 상자를 눌러 선물을 받으세요", true, 1000, 1, false);
                } else {
                    break;
                }
            }
            func.mainBack("혜택", "", false);
        }

        private void handleWeeklyVisitMission() {
            int cnt = 0;
            for (int i = 0; i < 10; i++) {
                if (func.findAndClick("포인트 받기", true, 1000, 1, false)) {
                    if (func.findElementByText("상품을 눌러서 구경하면") != null) {
                        SystemClock.sleep(4000);
                    }
                    func.findAndClick("동의하고 시작하기", false, 500, 1, false);
                    boolean place = handleMissionLocation();
                    if (place) {
                        handleLocationMission();
                    } else {
                        handleKeywordMission();
                    }
                } else {
                    cnt++;
                    Log.d("MainTask", cnt + "번 포인트받기 없음");
                    if (cnt > 2) {
                        break;
                    }
                    func.scrollMove("down", 2, 0);
                }
            }
            func.mainBack("혜택", "", false);
        }

        private boolean handleMissionLocation() {
            boolean place = false;
            if (func.findElementByText("장") != null) {
                place = true;
                Log.d("MainTask", "장소미션");
            }
            return place;
        }

        private void handleLocationMission() {
            for (int j = 0; j < 2; j++) {
                if (!func.findAndClick("저장", false, 500, 1, false)) {
                    func.scrollMove("down", 2, 0);
                } else {
                    break;
                }
            }
            func.findAndClick("자동 로그인", true, 2000, 1, false);
            func.findAndClick("예", false, 4000, 1, false);
            func.findAndClick("내 장소", true, 1000, 1, false);
            func.findAndClick("저장", false, 3000, 1, false);
            func.mainBack("미션 완료", "", false);
        }

        private void handleKeywordMission() {
            for (int j = 0; j < 2; j++) {
                if (!func.findAndClick("복사", true, 2000, 1, false)) {
                    func.scrollMove("down", 2, 0);
                } else {
                    break;
                }
            }
            func.backKey(4);
            SystemClock.sleep(1000);
            func.deviceClick((int) (func.getWindowSize()[0] * 0.61), (int) (func.getWindowSize()[1] * 0.62));
            AccessibilityNodeInfo rootNode = func.service.getRootInActiveWindow();
            if (rootNode != null) {
                List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId("your_text_field_view_id");
                if (nodes != null && !nodes.isEmpty()) {
                    AccessibilityNodeInfo textField = nodes.get(0);
                    func.deleteTextExceptLast4(textField);
                }
            }
            func.deviceClick(func.getWindowSize()[0] / 2, (int) (func.getWindowSize()[1] * 0.9));
        }

        private void handlePostMission() {
            if (func.findElementByText("적립") != null) {
                for (int i = 0; i < 20; i++) {
                    func.scrollMove("down", 10, 0);
                    if (func.findElementByText("적립") != null) {
                        func.scrollMove("down", 5, 0);
                    } else {
                        func.mainBack("혜택", "", false);
                        break;
                    }
                }
            } else {
                func.mainBack("혜택", "", false);
            }
        }
    }
}
