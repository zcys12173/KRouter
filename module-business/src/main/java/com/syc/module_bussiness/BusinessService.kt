package com.syc.module_bussiness

import com.syc.router.annotations.RouterService
import com.syc.router.annotations.ServiceMethod

@RouterService("BusinessService")
class BusinessService {

    @ServiceMethod("getBusinessName")
    fun getBusinessName(params:Map<String,Any>) = "BusinessService+${params["arg"]}"
}