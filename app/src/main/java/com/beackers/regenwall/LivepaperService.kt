package com.beackers.regenwall.livepaper

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class LivepaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return LivepaperEngine()
    }

    inner class LivepaperEngine : Engine() {
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            // drawing setup here
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
        }
    }
}
