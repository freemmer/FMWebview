package com.tistory.freemmer.lib.fmwebview.webinterface

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.tistory.freemmer.lib.fmwebview.client.FMWebViewClient
import com.tistory.freemmer.lib.libfm.logger.FMILog
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class FMWebInterfaceManager constructor(
    private val activity: Activity,
    private val webView: WebView,
    private val log: FMILog?
) {

    companion object {
        const val KEY_INTERFACE = "INTERFACE"
        const val KEY_ACTION    = "ACTION"
        const val KEY_PARAMS    = "PARAMS"

        private val classMap = HashMap<String, Class<*>>()
        private var weakReference: WeakReference<FMWebInterfaceManager>? = null

        fun addClass(cls: Class<*>) {
            classMap[cls.simpleName] = cls
        }

        fun instance(activity: Activity, webView: WebView, log: FMILog?): FMWebInterfaceManager {
            if (weakReference?.get() == null) {
                weakReference = WeakReference(
                    FMWebInterfaceManager(activity, webView, log)
                )
            }
            return weakReference?.get()!!
        }
    }

    val instanceMap = HashMap<String, FMWebInterface>()

    fun addWebInterface(cls: Class<*>) {
        classMap[cls.simpleName] = cls
    }

    fun procWebInterface(url: String) {
        val map  = getParsedWebInterface(Uri.decode(url))
        if (map == null) {
            log?.e("Failed! parse WebInterface URL")
            return
        }
        if (map[KEY_INTERFACE] == null || map[KEY_ACTION] == null) {
            log?.e("Wrong Data! parse WebInterface URL")
            return
        }
        log?.printMap(FMILog.LEVEL.DEBUG, "", map)

        if (classMap[map[KEY_INTERFACE]] != null) {
            val params = JSONObject(map[KEY_PARAMS])
            val instance: FMWebInterface? = getWebInstance(classMap[map[KEY_INTERFACE]]!!)
            if (instance == null) {
                log?.e("Failed! create instance : ${map[KEY_INTERFACE]}")
                return
            }
            instance.initialize(activity, webView, log)
            instance.execute(map.getValue(KEY_ACTION), params)
        } else {
            log?.e("Not registered class : ${map[KEY_INTERFACE]}")
            return
        }
    }

    fun procActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (item: Map.Entry<String, FMWebInterface> in instanceMap) {
//            if (item.value.getActivityResultRequestCode() == requestCode) {
//                item.value.onActivityResult(requestCode, resultCode, data)
//                return
//            }
            item.value.onActivityResult(requestCode, resultCode, data)
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> getWebInstance(cls: Class<*>): T? {
        var obj: Any? = instanceMap[cls.simpleName]
        if (obj == null) {
            val inst = classMap[cls.simpleName]
            if (inst != null) {
                try {
                    obj = inst.newInstance()
                    instanceMap[cls.simpleName] = obj as FMWebInterface
                } catch (e: Exception) {
                    log?.exception(e)
                }
            } else {
                log?.e("Not regist class")
            }
        }
        return obj as T?
    }

    private fun getParsedWebInterface(baseUrl: String?): Map<String, String>? {
        if (baseUrl != null && baseUrl.isNotEmpty()) {
            val url = baseUrl.replace(FMWebViewClient.SCHEME_WEB_INTERFACE + FMWebViewClient.SCHEME_TAIL
                , "")
            var resultMap: MutableMap<String, String>? = null
            if (url.isNotBlank()) {
                resultMap = java.util.HashMap()
                val parseUrl = url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parseUrl.size > 1) {
                    val parseAction = parseUrl[0].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parseAction.size > 1) {
                        resultMap[KEY_INTERFACE] = parseAction[0]
                        resultMap[KEY_ACTION] = parseAction[1]
                    }
                    resultMap[KEY_PARAMS] = baseUrl.substring(baseUrl.indexOf("?") + 1)
                }
            }
            return resultMap
        }
        return null
    }

}

