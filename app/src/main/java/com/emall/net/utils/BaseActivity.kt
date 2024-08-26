package com.emall.net.utils

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.emall.net.activity.IntroSliderActivity
import com.emall.net.activity.dashboard.*
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.Constants.IS_NEW_DEVICE
import com.emall.net.utils.Utility.openActivity
import com.emall.net.viewmodel.*
import kotlinx.coroutines.*

open class BaseActivity : AppCompatActivity() {

    internal val name = BaseActivity::class.java.simpleName

    private val job = Job()
    internal val scopeMainThread = CoroutineScope(job + Dispatchers.Main)

    private val progressDialog = CustomProgressDialog()
    private val progressBar = ProgressBar()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(base!!))
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLocale(this@BaseActivity, PreferenceHelper.readString(APP_LOCALE)!!)
    }

    open suspend fun openNextActivity() {
        delay(2000)
        when {
            !PreferenceHelper.readBoolean(Constants.FIRST_TIME_USER)!! -> {
                PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, false)
                openActivity(IntroSliderActivity::class.java)
            }
            else -> {
                PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, true)
                PreferenceHelper.writeBoolean(IS_NEW_DEVICE,true)
                when {
                    PreferenceHelper.readInt(Constants.CUSTOMER_ID) != 0 ->
                        when {
                            PreferenceHelper.readString(Constants.LOGIN_TYPE)!!
                                .equals(Constants.SELLER, true) ->
                                openActivity(SellerActivity::class.java)
                            else -> openActivity(BuyerActivity::class.java)
                        }
                    else -> openActivity(SellerActivity::class.java)
                }
            }
        }
        finish()
    }

    open suspend fun getSellerEvaluatorToken(): String = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)!!

    open suspend fun getECommerceToken(): String = PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!

    open suspend fun getReCommerceViewModel(): MainViewModel = ViewModelProviders.of(
        this,
        ViewModelFactory(
            ApiClient().apiClient().create(ApiService::class.java)
        )
    )
        .get(MainViewModel::class.java)

    open suspend fun getECommerceViewModel() : MainViewModel = ViewModelProviders.of(
        this,
        ViewModelFactory(
            ApiClient().storeUrlApiClient()
                .create(ApiService::class.java)
        )
    )
        .get(MainViewModel::class.java)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    open fun showProgressDialog() {
        progressDialog.show(this)
    }

    open fun hideProgressDialog() {
        progressDialog.dialog.dismiss()
    }

    open fun showProgressBar() {
        progressBar.show(this)
    }

    open fun hideProgressBar() {
        progressBar.dialog.dismiss()
    }

}