package com.syc.plugin_router.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

object RegisterCodeProcessor {

    var jarFile: File? = null

    fun process() {
        jarFile?.let {file ->
            val optJar = File(file.parentFile, file.name+".opt")
            if(optJar.exists()){
                optJar.delete()
            }
            val jarFile = JarFile(file)
            val jarOutputStream = JarOutputStream(FileOutputStream(optJar))
            jarFile.entries().iterator().forEach { jarEntry ->
                val entryName = jarEntry.name
                val inputStream = jarFile.getInputStream(jarEntry)
                if(entryName == GENERATE_TO_CLASS_FILE_NAME){
                    val classReader = ClassReader(inputStream)
                    val classWriter = ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                    val cv = AutoRegisterVisitor(classWriter)
                    val options =
                        ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
                    classReader.accept(cv, options)
                    val bytes = classWriter.toByteArray()
                    jarOutputStream.putNextEntry(JarEntry(entryName))
                    jarOutputStream.write(bytes)
                }else{
                    jarOutputStream.putNextEntry(JarEntry(entryName))
                    inputStream.copyTo(jarOutputStream)
                }
                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            if(file.exists()){
                file.delete()
            }
            optJar.renameTo(file)
        }
    }
}