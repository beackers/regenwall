package com.beackers.regenwall;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import androidx.datastore.core.DataStore;
import androidx.datastore.core.DataStoreFactory;
import com.beackers.regenwall.datastore.FlowFieldConfigProto;
import com.beackers.regenwall.datastore.FlowFieldConfigSerializer;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.beackers.regenwall.livepaper.LivepaperService;
import com.beackers.regenwall.crashcar.CrashReportActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread;

public class RegenwallApp extends Application {
    
    private static final String DATASTORE_NAME = "flow_field_config.pb";

    private DataStore<FlowFieldConfigProto> flowFieldConfigStore;


    @Override
    public void onCreate() {
        super.onCreate();

        // set crash reporter
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                File dir = getFilesDir();
                if (dir != null) {
                    File file = File(dir, "last_crash.txt");
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(file);
                        writer.write(Log.getStackTraceString(throwable));
                    } finally {
                        if (writer != null) writer.close();
                    }
                }
                Intent crashIntent = new Intent(this, CrashReportActivity.class);
                crashIntent.putExtra("crash", Log.getStackTraceString(throwable));
                crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(crashIntent);
            } catch (Exception e) {
                    // gotta catch em all
                    Log.e("Regenwall", "Crash handler failed", e);
                }
        });

        // check no crash reports
        File crashFile = new File(getFilesDir(), "last_crash.txt");
        if (crashFile.exists() && BuildInfo.DEBUG) {
            Intent intent = new Intent(this, CrashReportActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // later might add something for reporting the crash via GitHub Issues? Not sure how that would work.
        }
        
        // set thread policy
        if (BuildInfo.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            Log.d("Regenwall", "Debug mode ON");
        }

        // datastore
        flowFieldConfigStore = DataStoreFactory.INSTANCE.create(
                FlowFieldConfigSerializer.INSTANCE,
                () -> new File(getFilesDir(), DATASTORE_NAME)
                );
    }

    public DataStore<FlowFieldConfigProto> getFlowFieldConfigStore() {
        return flowFieldConfigStore;
    }

    // live wallpaper
    public void setLivepaper(Context context) {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, LivepaperService.class)
                );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
