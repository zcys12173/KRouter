package com.syc.krouter.service

import android.util.Log
import com.syc.router.annotations.RouterService
import com.syc.router.annotations.ServiceMethod

@RouterService("AppService")
class AppService {

    @ServiceMethod("getAppName")
    fun getAppName(): String {
        Log.e("sss","KRouterAppName")
        return "KRouterAppName"
    }

    @ServiceMethod("test")
    fun test(params:Map<String,Any>): String {
        Log.e("sss","$params")
        return "KRouterAppName"
    }
}