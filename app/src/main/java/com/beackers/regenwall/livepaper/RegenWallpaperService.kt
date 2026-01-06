package com.beackers.regenwall.livepaper

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class RegenWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return RegenEngine()
    }

    inner class RegenEngine : Engine() {
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            // drawing setup here
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
        }
    }
}
