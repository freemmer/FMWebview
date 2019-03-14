package com.tistory.freemmer.lib.fmwebview.client

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.tistory.freemmer.lib.fmwebview.webinterface.FMWebInterfaceManager
import com.tistory.freemmer.lib.libfm.logger.FMILog
import java.net.URLDecoder

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class FMWebViewClient constructor(
    private val context: Context,
    private val webView: WebView,
    private val log: FMILog?
): WebViewClient() {

    companion object {
        const val SCHEME_TAIL: String  = "://"
        const val SCHEME_WEB_INTERFACE: String  = "nativecall"
    }
    private val SCHEME_OS_INTENT: String      = "intent"
    private val SCHEME_OS_MARKET: String      = "market"
    private val SCHEME_OS_TEL: String         = "tel"
    private val SCHEME_OS_SMS: String         = "sms"
    private val SCHEME_OS_MAILTO: String      = "mailto"


    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val uri = Uri.parse(url)
        return processWebInterface(uri)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        return processWebInterface(uri)
    }

    private fun processWebInterface(uri: Uri): Boolean {
        log?.d("shouldOverrideUrlLoading ($uri)")
        val host: String? = uri.host
        val scheme: String? = uri.scheme

        log?.d("host($host), scheme($scheme)")
        when (scheme) {
            SCHEME_WEB_INTERFACE -> {
                log?.d("SCHEME_WEB_INTERFACE process > $scheme")
                FMWebInterfaceManager.instance(context, webView, log).procWebInterface(uri.toString())
                return true
            }
            SCHEME_OS_INTENT -> {
                log?.d("SCHEME_OS_INTENT process > $scheme")
                val intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
                val existPackage = context.packageManager.getLaunchIntentForPackage(intent.`package`)
                if (existPackage != null) {
                    context.startActivity(intent)
                } else {
                    val marketIntent = Intent(Intent.ACTION_VIEW)
                    marketIntent.data = uri
                    context.startActivity(marketIntent)
                }
                return true
            }
            SCHEME_OS_MARKET -> {
                log?.d("SCHEME_OS_MARKET process > $scheme")
                context.startActivity(Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME))
                return true
            }
            SCHEME_OS_TEL -> {
                log?.d("SCHEME_OS_TEL process > $scheme")
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = uri
                context.startActivity(intent)
                return true
            }
            SCHEME_OS_SMS, SCHEME_OS_MAILTO -> {
                log?.d("SCHEME_OS_SMS, SCHEME_OS_MAILTO process > $scheme")
                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(uri.toString())))
                return true
            }
        }
        log?.d("Default process > $scheme")
        return false
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        try {
            log?.d("WebView onPageStarted(%s)", URLDecoder.decode(url, "utf-8"))
        } catch (e: Exception) {
            log?.e(">> Error <<")
            log?.exception(e)
        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
        log?.d("WebView onPageFinished(%s)", URLDecoder.decode(url, "utf-8"))
    }

    override fun onLoadResource(view: WebView, url: String) {
        super.onLoadResource(view, url)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        super.onReceivedSslError(view, handler, error)
    }

    @SuppressWarnings("deprecation")
    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
    }

}

