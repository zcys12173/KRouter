package com.syc.plugin_router.utils


fun String.shouldProcessClass(): Boolean {
    return this.endsWith(".class")
            && !this.contains("META-INF")
}