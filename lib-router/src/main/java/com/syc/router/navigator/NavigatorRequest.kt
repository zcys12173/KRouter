package com.syc.router.navigator

import android.content.Context
import android.os.Bundle
import com.syc.router.navigator.interceptor.Interceptor

class NavigatorRequest private constructor(
    val context: Context? = null,
    val className: String = "",
    val params: Bundle? = null,
    val requestCode:Int = -1,
    val interceptors: MutableList<Interceptor> = mutableListOf()
) {


    fun execute() {
        val hasAdded = interceptors.any { it is Interceptor.DispatchInterceptor }
        if (!hasAdded) {
            interceptors.add(Interceptor.DispatchInterceptor())
        }
        Interceptor.Chain(this).process()
    }


    class Builder {
        private var context: Context? = null
        private var className: String = ""
        private var params: Bundle? = null
        private var requestCode:Int = -1
        private var interceptors: MutableList<Interceptor> = mutableListOf()

        fun context(context: Context) = this.apply { this.context = context }

        fun requestCode(requestCode: Int) = this.apply { this.requestCode = requestCode }

        fun className(className: String) = this.apply { this.className = className }

        fun params(params: Bundle) = this.apply { this.params = params }

        fun interceptors(interceptors: MutableList<Interceptor>) =
            this.apply { this.interceptors = interceptors }

        fun build(): NavigatorRequest {
            return NavigatorRequest(context, className, params,requestCode, interceptors)
        }
    }
}