package com.emall.net

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.LocaleHelper
import com.emall.net.utils.PreferenceHelper
import java.util.*

class EmallApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context) {
        PreferenceHelper.init(base)
        if (PreferenceHelper.readString(APP_LOCALE) == "") {
            Log.e("language", Locale.getDefault().language)
            PreferenceHelper.writeString(APP_LOCALE, Locale.getDefault().language)
        }

        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /*GoSellSDK.init(this, Constants.TAP_PAYMENT_SECRET_STAGING_KEY, BuildConfig.APPLICATION_ID)
        GoSellSDK.setLocale("en")*/
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}