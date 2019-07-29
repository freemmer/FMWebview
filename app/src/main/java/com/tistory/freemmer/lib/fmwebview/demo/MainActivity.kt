package com.tistory.freemmer.lib.fmwebview.demo

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.tistory.freemmer.lib.fmwebview.FMWebview
import com.tistory.freemmer.lib.libfm.permission.FMCheckPermission
import com.tistory.freemmer.lib.libfm.util.FMDeviceUtil

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var webview: FMWebview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar?)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        FMCheckPermission.instance(this).check(arrayOf(Manifest.permission.INTERNET)
            , pDeniedFunc = { checkedDoNotAskPermissions, permissions ->
            }, pAllowedFunc = {
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
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
