package com.syc.plugin_router.core

import com.syc.plugin_router.utils.shouldProcessClass
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
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
        clearCache()
        val needCreateTempOutputFile = allJars.get().contains(output.get())
        val outputFile = output.get().asFile
        val finalOutputFile = if(needCreateTempOutputFile){
            File(outputFile.parent,"${outputFile.name}_temp")
        }else{
            outputFile
        }
        val jarOutput = JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(
                    finalOutputFile
                )
            )
        )

        allJars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                if (jarEntry.name.shouldProcessClass()) {
                    if (jarEntry.name == GENERATE_TO_CLASS_FILE_NAME) {
                        //这里不做任何处理，等待最后插入扫码到的所有结果
                        RegisterCodeProcessor.jarFile = file.asFile
                    } else {
                        jarOutput.putNextEntry(JarEntry(jarEntry.name))
                        jarFile.scanJarInput(jarEntry)
                        jarFile.getInputStream(jarEntry).use {
                            it.copyTo(jarOutput)
                        }
                    }
                }
                jarOutput.closeEntry()
            }
            jarFile.close()
        }

        allDirectories.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                    jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                    if (file.name.shouldProcessClass()) {
                        scanFileInput(file)
                    }
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                }
            }
        }
        RegisterCodeProcessor.process(jarOutput)
        jarOutput.close()
        if(needCreateTempOutputFile){
            finalOutputFile.copyTo(outputFile,overwrite = true)
        }
    }

}