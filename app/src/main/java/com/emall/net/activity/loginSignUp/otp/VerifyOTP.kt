package com.emall.net.activity.loginSignUp.otp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.activity_otp_verify.*

class VerifyOTP : BaseActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel;
    private var mobileNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        mobileNumber = intent?.getStringExtra("mobile")
        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                ApiClient()
                    .storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        verify_otp_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.verify_otp_btn -> callVerifyOTPApi()
            R.id.back_btn -> {
                openActivity(LoginViaOTP::class.java)
                this.finish()
            }
        }
    }

    private fun callVerifyOTPApi() {
        viewModel.verifyOTP(
            mobileNumber!!,
            otp_edit_text_1.toString() + otp_edit_text_2.toString() + otp_edit_text_3.toString() + otp_edit_text_4.toString()
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(
                                Constants.CUSTOMER_TOKEN,
                                resource.data.toString()
                            )

                            // login flow add

                            openActivity(SellerActivity::class.java)
                        }
                        Status.ERROR -> {
                            constraint_layout.snack(it.message!!)
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }
}