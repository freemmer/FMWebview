package com.tistory.freemmer.lib.fmwebview.webinterface

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.tistory.freemmer.lib.libfm.logger.FMILog
import org.json.JSONObject

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
abstract class FMWebInterface {
    lateinit var context: Context
    lateinit var webview: WebView
    private var libLog: FMILog? = null

    fun initialize(context: Context, view: WebView, log: FMILog?) {
        this.context = context
        this.webview = view
        this.libLog = log
    }

    fun executeJs(funcName: String, paramsJson: String) {
        if (funcName.isBlank()) {
            libLog?.e("funcName is Blank")
            return
        }
        val encodeParams = Uri.encode(paramsJson, "utf-8")
        val command = "javascript:$funcName('$encodeParams');"
        libLog?.d("Execute JS : $command")
        webview.loadUrl(command)
    }

    abstract fun execute(action: String, params: JSONObject?)
    abstract fun getActivityResultRequestCode() : Int
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

}
