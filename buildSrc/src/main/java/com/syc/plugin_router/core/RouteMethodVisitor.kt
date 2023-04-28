package com.syc.plugin_router.core

import com.syc.plugin_router.ASM_VERSION
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class RouteMethodVisitor(mv:MethodVisitor,access:Int,name:String?,descriptor: String?): AdviceAdapter(ASM_VERSION,mv,access,name,descriptor) {
    override fun onMethodEnter() {
        super.onMethodEnter()
        if (name == "install"){
            pageList.forEach {
                val className = it.className.replace("/",".")
                mv.visitVarInsn(Opcodes.ALOAD,0)
                mv.visitLdcInsn(it.path)
                mv.visitLdcInsn(className)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"com/syc/router/KRouter","registerPage","(Ljava/lang/String;Ljava/lang/String;)V",false)
            }

            serviceList.forEach {
                mv.visitTypeInsn(Opcodes.NEW,it.className)
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL,it.className,"<init>","()V",false)
                mv.visitVarInsn(Opcodes.ASTORE,1)
                mv.visitVarInsn(Opcodes.ALOAD,0)
                mv.visitLdcInsn(it.path)
                mv.visitVarInsn(Opcodes.ALOAD,1)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"com/syc/router/KRouter","registerService","(Ljava/lang/String;Ljava/lang/Object;)V",false)
            }
        }
    }
}