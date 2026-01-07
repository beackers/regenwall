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

import java.io.File;

public class RegenwallApp extends Application {
    
    private static final String DATASTORE_NAME = "flow_field_config.pb";

    private DataStore<FlowFieldConfigProto> flowFieldConfigStore;


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
            Log.d("Regenwall", "Debug mode ON");
        }

        flowFieldConfigStore = DataStoreFactory.INSTANCE.create(
                FlowFieldConfigSerializer.INSTANCE,
                () -> new File(getFilesDir(), DATASTORE_NAME)
                );
    }

    public DataStore<FlowFieldConfigProto> getFlowFieldConfigStore() {
        return flowFieldConfigStore;
    }

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
