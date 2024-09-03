package com.test.tclick

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var tossButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 멤버 변수 tossButton에 버튼 인스턴스를 할당
        tossButton = findViewById(R.id.buttonToss)
        tossButton?.setOnClickListener(View.OnClickListener { v: View? -> triggerAccessibilityService() })
    }

    override fun onResume() {
        super.onResume()

        // 접근성 서비스가 활성화되어 있는지 확인
        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
            // 접근성 서비스 활성화 안내 다이얼로그 표시
            AlertDialog.Builder(this)
                .setTitle("접근성 서비스 필요")
                .setMessage("이 기능을 사용하려면 접근성 서비스를 활성화해야 합니다.")
                .setPositiveButton("설정으로 이동") { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("취소", null)
                .show()
            tossButton?.isEnabled = false
        } else {
            tossButton?.isEnabled = true
        }
    }

    // 접근성 서비스에 작업을 트리거하는 메서드
    private fun triggerAccessibilityService() {
        val intent = Intent(this, MyAccessibilityService::class.java)
        intent.setAction("com.test.tclick.ACTION_TRIGGER_ACCESSIBILITY")
        Log.d("MainActivity", "Starting service: " + intent.action)
        startForegroundService(intent)
    }

    // 접근성 서비스 활성화 여부 확인
    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (enabledServices != null) {
            val componentNames =
                enabledServices.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (componentName in componentNames) {
                val enabledService = ComponentName.unflattenFromString(componentName)
                if (enabledService != null && enabledService == expectedComponentName) {
                    return true
                }
            }
        }
        return false
    }
}
