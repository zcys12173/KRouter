package com.syc.module_bussiness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.syc.router.KRouter
import com.syc.router.annotations.RouterPage
import com.syc.router.service.ResultCallback


@RouterPage(path = "path/BusinessActivity")
class BusinessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bussiness)
        KRouter.loadService("AppService").withParams(mutableMapOf(Pair("arg1",111))).callAsync("test", object : ResultCallback {
            override fun success(result: Any?) {
                Toast.makeText(this@BusinessActivity,result.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun error(errorMsg: String?, cause: Throwable?) {
                Toast.makeText(this@BusinessActivity,errorMsg,Toast.LENGTH_SHORT).show()
            }

        })

    }
}