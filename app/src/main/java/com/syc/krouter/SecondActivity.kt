package com.syc.krouter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage
import com.syc.router.service.ResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@RouterPage("path/SecondActivity")
class SecondActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val arg = intent.getStringExtra("params")
        Toast.makeText(this,arg, Toast.LENGTH_SHORT).show()
        findViewById<Button>(R.id.btn).setOnClickListener {
            KRouter.asNavigator(this).path("path/BusinessActivity").navigate()
        }
    }

    override fun finish() {
        setResult(RESULT_OK, Intent().apply {
            putExtra("result", "Second")
        })
        super.finish()
    }

    fun callService(view: View){
//        val result = KRouter.loadService("AppService")
//            .withParams(mutableMapOf("arg" to 1))
//            .call("getAppName")
//        Toast.makeText(this,result.toString(),Toast.LENGTH_SHORT).show()


        val result = KRouter.loadService("AppService")
            .call("getAppNameWithoutParams")
        Toast.makeText(this,result.toString(),Toast.LENGTH_SHORT).show()
    }

    fun asyncCallService(view: View){
//        KRouter.loadService("AppService")
//            .withParams(mutableMapOf("arg" to 1))
//            .callAsync("asyncTest",object : ResultCallback {
//                override fun success(result: Any?) {
//                    Toast.makeText(this@SecondActivity,result.toString(),Toast.LENGTH_SHORT).show()
//                }
//
//                override fun error(errorMsg: String?, cause: Throwable?) {
//                    Toast.makeText(this@SecondActivity,errorMsg,Toast.LENGTH_SHORT).show()
//                }
//            })

        KRouter.loadService("AppService")
            .callAsync("asyncTestWithoutParams",object : ResultCallback {
                override fun success(result: Any?) {
                    Toast.makeText(this@SecondActivity,result.toString(),Toast.LENGTH_SHORT).show()
                }

                override fun error(errorMsg: String?, cause: Throwable?) {
                    Toast.makeText(this@SecondActivity,errorMsg,Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun suspendCallService(view: View){
//        GlobalScope.launch(Dispatchers.Main) {
//            val result = KRouter.loadService("AppService")
//                .withParams(mutableMapOf("arg" to 1))
//                .callSuspend("suspendTest")
//            Toast.makeText(this@SecondActivity,result.toString(),Toast.LENGTH_SHORT).show()
//        }

        GlobalScope.launch(Dispatchers.Main) {
            val result = KRouter.loadService("AppService")
                .callSuspend("suspendTestWithoutParams")
            Toast.makeText(this@SecondActivity,result.toString(),Toast.LENGTH_SHORT).show()
        }
    }
}