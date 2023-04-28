package com.syc.plugin_router.core

import com.syc.plugin_router.ASM_VERSION
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor


data class PageNode(val className: String, val path: String)
data class ServiceNode(val className: String, val path: String)

val pageList = mutableListOf<PageNode>()
val serviceList = mutableListOf<ServiceNode>()

class CollectClassVisitor : ClassVisitor(ASM_VERSION) {
    private var className: String? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name
    }


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return object : AnnotationVisitor(ASM_VERSION) {
            override fun visit(name: String?, value: Any?) {
                if (className == null) {
                    return
                }
                if (descriptor == "Lcom/syc/router/annotations/RouterPage;") {
                    pageList.add(PageNode(className!!, value.toString()))
                } else if (descriptor == "Lcom/syc/router/annotations/RouterService;") {
                    serviceList.add(ServiceNode(className!!, value.toString()))
                }

            }
        }

    }

}
