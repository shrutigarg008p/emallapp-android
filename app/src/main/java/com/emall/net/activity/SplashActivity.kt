package com.emall.net.activity

import android.os.Bundle
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_background)
        scopeMainThread.launch {

            openNextActivity()
        }
    }
}