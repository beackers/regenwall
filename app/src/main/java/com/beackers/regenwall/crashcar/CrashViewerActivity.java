package com.beackers.regenwall.crashcar;

import com.beackers.regenwall.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CrashViewerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_viewer);

        String path = getIntent().getStringExtra("file");
        File file = new File(path);

        String content;
        TextView tv = findViewById(R.id.crashText);
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
            tv.setText(content);
        } catch (IOException e) {
            Toast.makeText(this, "IOException occured", Toast.LENGTH_SHORT).show();
        }


        findViewById(R.id.copyButton).setOnClickListener(v -> copy(tv));
        findViewById(R.id.deleteButton).setOnClickListener(v -> delete(file));
    }

    private void copy(TextView tv) {
        ClipboardManager clipboard =
            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        
        ClipData clip =
            ClipData.newPlainText("stack trace", tv.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT);
    }

    private void delete(File file) {
        file.delete();
        finish();
    }
}
