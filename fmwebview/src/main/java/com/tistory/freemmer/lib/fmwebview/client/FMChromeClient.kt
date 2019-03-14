package com.tistory.freemmer.lib.fmwebview.client

import android.webkit.*
import com.tistory.freemmer.lib.libfm.logger.FMILog

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class FMChromeClient constructor(
    private val log: FMILog?
) : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        if (consoleMessage.sourceId() != null) {
            log?.d("JS console: source(%s:%d) > %s"
                , consoleMessage.sourceId()
                , consoleMessage.lineNumber()
                , consoleMessage.message())
        } else {
            log?.d("JS console: ${consoleMessage.message()}")
        }
        return true
    }

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        log?.d("JS alert: URL($url) Message($message)")
        return true
    }

    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        log?.d("JS confirm : URL($url) Message($message)")
        return super.onJsConfirm(view, url, message, result)
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        callback.invoke(origin, true, false)
    }

}


