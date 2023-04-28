package com.syc.module_bussiness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.app.ActivityOptionsCompat
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage


@RouterPage(path = "path/BusinessActivity")
class BusinessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bussiness)
//        KRouter.loadService("AppService").call("getAppName")
        val name = KRouter.loadService("AppService").params(mutableMapOf(Pair("arg1",111))).call<String>("test")
        Toast.makeText(this,name,Toast.LENGTH_SHORT).show()
    }
}