package com.syc.plugin_router.core

import com.syc.plugin_router.log.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
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
        className = name
    }


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return object : AnnotationVisitor(API_VERSION) {
            override fun visit(name: String?, value: Any?) {
                if (className == null) {
                    return
                }
                if (descriptor == "L$INJECT_ROUTER_PAGE_ANNOTATION_CLASS_NAME;") {
                    Logger.log("register page class:$className; path:$value")
                    pageList.add(PageNode(className!!, value.toString()))
                } else if (descriptor == "L$INJECT_ROUTER_SERVICE_ANNOTATION_CLASS_NAME;") {
                    Logger.log("register service class:$className; path:$value")
                    serviceList.add(ServiceNode(className!!, value.toString()))
                }

            }
        }

    }

}
