package com.syc.krouter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage


@RouterPage("path/SecondActivity")
class SecondActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
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
}