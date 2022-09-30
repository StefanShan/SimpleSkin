package com.stefan.simpleskin

import android.app.Application
import com.stefan.simple_skin.SimpleSkin

class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SimpleSkin.init(this)
    }
}