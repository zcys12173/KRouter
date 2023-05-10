package com.syc.plugin_router.core

import org.objectweb.asm.ClassReader
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

fun scanFileInput(file: File) {
    file.inputStream().use { inputStream ->
        scanFromInputStream(inputStream)
    }
}

fun JarFile.scanJarInput(jarEntry: JarEntry) {
    val inputStream = getInputStream(jarEntry)
    scanFromInputStream(inputStream)
}

private fun scanFromInputStream(inputStream: InputStream) {
    val classReader = ClassReader(inputStream)
    val cv = CollectClassVisitor()
    val options =
        ClassReader.SKIP_DEBUG or ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES
    classReader.accept(cv, options)
    inputStream.close()
}

fun clearCache(){
    pageList.clear()
    serviceList.clear()
}

data class PageNode(val className: String, val path: String)
data class ServiceNode(val className: String, val path: String)

val pageList = mutableListOf<PageNode>()
val serviceList = mutableListOf<ServiceNode>()
