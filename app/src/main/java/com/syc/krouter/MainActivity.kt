package com.syc.krouter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage


@RouterPage("path/MainActivity")
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener {
            val params = Bundle()
            params.putString("params","Main")
            KRouter.asNavigator(this).path("path/SecondActivity").requestCode(1001).bundle(params).navigate()
//            testAsm()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==1001 && resultCode == Activity.RESULT_OK){
            Log.e("sss","-----${data?.getStringExtra("result")}")
        }
    }

    private fun testAsm(){
        KRouter.install()
    }
}