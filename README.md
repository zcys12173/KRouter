# KRouter

## 引用 

#### 引用KRouter自动注册插件  

`AGP7.4+`  

```kotlin
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.zcys12173.plugins:plugin-router-7-4:1.0.0-pre"
  }
}

apply plugin: "io.github.zcys12173.plugin_router"
```  

`AGP7.0`之前的版本

```kotlin
//  project/build.gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.zcys12173.plugins:plugin-router:1.0.0-pre"
  }
}

//  app/build.gradle
apply plugin: "io.github.zcys12173.plugin_router_4_2"
```

#### 引用KRouter Api库

* `project/build.gradle`中增加`Maven`中央仓库  
```gradle
 repositories {
    mavenCentral()
   }  
 ```
 
* `module/build.gradle`增加依赖    

 ```gradle
implementation 'io.github.zcys12173:KRouter:1.0.0-pre'  

```

## 初始化 
`KRouter.install()` 

```kotlin
class MyApplication:Application() {  
    override fun onCreate() {  
        super.onCreate()  
        KRouter.install()  
    }  
}  
```

## 页面跳转
#### 注册`Activity`
```kotlin
@RouterPage(MAIN_ACTIVITY_PATH)
class MainActivity : Activity() {...}   
```

#### 路由跳转
```kotlin
//如果传递requestCode的话，会以startActivityForResult方式启动新的activity
KRouter.asNavigator(this).path(SECOND_ACTIVITY_PATH).requestCode(1001).withString("params","Main").navigate()
```

#### 参数接收  

跳转的参数都是通过`intent.putExtras`方式传递的，目标页面可通过`getIntent.getXXExtra("xx")`方式接收  


## Service调用
#### 注册Service  
*  使用`@RouterService`标记`Service`的类
*  使用`@ServiceMethod`标记函数，入参仅支持下面列举出的几种，支持参数为可空（如果不可空，调用的时候必须传递） 

```kotlin
@RouterService("AppService")
class AppService {


    @ServiceMethod("getAppNameWithoutParams")
    fun getAppName(): String {
        Log.e("sss","run getAppNameWithoutParams")
        return "KRouterAppName"
    }

    @ServiceMethod("getAppName")
    fun getAppName(params:Map<String,Any>): String {
        Log.e("sss","run getAppName，$params")
        return "KRouterAppName"
    }

    @ServiceMethod("asyncTest")
    fun asyncTest(params:Map<String,Any>,callback: ResultCallback) {
        Log.e("sss","$params")
        callback.success("KRouterAppName")
    }

    @ServiceMethod("asyncTestWithoutParams")
    fun asyncTest(callback: ResultCallback) {
        callback.success("KRouterAppName")
    }

    @ServiceMethod("suspendTest")
    suspend fun suspendTest(params:Map<String,Any>?):String{
        Log.e("sss",params.toString())
        return withContext(Dispatchers.IO){
            delay(3000)
            "BusinessService"
        }
    }

    @ServiceMethod("suspendTestWithoutParams")
    suspend fun suspendTest():String{
        return withContext(Dispatchers.IO){
            delay(3000)
            "BusinessService"
        }
    }
}
```



#### Service调用  

```kotlin
    //直接调用
    fun callService(view: View){
//        val result = KRouter.loadService("AppService")
//            .withParams(mutableMapOf("arg" to 1))
//            .call("getAppName")
//        Toast.makeText(this,result.toString(),Toast.LENGTH_SHORT).show()


        val result = KRouter.loadService("AppService")
            .call("getAppNameWithoutParams")
        Toast.makeText(this,result.toString(),Toast.LENGTH_SHORT).show()
    }

    //异步调用，通过callback返回参数
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
    
    //通过协程挂起函数调用
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
```
