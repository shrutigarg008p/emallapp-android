package com.emall.net.activity.loginSignUp.changePassword

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.network.api.*
import com.emall.net.utils.Status.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.activity_create_new_password.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CreateNewPasswordActivity : BaseActivity(), View.OnClickListener {

    private var mobileNumber: String? = ""
    private var customerId: String? = ""
    private lateinit var viewModel: MainViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_password)

        intent.let {
            mobileNumber = intent.getStringExtra("value")
            customerId = intent.getStringExtra("customerId")
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
        reset_password_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.reset_password_btn -> createNewPassword()
        }
    }

    private fun createNewPassword() {

        showProgressDialog()
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()

        jsonObject.apply {
            put("customer_id", customerId)
            put("newpassword", new_password.text.toString())

        }

        jsonObject1.put("param", jsonObject)
        val jsonObjectString = jsonObject1.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        viewModel.createNewPassword(requestBody)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        SUCCESS -> {
                            hideProgressDialog()
                            openActivity(LoginActivity::class.java)
                        }
                        ERROR -> {
                            hideProgressDialog()
                            linear_layout.snack(it.message!!)
                        }
                        LOADING -> {
                        }
                    }
                }
            })
    }
}