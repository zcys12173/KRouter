package com.syc.router

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.syc.router.log.log
import com.syc.router.navigator.NavigatorRequest
import com.syc.router.navigator.interceptor.Interceptor
import com.syc.router.service.ResultCallback
import com.syc.router.service.ServiceRequest
import java.io.Serializable

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


    fun install() {}

    fun registerPage(path: String, className: String) {
        pageMap[path] = className
    }

    fun registerService(name: String, service: Any) {
        serviceMap[name] = service
    }

    /**
     * 添加全局拦截器
     */
    fun addInterceptor(interceptor: Interceptor) {
        pageInterceptors.add(interceptor)
    }

    fun asNavigator(context: Context) = NavigatorBuilder(context)

    fun loadService(name: String) = ServiceBuilder(name)

    class NavigatorBuilder(private val context: Context) {
        private lateinit var path: String
        private var bundle: Bundle = Bundle()
        private var requestCode: Int = -1
        fun path(path: String) = this.apply { this.path = path }
        fun requestCode(requestCode: Int) = this.apply { this.requestCode = requestCode }
        fun withBundle(bundle: Bundle) = this.apply { this.bundle.putAll(bundle) }
        fun withBoolean(key: String, value: Boolean) =
            this.apply { this.bundle.putBoolean(key, value) }

        fun withLong(key: String, value: Long) = this.apply { this.bundle.putLong(key, value) }
        fun withString(key: String, value: String) =
            this.apply { this.bundle.putString(key, value) }

        fun withInt(key: String, value: Int) = this.apply { this.bundle.putInt(key, value) }
        fun withFloat(key: String, value: Float) = this.apply { this.bundle.putFloat(key, value) }
        fun withDouble(key: String, value: Double) =
            this.apply { this.bundle.putDouble(key, value) }

        fun withParcelable(key: String, value: Parcelable) =
            this.apply { this.bundle.putParcelable(key, value) }

        fun withSerializable(key: String, value: Serializable) =
            this.apply { this.bundle.putSerializable(key, value) }


        fun navigate() {
            pageMap[path]?.run {

                val builder = NavigatorRequest.Builder().context(context).className(this)
                    .path(path)
                    .requestCode(requestCode)
                bundle.takeIf { !it.isEmpty }?.run {
                    builder.params(this)
                }
                val request = builder.build()

                val interceptors = mutableListOf<Interceptor>()
                interceptors.addAll(pageInterceptors)
                interceptors.add(Interceptor.DispatchInterceptor())

                Interceptor.Chain(interceptors).process(request)
            } ?: kotlin.run {
                log("未找到页面:$path")
            }

        }
    }

    class ServiceBuilder(private val name: String) {
        private var params: Map<String, Any>? = null
        fun withParams(params: Map<String, Any>) = this.apply { this.params = params }

        fun call(methodName: String):Any? {
            serviceMap[name]?.run {
                val builder = ServiceRequest.Builder(this, methodName)
                params?.run {
                    builder.params(this)
                }
                val request = builder.build()
                return ServiceRequest.Call(request).call()
            } ?: throw RuntimeException("未找到服务:$name")
        }

        fun callAsync(methodName: String, callback: ResultCallback) {
            serviceMap[name]?.run {
                val builder = ServiceRequest.Builder(this, methodName)
                    .withCallback(callback)
                params?.run {
                    builder.params(this)
                }
                val request = builder.build()
                ServiceRequest.Call(request).callAsync()
            } ?: throw RuntimeException("未找到服务:$name")
        }

        suspend fun callSuspend(methodName: String): Any? {
            serviceMap[name]?.run {
                val builder = ServiceRequest.Builder(this, methodName)
                params?.run {
                    builder.params(this)
                }
                val request = builder.build()
                return ServiceRequest.Call(request).callSuspend()
            } ?: kotlin.run {
                log("未找到服务:$name")
                return null
            }
        }

        fun async() {}
    }
}
