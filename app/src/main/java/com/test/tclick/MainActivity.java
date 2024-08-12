package com.test.tclick;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Activity created.");

        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            Log.d(TAG, "onCreate: Accessibility Service not enabled.");
            Toast.makeText(this, "Please enable the Accessibility Service.", Toast.LENGTH_LONG).show();
            startAccessibilitySettings();
        } else {
            Log.d(TAG, "onCreate: Accessibility Service already enabled.");
            checkAndRequestPermissions();
        }
    }

    private boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        String colonSplitter = ":";
        String colonSplitterValue = colonSplitter + service.getName() + colonSplitter;

        String enabledServices = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        boolean isEnabled = enabledServices.contains(colonSplitterValue);
        Log.d(TAG, "isAccessibilityServiceEnabled: " + isEnabled);
        return isEnabled;
    }

    private void startAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Log.d(TAG, "startAccessibilitySettings: Accessibility settings opened.");
    }

    private void launchTossApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("viva.republica.toss");
        if (launchIntent != null) {
            startActivity(launchIntent);
            Log.d(TAG, "launchTossApp: Toss app launched.");
        } else {
            Log.e(TAG, "launchTossApp: Toss app not found.");
            Toast.makeText(this, "Toss app not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            Log.d(TAG, "checkAndRequestPermissions: Permission requested.");
        } else {
            Log.d(TAG, "checkAndRequestPermissions: Permission already granted.");
            launchTossApp(); // 권한이 이미 허용된 경우 앱을 실행합니다.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Permission granted.");
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                launchTossApp(); // 권한이 승인되었으므로 앱을 실행합니다.
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Permission denied.");
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            checkAndRequestPermissions();
        } else {
            Toast.makeText(this, "Please enable the Accessibility Service.", Toast.LENGTH_LONG).show();
            startAccessibilitySettings();
        }
    }
}
