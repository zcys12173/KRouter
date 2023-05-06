package com.syc.krouter

import android.app.Application
import android.util.Log
import com.syc.router.KRouter
import com.syc.router.navigator.interceptor.Interceptor

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        KRouter.install()
        KRouter.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain) {
                Log.e("interceptor", chain.request.toString())
                chain.process(chain.request)
            }
        })
    }
}