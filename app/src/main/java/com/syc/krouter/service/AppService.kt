package com.syc.krouter.service

import android.util.Log
import com.syc.router.annotations.RouterService
import com.syc.router.annotations.ServiceMethod
import com.syc.router.service.ResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@RouterService("AppService")
class AppService {


    @ServiceMethod("getAppNameWithoutParams")
    fun getAppName(): String {
        Log.e("sss","run getAppNameWithoutParams")
        return "KRouterAppName"
    }

    @ServiceMethod("getAppName")
    fun getAppName(params:Map<String,Any>): String {
        Log.e("sss","run getAppName，$params")
        return "KRouterAppName"
    }

    @ServiceMethod("asyncTest")
    fun asyncTest(params:Map<String,Any>,callback: ResultCallback) {
        Log.e("sss","$params")
        callback.success("KRouterAppName")
    }

    @ServiceMethod("asyncTestWithoutParams")
    fun asyncTest(callback: ResultCallback) {
        callback.success("KRouterAppName")
    }

    @ServiceMethod("suspendTest")
    suspend fun suspendTest(params:Map<String,Any>?):String{
        Log.e("sss",params.toString())
        return withContext(Dispatchers.IO){
            delay(3000)
            "BusinessService"
        }
    }

    @ServiceMethod("suspendTestWithoutParams")
    suspend fun suspendTest():String{
        return withContext(Dispatchers.IO){
            delay(3000)
            "BusinessService"
        }
    }


}