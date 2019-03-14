package com.tistory.freemmer.lib.fmwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.tistory.freemmer.lib.fmwebview.client.FMChromeClient
import com.tistory.freemmer.lib.fmwebview.client.FMWebViewClient
import com.tistory.freemmer.lib.libfm.logger.FMILog
import com.tistory.freemmer.lib.libfm.logger.impl.FMLogCatImpl
import java.net.URLEncoder

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class FMWebview private constructor(
    private val activity: Activity,
    val webView: WebView,
    private val isDebug: Boolean = false
) {
    private var log: FMILog? = null
    private var userAgent: String? = null
    var pOnReceivedSslError: ((view: WebView, handler: SslErrorHandler, error: SslError) -> Unit)? = null
    var pOnReceivedError: ((view: WebView?, request: WebResourceRequest?, error: WebResourceError?) -> Unit)? = null
    var pOnReceivedErrorOld: ((view: WebView, errorCode: Int, description: String, failingUrl: String) -> Unit)? = null


    companion object {
        fun build(activity: Activity, webView: WebView, isDebug: Boolean, init: FMWebview.Builder.() -> Unit): FMWebview
                = FMWebview.Builder(activity, webView, isDebug, init).build()
    }


    class Builder(private val activity: Activity
                  , private val webView: WebView
                  , private val isDebug: Boolean
                  , init: Builder.() -> Unit
    ) {
        var userAgent: String? = null
        var pOnReceivedSslError: ((view: WebView, handler: SslErrorHandler, error: SslError) -> Unit)? = null
        var pOnReceivedError: ((view: WebView?, request: WebResourceRequest?, error: WebResourceError?) -> Unit)? = null
        var pOnReceivedErrorOld: ((view: WebView, errorCode: Int, description: String, failingUrl: String) -> Unit)? = null

        init {
            init()
        }

        fun build(): FMWebview {
            val webview = FMWebview(activity, webView, isDebug)
            webview.userAgent = this.userAgent
            webview.pOnReceivedSslError = this.pOnReceivedSslError
            webview.pOnReceivedError = this.pOnReceivedError
            webview.pOnReceivedErrorOld = this.pOnReceivedErrorOld
            return webview
        }

        companion object {
            fun build(activity: Activity, webView: WebView, isDebug: Boolean, init: Builder.() -> Unit)
                    = Builder(activity, webView, isDebug, init).build()
        }
    }


    fun initialize(): FMWebview {
        if (isDebug) {
            log = FMLogCatImpl.build(FMILog.LEVEL.DEBUG, activity.resources.getString(R.string.lib_name), null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
        setWebviewSetting(webView)
        return this
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun postUrl(url: String, param: Map<String, String>) {
        val postData = StringBuilder()
        for ((key, value) in param) {
            postData.append("$key=")
            postData.append(URLEncoder.encode(value, "UTF-8"))
            postData.append("&")
        }
        postData.deleteCharAt(postData.lastIndex)
        webView.postUrl(url, postData.toString().toByteArray())
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebviewSetting(webview: WebView) {
        val webSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // LOAD_CACHE_ELSE_NETWORK
        if (userAgent != null) {
            webSettings.userAgentString = userAgent
        }
        webSettings.useWideViewPort = true
        webSettings.allowFileAccess = true
        webSettings.setAppCachePath(activity.cacheDir.absolutePath)
        webSettings.setAppCacheEnabled(true)
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.allowUniversalAccessFromFileURLs = true
        webview.isHapticFeedbackEnabled = false
        webview.isHorizontalScrollBarEnabled = false
        webview.clearSslPreferences()
        webview.requestFocus()
        webview.isFocusable = true
        webview.isFocusableInTouchMode = true
        webview.webViewClient = FMWebViewClient(activity, this, log)
        webview.webChromeClient = FMChromeClient(log)
    }

}

