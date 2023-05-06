package com.syc.router.service

interface ResultCallback{
    fun success(result:Any?=null)
    fun error(errorMsg:String?,cause:Throwable? = null)
}