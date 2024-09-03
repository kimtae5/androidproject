package com.test.tclick

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class Click(private val func: Func) {

    suspend fun execute() {
        withContext(Dispatchers.IO) {
            try {
                // '혜택'이라는 텍스트를 가진 요소 찾기
                func.mainBack("혜택", false.toString())
                delay(500)
                func.findAndClick("혜택", false, 500, 2, if (false) 1 else 0)

                if (func.findAndClick("친구와 함께", true, 5000, 1, if (false) 1 else 0)) {
                    func.waitElement(true, "보너스")
                    repeat(10) {
                        if (func.findAndClick("1원 더", false, 3000, 1, if (false) 1 else 0)) {
                            func.backKey(1)
                            delay(2000)
                        } else {
                            return@withContext
                        }
                    }
                    if (func.findAndClick("보너스", true, 3000, 1, if (false) 1 else 0)) {
                        func.backKey(1)
                    }
                    func.backKey(1)
                }

                // '방송 중'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("방송 중", false, 500, 1, if (false) 1 else 0)) {
                    func.waitElement(true, "포인트")
                    Log.d("MainTask", "thumbnails.count 개의 thumbnail 찾음")
                    repeat(10) {
                        // 썸네일 탐색 및 클릭 로직 추가 가능
                    }
                }

                // '행운복권'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("행운복권", true, 3500, 1, if (false) 1 else 0)) {
                    func.findAndClick("행운복권", true, 2000, 1, if (false) 1 else 0)
                    func.mainBack("혜택", false.toString())
                }

                func.scrollMove("up", 2, 0)

                // '이번주 미션'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("미션", true, 1500, 1, if (false) 1 else 0)) {
                    handleWeeklyMission()
                }

                func.scrollMove("up", 2, 0)

                // '토스쇼핑'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("토스쇼핑", true, 1500, 1, if (false) 1 else 0)) {
                    if (func.findAndClick("받기", true, 500, 1, if (false) 1 else 0)) {
                        func.scrollMove("up", 26, 0)
                    }
                    func.mainBack("혜택", false.toString())
                }

                func.scrollMove("up", 2, 0)

                // '만보기'라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("만보기", true, 500, 1, 0)) {
                    handleStepCounter()
                }

                // '일주일 방문 미션'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("일주일 방문 미션", false, 1000, 1, if (false) 1 else 0)) {
                    handleWeeklyVisitMission()
                }

                func.scrollMove("up", 2, 0)

                // '게시물'이라는 텍스트를 가진 요소 찾기
                if (func.findAndClick("게시물", true, 2000, 1, if (false) 1 else 0)) {
                    handlePostMission()
                }

                func.scrollMove("up", 2, 0)

            } catch (e: Exception) {
                Log.e("ClickTask", "Error occurred: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun handleWeeklyMission() {
        var receivedMissionFound = false
        repeat(10) {
            if (func.findAndClick("페이지", true, 500, 1, if (false) 1 else 0)) {
                if (func.findElementByText("구경가기") == null) {
                    func.findAndClick("10원 받기", false, 1000, 1, if (false) 1 else 0)
                }
                func.mainBack("페이지", false.toString())
            }
            func.scrollMove("down", 3, 0)

            if (func.findElementByText("받은 미션") != null) {
                receivedMissionFound = true
                return
            }
        }
        if (receivedMissionFound) {
            func.mainBack("혜택", false.toString())
        }
    }

    private suspend fun handleStepCounter() {
        func.waitElement(true, "선물")
        delay(2000)
        repeat(5) {
            if (func.findElementByText("선물 상자를 눌러 선물을 받으세요") != null) {
                func.findAndClick("선물 상자를 눌러 선물을 받으세요", true, 1000, 1, if (false) 1 else 0)
            } else {
                return
            }
        }
        func.mainBack("혜택", false.toString())
    }

    private suspend fun handleWeeklyVisitMission() {
        var cnt = 0
        repeat(10) {
            if (func.findAndClick("포인트 받기", true, 1000, 1, if (false) 1 else 0)) {
                if (func.findElementByText("상품을 눌러서 구경하면") != null) {
                    delay(4000)
                }
                func.findAndClick("동의하고 시작하기", false, 500, 1, if (false) 1 else 0)
                val place = handleMissionLocation()
                if (place) {
                    handleLocationMission()
                } else {
                    handleKeywordMission()
                }
            } else {
                cnt++
                Log.d("MainTask", "$cnt 번 포인트받기 없음")
                if (cnt > 2) {
                    return
                }
                func.scrollMove("down", 2, 0)
            }
        }
        func.mainBack("혜택", false.toString())
    }

    private fun handleMissionLocation(): Boolean {
        val place = func.findElementByText("장") != null
        if (place) {
            Log.d("MainTask", "장소미션")
        }
        return place
    }

    private suspend fun handleLocationMission() {
        repeat(2) {
            if (!func.findAndClick("저장", false, 500, 1, if (false) 1 else 0)) {
                func.scrollMove("down", 2, 0)
            } else {
                return
            }
        }
        func.findAndClick("자동 로그인", true, 2000, 1, if (false) 1 else 0)
        func.findAndClick("예", false, 4000, 1, if (false) 1 else 0)
        func.findAndClick("동의", false, 2000, 1, if (false) 1 else 0)
        func.findAndClick("지금 바로 시작하기", true, 1000, 1, if (false) 1 else 0)
        delay(5000)
        func.mainBack("혜택", false.toString())
    }

    private suspend fun handleKeywordMission() {
        repeat(2) {
            if (!func.findAndClick("복사", true, 2000, 1, if (false) 1 else 0)) {
                func.scrollMove("down", 2, 0)
            } else {
                return
            }
        }
        func.backKey(4)
        delay(1000)

    }

    private suspend fun handlePostMission() {
        if (func.findElementByText("적립") != null) {
            repeat(20) {
                func.scrollMove("down", 10, 0)
                if (func.findElementByText("적립") != null) {
                    func.scrollMove("down", 5, 0)
                } else {
                    func.mainBack("혜택", false.toString())
                    return
                }
            }
        } else {
            func.mainBack("혜택", false.toString())
        }
    }
}
