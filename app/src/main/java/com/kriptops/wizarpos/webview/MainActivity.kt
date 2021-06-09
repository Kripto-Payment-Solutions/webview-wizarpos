package com.kriptops.wizarpos.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var firstTimeLoaded = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        initUI()
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initUI() {
        if (isOnline(this)) {
            firstTimeLoaded = true
            webview.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (firstTimeLoaded) {
                        progressbar.visibility = View.VISIBLE
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    firstTimeLoaded = false
                    progressbar.visibility = View.GONE
                    webview.visibility = View.VISIBLE
                }
            }
            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true
            webview.settings.javaScriptCanOpenWindowsAutomatically = true
            webview.settings.databaseEnabled = true
            webview.settings.javaScriptCanOpenWindowsAutomatically = true
            webview.settings.setSupportMultipleWindows(true)
            //webview.settings.setAppCacheEnabled(true)
            webview.loadUrl(getString(R.string.main_url))
        } else {
            tvNoInternetConnection.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                return true
            }
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack()
            return true
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name)
            builder.setMessage(R.string.exit_message)
            builder.setPositiveButton(R.string.yes) { _, _ -> finish() }
            builder.setNegativeButton(R.string.no) { _, _ -> }
            builder.show()
        }
        return super.onKeyDown(keyCode, event)
    }
}