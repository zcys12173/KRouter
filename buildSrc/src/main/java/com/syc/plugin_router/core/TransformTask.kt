package com.syc.plugin_router.core

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class TransformTask : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        serviceList.clear()
        pageList.clear()
        val jarOutput = JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(
                    output.get().asFile
                )
            )
        )
        var targetJarFile: JarFile? = null
        var targetJarEntry: JarEntry? = null

        allJars.get().forEach { file ->
//            println("handling " + file.asFile.absolutePath)
            var isFindTarget = false
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                if (jarEntry.name.endsWith(".class") && !jarEntry.name.contains("META-INF")) {
//                    println("Adding from jar ${jarEntry.name}")
                    if (jarEntry.name.endsWith("KRouter.class")) {
                        targetJarEntry = jarEntry
                        targetJarFile = jarFile
                        isFindTarget = true
                    } else {
                        jarOutput.putNextEntry(JarEntry(jarEntry.name))
                        jarFile.getInputStream(jarEntry).use {
                           readFile(it)
                        }
                        jarFile.getInputStream(jarEntry).use {
                            it.copyTo(jarOutput)
                        }
                    }
                }
                jarOutput.closeEntry()
            }
            if(!isFindTarget){
                jarFile.close()
            }

        }

        allDirectories.get().forEach { directory ->
//            println("handling " + directory.asFile.absolutePath)
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
//                    println(
//                        "Adding from directory ${
//                            relativePath.replace(
//                                File.separatorChar,
//                                '/'
//                            )
//                        }"
//                    )
                    jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                    if (file.name.endsWith(".class")) {
                        file.inputStream().use { inputStream ->
                            readFile(inputStream)
                        }
                    }
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                }
            }
        }


        targetJarFile?.getInputStream(targetJarEntry)?.use {
            val classReader = ClassReader(it.readBytes())
            val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val cv = AutoRegisterVisitor(classWriter)
            val parsingOptions = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
            classReader.accept(cv, parsingOptions)
            jarOutput.putNextEntry(JarEntry(targetJarEntry!!.name))
            jarOutput.write(classWriter.toByteArray())
            jarOutput.closeEntry()
            targetJarFile?.close()
        }

        jarOutput.close()
    }

    private fun readFile(inputStream: InputStream) {
        val cr = ClassReader(inputStream)
        val cv = CollectClassVisitor()
        val parsingOptions = ClassReader.SKIP_DEBUG
        cr.accept(cv, parsingOptions)
        inputStream.close()
    }
}