package com.syc.module_bussiness

import com.syc.router.annotations.RouterService
import com.syc.router.annotations.ServiceMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@RouterService("BusinessService")
class BusinessService {

    @ServiceMethod("getBusinessName")
    suspend fun getBusinessName(params:Map<String,Any>):String{
        return withContext(Dispatchers.IO){
            delay(3000)
            "BusinessService"
        }
    }
}