package com.syc.plugin_router

import com.android.build.gradle.AppExtension
import com.syc.plugin_router.core.KRouterTransform
import com.syc.plugin_router.log.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class KRouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        Logger.init(project.logger)
        Logger.log("KRouterPlugin apply")
        project.extensions.findByType(AppExtension::class.java)
            ?.registerTransform(KRouterTransform())
    }
}

