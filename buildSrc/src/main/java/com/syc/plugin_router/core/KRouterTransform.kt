package com.syc.plugin_router.core

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.syc.plugin_router.log.Logger
import com.syc.plugin_router.utils.shouldProcessClass
import org.apache.commons.io.FileUtils
import java.util.jar.JarFile

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
        clearCache()
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        val outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
//                Logger.log("directoryInput:${directoryInput.file.absolutePath}")
                val file = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                directoryInput.file.walk().forEach { fileInput ->
                    if (fileInput.isFile && fileInput.name.shouldProcessClass()) {
//                        Logger.log("----FileInput:${fileInput.absolutePath}")
                        scanFileInput(fileInput)
                    }
                }
                FileUtils.copyDirectory(directoryInput.file, file)
            }
            input.jarInputs.forEach { jarInput ->
//                Logger.log("jarInput:${jarInput.file.absolutePath}")
                val srcFile = jarInput.file
                val destFile = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                val jarFile = JarFile(jarInput.file)
                jarFile.entries().iterator().forEach {jarEntry ->
                    val entryName = jarEntry.name
                    if (entryName.shouldProcessClass()) {
                        if(entryName == GENERATE_TO_CLASS_FILE_NAME){
                            RegisterCodeProcessor.jarFile= destFile
                        }else{
//                            Logger.log("----JarInput:$entryName")
                            jarFile.scanJarInput(jarEntry)
                        }
                    }
                }
                jarFile.close()
                FileUtils.copyFile(srcFile, destFile)
            }
        }
        RegisterCodeProcessor.process()
    }
}