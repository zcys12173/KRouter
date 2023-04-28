package com.syc.router

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.syc.router.log.log
import com.syc.router.navigator.NavigatorRequest
import com.syc.router.navigator.interceptor.Interceptor
import com.syc.router.service.ServiceRequest

object KRouter {
    private val pageInterceptors: MutableList<Interceptor> = mutableListOf()

    /**
     * 页面map {key:Path,value:Activity 全额类名}
     */
    private val pageMap = mutableMapOf<String, String>()

    /**
     * 服务map {key:Name,value:Service}
     */
    private val serviceMap = mutableMapOf<String, Any>()


    fun install() {
        println("KRouter init")
    }

    fun registerPage(path: String, className: String) {
        Log.e("registerPage", "path:$path,className:$className")
        pageMap[path] = className
    }

    fun registerService(name: String, service: Any) {
        Log.e("registerPage", "name:$name,className:${service.javaClass.name}")
        serviceMap[name] = service
    }

    fun asNavigator(context: Context) = NavigatorBuilder(context)

    fun loadService(name: String) = ServiceBuilder(name)

    class NavigatorBuilder(private val context: Context) {
        private lateinit var path: String
        private var bundle: Bundle? = null
        private var requestCode:Int = -1
        fun path(path: String) = this.apply { this.path = path }
        fun requestCode(requestCode: Int) = this.apply { this.requestCode = requestCode }
        fun bundle(bundle: Bundle) = this.apply { this.bundle = bundle }
        fun navigate() {
            pageMap[path]?.run {
                val builder = NavigatorRequest.Builder().context(context).className(this).requestCode(requestCode)
                    .interceptors(pageInterceptors)
                bundle?.run {
                    builder.params(this)
                }
                builder.build().execute()
            } ?: kotlin.run {
                log("未找到页面:$path")
            }

        }
    }

    class ServiceBuilder(private val name: String) {
        private var params: Map<String, Any>? = null
        fun params(params: Map<String, Any>) = this.apply { this.params = params }
        fun <T>call(methodName: String):T? {
            serviceMap[name]?.run {
                val builder = ServiceRequest.Builder(this, methodName)
                params?.run {
                    builder.params(this)
                }
                return builder.build().call()
            } ?: kotlin.run {
                log("未找到服务:$name")
                return null
            }
        }
    }
}
