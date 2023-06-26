package com.syc.router.annotations


/**
 * Service注解
 * 用于标记Service实现类
 * @param name Service名称
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class RouterService(val name:String)


/**
 * Service方法注解
 * 用于标记Service类中的方法
 * @param name 方法名称
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceMethod(val name:String)


/**
 * Page注解
 * 用于标记页面
 * @param path 页面路径
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class RouterPage(val path:String)

