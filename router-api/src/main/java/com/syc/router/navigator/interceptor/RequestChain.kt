package com.syc.router.navigator.interceptor

import android.app.Activity
import android.content.Intent
import com.syc.router.log.log
import com.syc.router.navigator.NavigatorRequest


interface Interceptor {

    fun intercept(chain: Chain)

    class Chain(private val interceptors:MutableList<Interceptor>) {
        private var curIndex = -1
        lateinit var request: NavigatorRequest
        fun process(request:NavigatorRequest) {
            this.request = request
            curIndex++
            interceptors.getOrNull(curIndex)?.intercept(this)
        }
    }


    class DispatchInterceptor : Interceptor {
        override fun intercept(chain: Chain) {
            with(chain.request) request@{
                context?.run {
                    val intent = Intent(this, Class.forName(className))
                    this@request.params?.run {
                        intent.putExtras(this)
                    }
                    if (requestCode != -1) {
                        if (this is Activity) {
                            this.startActivityForResult(intent, requestCode)
                        } else {
                            log("context is not activity, can not use startActivityForResult")
                        }
                    } else {
                        startActivity(intent)
                    }

                }
            }

        }
    }
}




