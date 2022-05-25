package com.gamelacky.luckyjet.lucky.app

import android.app.Application
import android.content.Context
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class App : Application() {

    companion object {
        private var appContext: Context? = null

        fun getApplicationContext(): Context = appContext!!
        private const val ONESIGNAL_APP_ID = "c1aa2cec-0826-4c14-abf3-02f86c134122"

    }

    override fun onCreate() {
        super.onCreate()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        val config = YandexMetricaConfig.newConfigBuilder("506eddd8-d36e-4c2e-b930-8ff971095b3f").build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)
        appContext = applicationContext
    }
}