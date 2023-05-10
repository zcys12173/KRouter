package com.syc.krouter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@RouterPage(MAIN_ACTIVITY_PATH)
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener {
            KRouter.asNavigator(this).path(SECOND_ACTIVITY_PATH).requestCode(1001).withString("params","Main").navigate()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==1001 && resultCode == Activity.RESULT_OK){
            Log.e("sss","-----${data?.getStringExtra("result")}")
        }
    }


}