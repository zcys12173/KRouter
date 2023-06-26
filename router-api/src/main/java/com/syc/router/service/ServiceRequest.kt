package com.syc.router.service

import android.content.res.Resources.NotFoundException
import com.syc.router.annotations.ServiceMethod
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters

class ServiceRequest private constructor(
    private val serviceInstance: Any,
    private val methodName: String,
    private val params: Map<String, Any>? = null,
    private val callback: ResultCallback? = null
) {


    class Call(private val request: ServiceRequest) {

        /**
         * MethodName获取目标方法
         */
        private fun getTargetKFunction():KFunction<*> = with(request){
            serviceInstance.javaClass.kotlin.functions.find { function ->
                function.annotations.any { it is ServiceMethod && it.name == methodName }
            } ?: throw NotFoundException("方法没有找到：$methodName")
        }
        fun call(): Any? = with(request) {
            val kFunction = getTargetKFunction()
            if(kFunction.isSuspend){
                throw RuntimeException("${methodName}方法是suspend方法,请使用callSuspend方法调用")
            }else{
                return if (kFunction.valueParameters.isEmpty()) {
                    kFunction.call(serviceInstance)
                } else {
                    kFunction.call(serviceInstance, params)
                }
            }
        }
        fun callAsync() {
            with(request) {
                val kFunction = getTargetKFunction()
                if (kFunction.isSuspend) {
                    throw RuntimeException("${methodName}方法是suspend方法,请使用callSuspend方法调用")
                } else {
                    val functionValueParameters = kFunction.valueParameters
                    when(functionValueParameters.size){
                        0->kFunction.call(serviceInstance)
                        1->{
                            val params:Any? = if(functionValueParameters[0].type.classifier == ResultCallback::class){
                                callback
                            }else{
                                params
                            }
                            kFunction.call(serviceInstance,params)
                        }
                        2->{
                            if(functionValueParameters[0].type.classifier == ResultCallback::class){
                                kFunction.call(serviceInstance,callback,params)
                            }else{
                                kFunction.call(serviceInstance,params,callback)
                            }
                        }
                        else->{
                            throw RuntimeException("方法参数不正确")
                        }
                    }
                }
            }
        }

        suspend fun callSuspend(): Any? = with(request) {
            val kFunction = getTargetKFunction()
            if (kFunction.isSuspend) {
                return if (kFunction.valueParameters.isEmpty()) {
                    kFunction.callSuspend(serviceInstance)
                } else {
                    kFunction.callSuspend(serviceInstance, params)
                }
            } else {
                return if (kFunction.valueParameters.isEmpty()) {
                    kFunction.call(serviceInstance)
                } else {
                    kFunction.call(serviceInstance, params)
                }
            }

        }
    }


    class Builder(private val serviceInstance: Any, private val methodName: String) {
        private var params: Map<String, Any>? = null
        private var callback: ResultCallback? = null
        fun params(params: Map<String, Any>?) = this.apply { this.params = params }
        fun withCallback(callback: ResultCallback) = this.apply { this.callback = callback }
        fun build(): ServiceRequest {
            return ServiceRequest(serviceInstance, methodName, params,callback)
        }
    }
}


