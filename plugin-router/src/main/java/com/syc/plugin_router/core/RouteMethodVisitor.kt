package com.syc.plugin_router.core

import com.syc.plugin_router.log.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class RouteMethodVisitor(mv: MethodVisitor, access: Int, name: String?, descriptor: String?) :
    AdviceAdapter(API_VERSION, mv, access, name, descriptor) {
    override fun onMethodEnter() {
        super.onMethodEnter()
        Logger.log("inject router page and service into method:$name")
        pageList.forEach {
            val className = it.className.replace("/", ".")
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitLdcInsn(it.path)
            mv.visitLdcInsn(className)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                GENERATE_TO_CLASS_NAME,
                InjectMethodType.REGISTER_PAGE.methodName,
                InjectMethodType.REGISTER_PAGE.methodDesc,
                false
            )
        }

        serviceList.forEach {
            mv.visitTypeInsn(Opcodes.NEW, it.className)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, it.className, "<init>", "()V", false)
            mv.visitVarInsn(Opcodes.ASTORE, 1)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitLdcInsn(it.path)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                GENERATE_TO_CLASS_NAME,
                InjectMethodType.REGISTER_SERVICE.methodName,
                InjectMethodType.REGISTER_SERVICE.methodDesc,
                false
            )
        }
    }
}