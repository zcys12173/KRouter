package com.syc.router.service

import android.content.res.Resources.NotFoundException
import com.syc.router.annotations.ServiceMethod
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions

class ServiceRequest private constructor(
    private val serviceInstance: Any,
    private val methodName: String,
    private val params: Map<String, Any>? = null,
) {


    class Call(private val request: ServiceRequest){
        fun call(): Any? = with(request) {
            val kFunction = serviceInstance.javaClass.kotlin.functions.find { function ->
                function.annotations.any { it is ServiceMethod && it.name == methodName }
            } ?: throw NotFoundException("方法没有找到：$methodName")
            if(kFunction.isSuspend){
                throw RuntimeException("${methodName}方法是suspend方法,请使用callSuspend方法调用")
            }else{
                return if (kFunction.parameters.isEmpty()) {
                    kFunction.call(serviceInstance)
                } else {
                    kFunction.call(serviceInstance, params)
                }
            }
        }

        suspend fun callSuspend(): Any? = with(request) {
            val kFunction = serviceInstance.javaClass.kotlin.functions.find { function ->
                function.annotations.any { it is ServiceMethod && it.name == methodName }
            } ?: throw NotFoundException("方法没有找到：$methodName")
            if(kFunction.isSuspend){
                return if (kFunction.parameters.isEmpty()) {
                    kFunction.callSuspend(serviceInstance)
                } else {
                    kFunction.callSuspend(serviceInstance, params)
                }
            }else{
                return if (kFunction.parameters.isEmpty()) {
                    kFunction.call(serviceInstance)
                } else {
                    kFunction.call(serviceInstance, params)
                }
            }

        }
    }


    class Builder(private val serviceInstance: Any, private val methodName: String) {
        private var params: Map<String, Any>? = null

        fun params(params: Map<String, Any>?) = this.apply { this.params = params }

        fun build(): ServiceRequest {
            return ServiceRequest(serviceInstance, methodName, params)
        }
    }
}


