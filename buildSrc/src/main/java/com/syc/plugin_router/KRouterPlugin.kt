package com.syc.plugin_router

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.syc.plugin_router.core.TransformTask
import com.syc.plugin_router.log.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class KRouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        Logger.init(project.logger)
        Logger.log("KRouterPlugin apply")
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponents?.onVariants {
            val taskName = "KRouterTransform${it.buildType?.capitalize()}${ it.flavorName?.capitalize()}Task"
            val taskProvider = project.tasks.register(
                taskName,
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

