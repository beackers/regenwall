package com.beackers.regenwall.crashcar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beackers.regenwall.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrashReportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_report_main);

        RecyclerView recycler = findViewById(R.id.crashList);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        File dir = getFilesDir();

        File[] found = dir.listFiles((d, name) -> name.startsWith("exc_"));

        List<File> files = new ArrayList<>();

        if (found != null) {
            files.addAll(Arrays.asList(found));
        }

        CrashAdapter adapter = new CrashAdapter(files, this::openFile);
        recycler.setAdapter(adapter);
    }

    private void openFile(File file) {
        Intent i = new Intent(this, CrashViewerActivity.class);
        i.putExtra("file", file.getAbsolutePath());
        startActivity(i);
    }
}
