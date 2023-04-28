package com.syc.krouter

import android.app.Application
import com.syc.router.KRouter

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        KRouter.install()
    }
}