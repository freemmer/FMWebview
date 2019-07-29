package com.tistory.freemmer.lib.fmwebview.demo.webinterface

import android.content.Intent
import com.google.gson.Gson
import com.tistory.freemmer.lib.fmwebview.demo.webinterface.model.JS_COMMON
import com.tistory.freemmer.lib.fmwebview.demo.webinterface.model.JS_RES_DEVICE_INFO
import com.tistory.freemmer.lib.fmwebview.webinterface.FMWebInterface
import com.tistory.freemmer.lib.libfm.logger.FMILog
import com.tistory.freemmer.lib.libfm.platform.FMBeanManager
import com.tistory.freemmer.lib.libfm.util.FMDeviceUtil
import org.json.JSONObject

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class CommonWebInterface : FMWebInterface() {

    private val REQUEST_CODE = 100

    private val log: FMILog? = FMBeanManager.getClass(FMILog::class.java)

    override fun execute(action: String, params: JSONObject?) {
        log?.d("action: $action, params: $params")
        val paramObj = Gson().fromJson(params.toString(), JS_COMMON::class.java)
        when (action) {
            "getDeviceInfo" -> {
                paramObj.COMMON?.APP_VERSION = FMDeviceUtil.instance(activity).getAppVersion()
                paramObj.COMMON?.ERROR_CODE = "0"

                val resp = JS_RES_DEVICE_INFO()
                resp.COMMON = paramObj.COMMON
                resp.UDID = FMDeviceUtil.instance(activity).getUDID()
                resp.OS_VERSION = FMDeviceUtil.instance(activity).getOSVersion()
                resp.MANUFACTURER = FMDeviceUtil.instance(activity).getManufacture()
                resp.BRAND = FMDeviceUtil.instance(activity).getBrand()
                resp.PRODUCT = FMDeviceUtil.instance(activity).getProductName()
                resp.MODEL = FMDeviceUtil.instance(activity).getModelName()
                if (paramObj.COMMON?.CALLBACK != null) {
                    executeJs(paramObj.COMMON?.CALLBACK!!, Gson().toJson(resp, JS_RES_DEVICE_INFO::class.java))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) return

    }

}
