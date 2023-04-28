package com.syc.plugin_router

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.syc.plugin_router.core.ActivityAutoRegisterVisitor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor

class KRouterPlugin:Plugin<Project>{
    override fun apply(project: Project) {
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponents?.onVariants {
            it.instrumentation.transformClassesWith(RouterAsmClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ){

            }
        }
    }

}

interface RouterParams: InstrumentationParameters {


}

abstract class RouterAsmClassVisitorFactory: AsmClassVisitorFactory<RouterParams>{
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ActivityAutoRegisterVisitor(nextClassVisitor)
    }


}