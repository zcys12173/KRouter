package com.syc.plugin_router.utils


fun String.shouldProcessEntry(): Boolean {
    return this.endsWith(".class")
            && !this.contains("R\$")
            && !this.contains("R.class")
            && !this.contains("BuildConfig.class")
            && !this.contains("META-INF")
}