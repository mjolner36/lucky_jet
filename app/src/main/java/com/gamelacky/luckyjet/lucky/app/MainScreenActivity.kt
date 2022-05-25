package com.gamelacky.luckyjet.lucky.app

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.web.*
import moxy.MvpAppCompatActivity


class MainScreenActivity : MvpAppCompatActivity(R.layout.web) {
    companion object {
        const val REQUEST_SELECT_FILE = 100
        var uploadMessage: ValueCallback<Array<Uri>>? = null
        const val REQUEST_CODE_KEY = "REQUEST_CODE_KEY"
        const val IS_FIRST_RUN_KEY = "IS_FIRST_RUN_KEY"
        const val PREFERENCE_KEY = "PREFERENCE"
        const val URL_KEY = "url"
        const val FILTER_URL = "filter"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWebView()
        val url = intent.extras?.getString(URL_KEY)
        url?.let { webView.loadUrl(it) }
    }


    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            showExitDialog()
        }
    }

    override fun onStop() {
        webView.url?.let { saveUrl(applicationContext, it) }
        super.onStop()

    }

    private fun saveUrl(context: Context, url: String) {
        context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(URL_KEY, url)
            .apply()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        progressBar?.visibility = View.VISIBLE

        webView.webViewClient = MyWebViewClient(progressBar)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setAppCacheEnabled(false)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.webChromeClient = MyWebChromeClient()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.error_icon)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close app?")
            .setPositiveButton(
                "Yes"
            ) { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    //WebChromeClient setup
    inner class MyWebChromeClient : WebChromeClient() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            if (uploadMessage != null) {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null
            }
            uploadMessage = filePathCallback
            val intent = fileChooserParams.createIntent()
            try {
                this@MainScreenActivity.startActivityForResult(
                    intent,
                    REQUEST_SELECT_FILE
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this@MainScreenActivity,
                    "Cannot open file chooser",
                    Toast.LENGTH_LONG
                ).show()
                uploadMessage = null

                return false
            }
            return true
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return
            uploadMessage?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            uploadMessage = null
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    //WebViewClient setup
    internal class MyWebViewClient(val progressBar: ProgressBar?) : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            view.loadUrl(request.url.toString())
            progressBar?.context?.let { saveUrl(it, request.url.toString()) }
            return true
        }

        // Для старых устройств
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }

        private fun saveUrl(context: Context, url: String) {
            context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(URL_KEY, url)
                .apply()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            progressBar?.visibility = View.GONE
        }
    }


}

