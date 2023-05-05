package com.syc.plugin_router.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

object RegisterCodeProcessor {

    var jarFile: File? = null

    fun process(outputStream: JarOutputStream) {
        val file = JarFile(jarFile)
        file.entries().iterator().forEach { jarEntry ->
            val name = jarEntry.name
            if (name == GENERATE_TO_CLASS_FILE_NAME) {
                file.getInputStream(jarEntry).use {
                    val classReader = ClassReader(it)
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = AutoRegisterVisitor(classWriter)
                    val options = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
                    classReader.accept(cv, options)
                    val bytes = classWriter.toByteArray()
                    outputStream.putNextEntry(JarEntry(name))
                    outputStream.write(bytes)
                }
            }
            outputStream.closeEntry()
        }
        file.close()
        outputStream.close()
    }
}