package com.syc.plugin_router.log

import org.gradle.api.logging.Logger

object Logger {
    private lateinit var mLogger: Logger

    fun init(logger: Logger) {
        mLogger = logger
    }

    fun log(text: String){
        mLogger.lifecycle(text)
    }

    fun q(text: String) {
        mLogger.quiet(text)
    }
    fun d(text: String) {
        mLogger.debug(text)
    }

    fun e(text: String) {
        mLogger.error(text)
    }

    fun i(text: String) {
        mLogger.info(text)
    }

    fun w(text: String) {
        mLogger.warn(text)
    }


}


