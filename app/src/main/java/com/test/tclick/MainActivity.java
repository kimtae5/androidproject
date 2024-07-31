package com.test.tclick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonToss = findViewById(R.id.buttonToss);
        buttonToss.setOnClickListener(view -> {
            // Start Toss app
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
            if (launchIntent != null) {
                startActivity(launchIntent);

                // Start TestTriggerService
                Intent serviceIntent = new Intent(MainActivity.this, TestTriggerService.class);
                startService(serviceIntent);
            }
        });
    }
}
