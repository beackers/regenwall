package com.beackers.regenwall;

import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // check no crash reports
        File crashDir = getFilesDir();
        File[] exceptions = crashDir.listFiles((dir, name) -> name.startsWith("exc_"));
        if (exceptions.length > 0 && BuildInfo.DEBUG) {
            Toast.makeText(this, "exceptions detected - " + exceptions.length, Toast.LENGTH_LONG).show();
        }
        
        setContentView(R.layout.activity_main);
        Button flowFieldButton = findViewById(R.id.flowFieldButton);
        Button viewCrashes = findViewById(R.id.viewCrashes);
        flowFieldButton.setOnClickListener(v -> openFlowFieldView());
        viewCrashes.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrashReportActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        if (BuildInfo.DEBUG) viewCrashes.setVisibility(View.VISIBLE);
    }
}
