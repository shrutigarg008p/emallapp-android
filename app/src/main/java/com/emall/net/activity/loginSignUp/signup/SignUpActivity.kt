package com.emall.net.activity.loginSignUp.signup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.dashboard.*
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.fragment.staticPages.MyBrowser
import com.emall.net.network.api.*
import com.emall.net.network.model.customerToken.*
import com.emall.net.network.model.ecommerceLogin.LoginRequestParams
import com.emall.net.network.model.ecommerceLoginSignUp.LoginDataResponse
import com.emall.net.utils.*
import com.emall.net.utils.Constants.CUSTOMER_TOKEN
import com.emall.net.utils.Utility.afterTextChanged
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.utils.ViewUtils.checkTextViewValidation
import com.emall.net.utils.ViewUtils.viewFinal
import com.emall.net.viewmodel.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_buyer.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.back_btn
import kotlinx.android.synthetic.main.activity_sign_up.country_code_spinner
import kotlinx.android.synthetic.main.activity_sign_up.flag_image
import kotlinx.android.synthetic.main.activity_sign_up.mobile_number
import kotlinx.android.synthetic.main.activity_sign_up.password
import kotlinx.android.synthetic.main.activity_sign_up.scrollView
import kotlinx.android.synthetic.main.fragment_privacy_policy.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.*

class SignUpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var eCommerceViewModel: MainViewModel
    private lateinit var reCommerceViewModel: MainViewModel
    private lateinit var viewModel: MainViewModel

    private var cellCode: String? = ""
    private var isAllConditionFulfilled = false
    private var countryListName = ArrayList<String>()
    private var flagUrlList = ArrayList<String>()
    private var cellcodelist = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        if (PreferenceHelper.readString(Constants.APP_LOCALE) == Constants.ENGLISH_LOCALE) {
            // setContentView(R.layout.activity_login)
            imageView2.setBackgroundResource(R.drawable.ic_emallicon)
        } else {
            //  setContentView(R.layout.activity_login_ar)
            imageView2.setBackgroundResource(R.drawable.ic_emallicon_ar)
        }

        /*checkbox click*/
        scopeMainThread.launch { viewModel = getECommerceViewModel() }
        check_box.setOnClickListener { view ->
            if (view is CheckBox) {
                if (view.isChecked)
                    getTermConditionContent()
               // else
                   // getTermConditionContent()
            }
        }

        sign_up_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)

        viewModelStore.clear()
        eCommerceViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        getCountryDetail()

        mobile_number.afterTextChanged {
            if (cellCode.equals(
                    "91",
                    true
                ) && (mobile_number.length() > 10 || mobile_number.length() < 10)
            ) {
                mobile_number.error = resources.getString(R.string.please_enter_mobile_number)
                mobile_number.requestFocus()
            } else if (!cellCode.equals(
                    "91",
                    true
                ) && (mobile_number.length() > 9 || mobile_number.length() < 9)
            ) {
                mobile_number.error = resources.getString(R.string.please_enter_mobile_number)
                mobile_number.requestFocus()
            }
        }
    }

    private fun getCountryDetail() {
        showProgressDialog()
        eCommerceViewModel.getCountryDetail(Utility.getLanguage()).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.data.let {
                            hideProgressDialog()
                            countryListName.clear()
                            flagUrlList.clear()
                            for (e in resource.data?.data!!) {
                                countryListName.add(e.name + " " + "(" + e.cel_code + ")")
                                flagUrlList.add(e.flag)
                                cellcodelist.add(e.cel_code.toString())
                            }
                        }
                        initSpinner()
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                        scrollView.snack("Error while fetching countries. Please check your internet connection")
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun initSpinner() {
        if (country_code_spinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, countryListName
            )
            country_code_spinner.adapter = adapter
            adapter.setDropDownViewResource(R.layout.checkedradiotextviewenglish)

            country_code_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long,
                ) {
                    Picasso.get()
                        .load("https://staging.emall.net" + flagUrlList[position])
                        .into(flag_image)

                    cellCode = cellcodelist[position]
                    if (cellCode.equals("91", true)) {
                        mobile_number.filters = arrayOf(InputFilter.LengthFilter(10))
                    } else {
                        mobile_number.filters = arrayOf(InputFilter.LengthFilter(9))
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_up_btn -> {
                (checkTextViewValidation(
                    findViewById(R.id.username),
                    getString(R.string.enter_username)
                ) &&
                        checkTextViewValidation(
                            findViewById(R.id.password),
                            getString(R.string.enter_password)
                        )
                        && checkTextViewValidation(
                    findViewById(R.id.email),
                    getString(R.string.enter_email)
                )
                        && checkTextViewValidation(
                    findViewById(R.id.mobile_number),
                    getString(R.string.enter_mobile)
                )).also { isAllConditionFulfilled = it }
                when {
                    isAllConditionFulfilled -> createAccount(
                        username.text.toString(),
                        password.text.toString(),
                        email.text.toString(),
                        mobile_number.text.toString()
                    )
                    else -> when {
                        viewFinal!! != null -> {
                            viewFinal!!.requestFocus()
                            return
                        }
                    }
                }
            }
            R.id.back_btn -> {
                openActivity(LoginActivity::class.java)
                this.finish()
            }
        }
    }

    private fun createAccount(
        username: String,
        password: String,
        email: String,
        number: String
    ) {
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()
        val number2 = "$cellCode$number".toString().toLong()
        jsonObject.apply {
            put("username", username)
            put("email", email)
            put("mobilenumber", "+$number2")
            put("type", "email")
            put("password", password)
            put("account_id", "")
            put("device_used", "android")
        }
        jsonObject1.put("param", jsonObject)

        val jsonObjectString = jsonObject1.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        showProgressDialog()

        eCommerceViewModel.createAccount(requestBody, Utility.getLanguage())
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            it.data?.let { it ->
                                if (it.response.equals("success", true)) {
                                    eCommerceLogin(
                                        email,
                                        number,
                                        password,
                                        cellCode!!,
                                        resource.data?.data?.entity_id.toString()
                                    )
                                } else {
                                    hideProgressDialog()
                                    scrollView.snack(it.message!!)
                                }
                            }

                        }

                        Status.ERROR -> {
                            hideProgressDialog()
                            scrollView.snack(it.message!!)
                        }

                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun eCommerceLogin(
        userEmail: String,
        mobileNumber: String,
        userPassword: String,
        mobileCode: String,
        accountId: String,
    ) {
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()
        val number = when {
            mobileNumber.isNotEmpty() -> mobileCode + mobileNumber
            else -> ""
        }
        jsonObject.apply {
            put("email", userEmail)
            put("mobile_number", "+$number")
            //put("mask_key", "")
            put("mask_key",PreferenceHelper.readString(Constants.GUEST_MASK_KEY))
            put("cart_id", "")
            put("type", "email")
            put("password", userPassword)
            put("account_id", "")
            put("device_id", accountId)
        }
        jsonObject1.put("param", jsonObject)
        val jsonObjectString = jsonObject1.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val apiInterface = ApiInterface.buildService(ApiService::class.java)
        val call = apiInterface.getEcommerceLogin(Utility.getLanguage(), requestBody)

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
                    PreferenceHelper.writeInt(Constants.CUSTOMER_ID, res.data[0].id.toInt())
                    PreferenceHelper.writeString(Constants.CUSTOMER_EMAIL, res.data[0].email)
                    PreferenceHelper.writeString(Constants.CUSTOMER_PHONE_NUMBER, res.data[0].mob)
                    getCustomerToken(
                        userEmail, userPassword, "+$number"
                    )
                } else {
                    scrollView.snack("Invalid Credentials")
                }
            }

            override fun onFailure(call: Call<LoginDataResponse>?, t: Throwable?) {
                hideProgressDialog()
                scrollView.snack(t!!.message!!)
            }
        })
    }

    private fun getCustomerToken(email: String, password: String, mobileNumber: String) {
        val userInfo = CustomerTokenParams(param = CustomerParams(email, mobileNumber, password))
        eCommerceViewModel.getCustomerToken(userInfo, Utility.getLanguage()).observe(this,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(CUSTOMER_TOKEN, resource.data.toString())
                            viewModelStore.clear()

                            reCommerceViewModel = ViewModelProviders.of(
                                this,
                                ViewModelFactory(
                                    ApiClient().apiClient()
                                        .create(ApiService::class.java)
                                )
                            )
                                .get(MainViewModel::class.java)
                            loginForSellerEvaluator()
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            scrollView.snack(it.message.toString())
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }


    private fun loginForSellerEvaluator() {
        val userInfo = LoginRequestParams(
            email.text.toString(),
            PreferenceHelper.readString(CUSTOMER_TOKEN)!!
        )

        reCommerceViewModel.getRecommerceLogin(userInfo, Utility.getLanguage())
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            scrollView.snack(getString(R.string.login_success))
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
                            scrollView.snack(it.message!!)
                            when {
                                !Utility.hasInternet(this) -> scrollView.snack(getString(R.string.no_internet))
                                else -> scrollView.snack(getString(R.string.invalid_credentials))
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
        scrollView.snack(getString(R.string.sign_up_successfully))
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


    /*open Terms and condition dialog*/
    private fun getTermConditionContent() {
        viewModel.getStaticPageData(
            "terms-conditions",
            Utility.getLanguage()
        )
            .observe(
                this,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                resource.data?.let {
                                    showAlertDialog(it.data)
                                }
                            }
                            Status.ERROR -> {
                                it.message?.let { it1 -> Log.e("error", it1) }
                                hideProgressDialog()
                            }
                            Status.LOADING -> {
                                showProgressDialog()
                            }
                        }
                    }
                })
    }

    private fun showAlertDialog(data: String ) {

        val customDialog = Dialog(this@SignUpActivity)
        customDialog.setContentView(R.layout.dialog_terms_condition)
        customDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val termCondition = customDialog.findViewById(R.id.terms_condition_web_view) as WebView
        val backButton = customDialog.findViewById(R.id.iv_back) as ImageView

        termCondition.webViewClient = MyBrowser(this@SignUpActivity)
        termCondition.settings.javaScriptEnabled = true

        termCondition.loadDataWithBaseURL(null,
            data,
            "text/html",
            "UTF-8",
            null)

        backButton.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

}