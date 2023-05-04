package com.syc.plugin_router.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor


data class PageNode(val className: String, val path: String)
data class ServiceNode(val className: String, val path: String)

val pageList = mutableListOf<PageNode>()
val serviceList = mutableListOf<ServiceNode>()

class CollectClassVisitor : ClassVisitor(API_VERSION) {
    private var className: String? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("visit name=$name")
        className = name
    }


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return object : AnnotationVisitor(API_VERSION) {
            override fun visit(name: String?, value: Any?) {
                if (className == null) {
                    return
                }
                if (descriptor == "L$INJECT_ROUTER_PAGE_ANNOTATION_CLASS_NAME;") {
                    pageList.add(PageNode(className!!, value.toString()))
                } else if (descriptor == "L$INJECT_ROUTER_SERVICE_ANNOTATION_CLASS_NAME;") {
                    serviceList.add(ServiceNode(className!!, value.toString()))
                }

            }
        }

    }

}
