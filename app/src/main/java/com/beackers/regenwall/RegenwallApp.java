package com.beackers.regenwall;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

public class RegenwallApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        if (BuildInfo.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            Log.d("Regenwall", "Debug mode ON")
        }
    }
}
