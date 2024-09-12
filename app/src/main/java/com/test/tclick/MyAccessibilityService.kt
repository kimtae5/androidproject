package com.test.tclick

import android.R
import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // Notification channel 생성
    }

    private fun scheduleServiceShutdown() {
        Handler(Looper.getMainLooper()).postDelayed({
            disableSelf()
        }, 5000) // 5초 후 서비스 종료
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Event Type: " + event.eventType)
        Log.d(TAG, "Package Name: " + event.packageName)

        // 특정 이벤트 타입을 감지
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val rootNode = rootInActiveWindow ?: return
            scheduleServiceShutdown()

            if (event.packageName == "viva.republica.toss") {
                // Toss 앱 실행 후 Click 클래스의 메서드 실행
                val func = Func(this) // Func 클래스 인스턴스를 생성 (가정)
                val click = Click(func)

//                 코루틴 내부에서 suspend 함수 호출
//                serviceScope.launch {
//                    click.execute(rootNode) // 여기서 suspend 함수 호출
//                }

//                for (i in 1..3) {
                func.mainBack(this, rootInActiveWindow, targetText = "혜택")
                func.findTextElements(rootNode)
//                    mainBack(targetText="혜택",maxAttempts=10)

//                }

            }

            // 모든 작업이 완료되면 서비스 종료
            stopForeground(true)
            disableSelf()
        }
    }

    private fun logAllNodes(node: AccessibilityNodeInfo?, level: Int) {
        if (node == null) {
            return
        }

        // 로그 출력
        val indent = " ".repeat(level * 2) // 들여쓰기
        val className = if (node.className != null) node.className.toString() else "null"
        val text = if (node.text != null) node.text.toString() else "null"
        val contentDescription =
            if (node.contentDescription != null) node.contentDescription.toString() else "null"

        Log.d(TAG, indent + "ClassName: " + className)
        if (node.text != null) {
            Log.d(TAG, indent + "Text: " + text)
        }
        if (node.contentDescription != null) {
            Log.d(TAG, indent + "ContentDescription: " + contentDescription)
        }

        // 자식 노드 재귀 호출
        for (i in 0 until node.childCount) {
            logAllNodes(node.getChild(i), level + 1)
        }
    }

    override fun onInterrupt() {
        // 필요 시 인터럽트 처리
        stopForeground(true)
        disableSelf()
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "Service Unbound")
        return super.onUnbind(intent)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service Connected")

        // Foreground 서비스로 설정
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")

        // Foreground 서비스로 설정
        startForeground(NOTIFICATION_ID, createNotification())

        if (intent != null && "com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY" == intent.action) {
            Log.d(TAG, "Triggering Toss App Launch")
            launchTossApp()
        } else {
            // 인텐트가 없으면 서비스 종료
            stopForeground(true)
            stopSelf()
        }
        return START_NOT_STICKY // 서비스가 종료된 후 다시 시작하지 않도록 설정
    }

    private fun launchTossApp() {
        val launchIntent = packageManager.getLaunchIntentForPackage("viva.republica.toss")
        Log.d(TAG, "Launch Intent: " + (launchIntent != null))
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)

            // 앱 실행 후 특정 시간 대기
            Handler().postDelayed({
                // 작업이 완료되면 서비스 종료
                stopForeground(true)
                stopSelf()
            }, 3000) // 예를 들어 3초 후
        } else {
            Log.d(TAG, "Toss 앱을 찾을 수 없습니다.")
            Toast.makeText(this, "Toss 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Accessibility Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("서비스 실행 중")
            .setContentText("MyAccessibilityService가 실행 중입니다.")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .build()
    }

    private fun startForegroundServiceWithType() {
        val notification = createNotification()

        // Android Q(API 레벨 29) 이상에서는 FOREGROUND_SERVICE_TYPE_MANIFEST를 지정합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf() // 서비스가 종료되도록 확실히 처리
        Log.d(TAG, "Service Destroyed")
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
        private const val CHANNEL_ID = "my_accessibility_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}
