![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
[![](https://jitpack.io/v/freemmer/FMWebview.svg)](https://jitpack.io/#freemmer/FMWebview)

# FMWebview
Webview For Hybrid or Web app on Android (Kotlin)

## Demo ScreenShot
![Screenshot](https://github.com/freemmer/FMWebview/blob/master/Screenshots/NativeCallTest.gif)

## Setup
Project build.gradle
```Groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

App build.gradle
```Groovy
dependencies {
    implementation 'com.github.freemmer:FMWebview:1.0.0'
}
```

## How to use
+ Application Class
```Kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Register WebInterface class
        FMWebInterfaceManager.addClass(CommonWebInterface::class.java)
    }
}
```

+ Main Activity
```Kotlin
class MainActivity : AppCompatActivity() {

    private var webview: FMWebview? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webview = FMWebview.build(this, main_webview, true) {
                        userAgent = String.format(
                                        "%s/FMWebview/%s|%s|%s",
                                        main_webview.settings.userAgentString,
                                        FMDeviceUtil.instance(this@MainActivity).getAppVersion(),
                                        Build.VERSION.RELEASE,
                                        Build.MODEL
                                    )
                    }.initialize()
        webview?.loadUrl("file:///android_asset/sample.html")
    }
}
```

+ WebInterface
```Kotlin
class CommonWebInterface : FMWebInterface() {

    private val REQUEST_CODE = 100

    private val log: FMILog? = FMBeanManager.getClass(FMILog::class.java)

    override fun execute(action: String, params: JSONObject?) {
        log?.d("action: $action, params: $params")
        val paramObj = Gson().fromJson(params.toString(), JS_COMMON::class.java)
        when (action) {
            "getDeviceInfo" -> {
                paramObj.COMMON?.APP_VERSION = FMDeviceUtil.instance(context).getAppVersion()
                paramObj.COMMON?.ERROR_CODE = "0"

                val resp = JS_RES_DEVICE_INFO()
                resp.COMMON = paramObj.COMMON
                resp.UDID = FMDeviceUtil.instance(context).getUDID()
                resp.OS_VERSION = FMDeviceUtil.instance(context).getOSVersion()
                resp.MANUFACTURER = FMDeviceUtil.instance(context).getManufacture()
                resp.BRAND = FMDeviceUtil.instance(context).getBrand()
                resp.PRODUCT = FMDeviceUtil.instance(context).getProductName()
                resp.MODEL = FMDeviceUtil.instance(context).getModelName()
                if (paramObj.COMMON?.CALLBACK != null) {
                    executeJs(paramObj.COMMON?.CALLBACK!!, Gson().toJson(resp, JS_RES_DEVICE_INFO::class.java))
                }
            }
        }
    }

    override fun getActivityResultRequestCode() : Int {
        return REQUEST_CODE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) return

    }

}
```

## License 
```code
This software is licensed under the [Apache 2 license](LICENSE), quoted below.

Copyright 2019 freemmer. <http://freemmer.tistory.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this project except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
