package com.emall.net.activity.loginSignUp.otp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.*
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.ecommerceLogin.LoginRequestParams
import com.emall.net.network.model.ecommerceLoginSignUp.LoginDataResponse
import com.emall.net.utils.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_otp_verify.*
import kotlinx.android.synthetic.main.activity_otp_verify.back_btn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.*
import kotlin.collections.set

class LoginViaOTPVerify : BaseActivity(), View.OnClickListener {

    private lateinit var eCommerceViewModel: MainViewModel
    private lateinit var reCommerceViewModel: MainViewModel
    private var mobileNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        intent.let {
            mobileNumber = intent.getStringExtra("value")
        }
        viewModelStore.clear()
        eCommerceViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        verify_otp_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)

        otp_edit_text_1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    otp_edit_text_2.requestFocus()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int,
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
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int,
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
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int,
            ) {
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.verify_otp_btn -> verifyLoginViaOTP()
            R.id.back_btn -> {
                openActivity(LoginViaOTP::class.java)
                this.finish()
            }
        }
    }

    private fun verifyLoginViaOTP() {
        showProgressDialog()
        val numberParam: HashMap<String, String> = HashMap()
        numberParam["mobile"] = "$mobileNumber"
        numberParam["otp"] =
            otp_edit_text_1.text.toString() + otp_edit_text_2.text.toString() + otp_edit_text_3.text.toString() + otp_edit_text_4.text.toString()
        numberParam["application"] = "1"

        eCommerceViewModel.verifyLoginViaOTP(numberParam)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> eCommerceLogin()
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

    private fun eCommerceLogin() {

        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()

        jsonObject.apply {
            put("email", " ")
            put("mobile_number", "$mobileNumber")
            put("mask_key", " ")
            put("cart_id", " ")
            put("type", "otp")
            put("password", " ")
            put("account_id", " ")
            put("device_id", " ")
        }
        jsonObject1.put("param", jsonObject)

        val jsonObjectString = jsonObject1.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val apiInterface = ApiInterface.buildService(ApiService::class.java)
        val call = apiInterface.getEcommerceLogin(Utility.getLanguage(),requestBody)

        call.enqueue(object : Callback<LoginDataResponse> {
            override fun onResponse(
                call: Call<LoginDataResponse>?,
                response: Response<LoginDataResponse>?
            ) {
                hideProgressDialog()
                val res = response?.body()!!
                PreferenceHelper.writeString(
                    Constants.USER_NAME,
                    """${res.data[0].fname} ${res.data[0].lname}"""
                )
                if (res.response.equals("success", true)) {
                    PreferenceHelper.writeInt(Constants.CUSTOMER_ID,
                        res.data[0].id.toInt())
                    PreferenceHelper.writeString(Constants.CUSTOMER_EMAIL,
                        res.data[0].email)
                    PreferenceHelper.writeString(Constants.CUSTOMER_PHONE_NUMBER, res.data[0].mob)
                    getCustomerToken(
                        res.data[0].email,
                        "$mobileNumber"
                    )
                } else {
                    constraint_layout.snack("Invalid Credentials")
                }

            }

            override fun onFailure(call: Call<LoginDataResponse>?, t: Throwable?) {
                hideProgressDialog()
                constraint_layout.snack(t!!.message!!)
            }
        })
    }

    private fun getCustomerToken(email: String, mobileNumber: String) {
        val userInfo = CustomerTokenParams(param = CustomerParams(email, mobileNumber, ""))
        eCommerceViewModel.getCustomerToken(userInfo, Utility.getLanguage()).observe(this,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(
                                Constants.CUSTOMER_TOKEN,
                                resource.data.toString()
                            )
                            viewModelStore.clear()

                            reCommerceViewModel = ViewModelProviders.of(
                                this,
                                ViewModelFactory(
                                    ApiClient().apiClient()
                                        .create(ApiService::class.java)
                                )
                            )
                                .get(MainViewModel::class.java)
                            loginForSellerEvaluator(email)
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            constraint_layout.snack(it.message.toString())
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }


    private fun loginForSellerEvaluator(email: String) {
        val userInfo = LoginRequestParams(
            email,
            PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!
        )

        reCommerceViewModel.getRecommerceLogin(userInfo, Utility.getLanguage())
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            constraint_layout.snack(getString(R.string.login_success))
                            // SAVE LOGIN TYPE TO NAVIGATE TO SELLER (AUCTION USER)  OR BUYER ( EVALUATION USER) SCREEN
                            PreferenceHelper.writeString(
                                Constants.LOGIN_TYPE,
                                resource.data?.DATA?.user?.type!!
                            )
                            // SAVE ACCESS TOKEN FOR SELLER/EVALUATOR FOR PERFORMING FURTHER API CALLS
                            PreferenceHelper.writeString(
                                Constants.SELLER_EVALUATOR_TOKEN,
                                resource.data.DATA.access_token
                            )
                            // save login details for get countries api
                            // NAVIGATE TO DASHBOARD
                            openDashboard()
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            when {
                                !Utility.hasInternet(this) -> constraint_layout.snack(getString(R.string.no_internet))
                                else -> constraint_layout.snack(getString(R.string.invalid_credentials))
                            }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun openDashboard() {
        viewModelStore.clear()
        PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, true)
        hideProgressDialog()
        constraint_layout.snack(getString(R.string.sign_up_successfully))
        return when {
            PreferenceHelper.readString(Constants.LOGIN_TYPE)!!
                .contentEquals(Constants.SELLER) -> {
                openActivity(
                    SellerActivity::class.java
                )
                finish()
            }
            else -> {
                openActivity(BuyerActivity::class.java)
                finish()
            }

        }
    }
}
