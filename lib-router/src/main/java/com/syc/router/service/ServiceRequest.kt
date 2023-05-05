package com.syc.router.service

import android.content.res.Resources.NotFoundException
import com.syc.router.annotations.ServiceMethod

class ServiceRequest private constructor(
    private val serviceInstance: Any,
    private val methodName: String,
    private val params: Map<String, Any>? = null,
) {
    class Call(private val request: ServiceRequest) {
        fun <T> execute(): T? =  with(request) {
            val method = serviceInstance::class.java.methods.find {
                it.getAnnotation(ServiceMethod::class.java)?.name == methodName
            } ?: throw NotFoundException("方法没有找到：$methodName")
            return method.run {
                if (parameterTypes.isEmpty()) {
                    invoke(serviceInstance) as? T
                } else {
                    invoke(serviceInstance, params) as? T
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


