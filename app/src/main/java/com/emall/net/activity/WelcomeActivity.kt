package com.emall.net.activity

import android.os.Bundle
import android.util.Log
import com.emall.net.R
import com.emall.net.utils.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.*

class WelcomeActivity : BaseActivity() {
    private lateinit var viewModel: MainViewModel
    private val TAG = WelcomeActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        scopeMainThread.launch { viewModel = getECommerceViewModel() }

        /**
         *  1 coroutine scope -> viewmodel
         *  api hit ->
         *  2 non-coroutine scope -> api hit possible
         */

        continueclick.setOnClickListener { openActivity(NewUsedDevicesActivity::class.java) }
    }

    override fun onStart() {
        super.onStart()
        scopeMainThread.launch {
            getGuestToken()
        }
    }

    private fun getGuestToken() {
        viewModel.getGuestToken().observe(this, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(
                                Constants.GUEST_MASK_KEY,
                                resource.data?.mask_key.toString()
                            )
                            PreferenceHelper.writeInt(
                                Constants.GUEST_QUOTE_ID,
                                resource.data?.quote_id!!
                            )
                        }
                        Status.ERROR -> {
                            Log.d(TAG, "error: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            Log.d(TAG, "loading: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
        }
}