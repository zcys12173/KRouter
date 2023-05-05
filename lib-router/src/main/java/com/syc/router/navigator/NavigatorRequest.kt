package com.syc.router.navigator

import android.content.Context
import android.os.Bundle

class NavigatorRequest private constructor(
    val context: Context? = null,
    var className: String = "",
    var path: String = "",
    var params: Bundle? = null,
    var requestCode: Int = -1,
) {
    class Builder {
        private var context: Context? = null
        private var className: String = ""
        private var path: String = ""
        private var params: Bundle? = null
        private var requestCode: Int = -1

        fun context(context: Context) = this.apply { this.context = context }

        fun requestCode(requestCode: Int) = this.apply { this.requestCode = requestCode }

        fun className(className: String) = this.apply { this.className = className }
        fun path(path: String) = this.apply { this.path = path }

        fun params(params: Bundle) = this.apply { this.params = params }

        fun build(): NavigatorRequest {
            return NavigatorRequest(context, className, path, params, requestCode)
        }
    }
}