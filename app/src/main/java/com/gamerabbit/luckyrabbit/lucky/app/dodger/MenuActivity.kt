package com.gamerabbit.luckyrabbit.lucky.app.dodger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.gamerabbit.luckyrabbit.lucky.app.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun openPolitics(view: View){
        val url = "https://www.google.com/"
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().setColorScheme(
            CustomTabsIntent.COLOR_SCHEME_DARK).build()
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }


    fun starGame(view: View){
        val intent = Intent(this@MenuActivity, MainActivity::class.java)
        startActivity(intent)
    }
}