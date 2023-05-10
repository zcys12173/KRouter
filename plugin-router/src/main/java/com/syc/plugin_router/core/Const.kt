package com.syc.plugin_router.core

import org.objectweb.asm.Opcodes

const val API_VERSION = Opcodes.ASM5

const val GENERATE_TO_CLASS_NAME = "com/syc/router/KRouter"

const val GENERATE_TO_CLASS_FILE_NAME = "$GENERATE_TO_CLASS_NAME.class"

const val INJECT_ROUTER_PAGE_ANNOTATION_CLASS_NAME="com/syc/router/annotations/RouterPage"

const val INJECT_ROUTER_SERVICE_ANNOTATION_CLASS_NAME="com/syc/router/annotations/RouterService"

const val INJECT_ROUTER_INSTALL_METHOD = "install"

enum class InjectMethodType(val methodName: String, val methodDesc: String) {
    REGISTER_PAGE("registerPage", "(Ljava/lang/String;Ljava/lang/String;)V"),
    REGISTER_SERVICE("registerService", "(Ljava/lang/String;Ljava/lang/Object;)V"),
}