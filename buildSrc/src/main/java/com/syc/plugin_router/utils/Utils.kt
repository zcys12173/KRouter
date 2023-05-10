package com.syc.plugin_router.utils


fun String.shouldProcessClass(): Boolean {
    return this.endsWith(".class")
            && !this.startsWith("androidx/")
            && !this.startsWith("android/support/")
            && !this.startsWith("org/jetbrains/")
            && !this.startsWith("kotlin/")
            && !this.startsWith("kotlinx/")
            && !this.startsWith("org/intellij/")
            && !this.startsWith("com/google/android")
            && !this.contains("R\$")
            && !this.contains("R.class")
            && !this.contains("BuildConfig.class")
            && !this.contains("META-INF")
}