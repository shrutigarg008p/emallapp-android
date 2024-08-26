package com.emall.net.activity.loginSignUp.changePassword

import android.os.Bundle
import android.text.*
import android.view.View
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.loginSignUp.otp.LoginViaOTP
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.activity_otp_verify.*
import kotlin.collections.set

class VerifyOTPForChangedPassword : BaseActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel;
    private var mobileNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                ApiClient()
                    .storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        intent.let { mobileNumber = intent.getStringExtra("value") }

        otp_edit_text_1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    otp_edit_text_2.requestFocus()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }
        })

        otp_edit_text_2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    otp_edit_text_3.requestFocus()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }
        })

        otp_edit_text_3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    otp_edit_text_4.requestFocus()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }
        })
        verify_otp_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_btn -> {
                openActivity(LoginViaOTP::class.java)
                this.finish()
            }
            R.id.verify_otp_btn -> verifyOTPForPasswordChange()
        }
    }

    private fun verifyOTPForPasswordChange() {

        showProgressDialog()
        val numberParam: HashMap<String, String> = HashMap()
        numberParam["mobile"] = "+$mobileNumber"
        numberParam["otp"] = otp_edit_text_1.text.toString() + otp_edit_text_2.text.toString() + otp_edit_text_3.text.toString() + otp_edit_text_4.text.toString()
        numberParam["application"] = "1"

        viewModel.verifyPasswordByMobileOTP(numberParam)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            openActivity(CreateNewPasswordActivity::class.java){
                                putString("value", "$mobileNumber")
                                putString("customerId", it.data?.customerId.toString())
                            }
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            constraint_layout.snack(it.message!!)
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }
}