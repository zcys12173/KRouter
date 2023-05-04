package com.syc.plugin_router

import com.android.build.gradle.AppExtension
import com.syc.plugin_router.core.KRouterTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.Opcodes

const val ASM_VERSION = Opcodes.ASM5

class KRouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.findByType(AppExtension::class.java)?.registerTransform(KRouterTransform())
    }
}

