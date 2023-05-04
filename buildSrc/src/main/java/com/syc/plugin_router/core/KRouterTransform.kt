package com.syc.plugin_router.core

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.syc.plugin_router.utils.shouldProcessEntry
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class KRouterTransform : Transform() {
    override fun getName(): String {
        return "KRouterTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation) {
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        var destJarFile: File?=null
        val outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
//                println("directoryInput:${directoryInput.file.absolutePath}")
                val file = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                directoryInput.file.walk().forEach { fileInput ->
                    if (fileInput.isFile && fileInput.name.endsWith(".class")) {
                        println("----fileInput:${fileInput.absolutePath}")
                        val classReader = ClassReader(fileInput.readBytes())
                        val cv = CollectClassVisitor()
                        val options =
                            ClassReader.SKIP_DEBUG or ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES
                        classReader.accept(cv, options)
                    }
                }
                FileUtils.copyDirectory(directoryInput.file, file)
            }
            input.jarInputs.forEach { jarInput ->
//                println("jarInput:${jarInput.file.absolutePath}")
                val srcFile = jarInput.file
                val destFile = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                val jarFile = JarFile(jarInput.file)
                val enumeration = jarFile.entries()
                while (enumeration.hasMoreElements()) {
                    val jarEntry = enumeration.nextElement()
                    val entryName = jarEntry.name
                    if (entryName.shouldProcessEntry()) {
                        if(entryName == GENERATE_TO_CLASS_FILE_NAME){
                            destJarFile= destFile
                        }else{
                            println("----fileInput:$entryName")
                            val inputStream = jarFile.getInputStream(jarEntry)
                            val classReader = ClassReader(inputStream)
                            val cv = CollectClassVisitor()
                            val options =
                                ClassReader.SKIP_DEBUG or ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES
                            classReader.accept(cv, options)
                            inputStream.close()
                        }
                    }
                }
                jarFile.close()
                FileUtils.copyFile(srcFile, destFile)
            }
        }


        destJarFile?.let {file ->
            val optJar = File(file.parentFile, file.name+".opt")
            if(optJar.exists()){
                optJar.delete()
            }
            val jarFile = JarFile(file)
            val jarOutputStream = JarOutputStream(FileOutputStream(optJar))
            val enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
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