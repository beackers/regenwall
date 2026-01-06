package com.beackers.regenwall.livepaper

class RegenwallWallpaperService : WallpaperService() {
    override fun onCreateEngine() {
        return RegenEngine()
    }

    inner class RegenEngine : Engine() {
    }
}
