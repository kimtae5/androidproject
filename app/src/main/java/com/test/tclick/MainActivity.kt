package com.test.tclick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.provider.Settings
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.launch_app_button).setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        findViewById<View>(R.id.launch_other_app_button).setOnClickListener {
            val service = MyAccessibilityService()
            service.launchApp("viva.republica.toss") // 실행할 다른 앱의 패키지 이름
        }
    }
}
