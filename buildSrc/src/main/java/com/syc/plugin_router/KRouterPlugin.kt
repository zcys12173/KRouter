package com.syc.plugin_router

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.syc.plugin_router.core.TransformTask
import com.syc.plugin_router.log.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.Opcodes

const val ASM_VERSION = Opcodes.ASM9

class KRouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        Logger.init(project.logger)
        Logger.log("KRouterPlugin apply")
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponents?.onVariants {
            val taskProvider = project.tasks.register(
                "KRouterTransform${it.buildType?.capitalize()}Task",
                TransformTask::class.java
            )
            it.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(taskProvider)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    TransformTask::allJars,
                    TransformTask::allDirectories,
                    TransformTask::output
                )

        }
    }
}

