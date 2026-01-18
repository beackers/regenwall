package com.beackers.regenwall.crashcar;

import java.io.File;
import android.util.Log;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.file.Files;

public class CrashReportActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "started CrashReportActivity", Toast.LENGTH_LONG).show();

        TextView tv = new TextView(this);
        tv.setTextSize(12);
        tv.setPadding(16, 16, 16, 16);
        tv.setText(readCrash());

        setContentView(tv);
    }

    private String readCrash() {
        Toast.makeText(this, "reading crash file", Toast.LENGTH_LONG).show();
        try {
            File file = new File(getFilesDir(), "last_crash.txt");
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            return "Could not read crash report.";
        }
    }
}
