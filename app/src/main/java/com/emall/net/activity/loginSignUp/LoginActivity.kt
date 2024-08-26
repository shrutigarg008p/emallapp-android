package com.emall.net.activity.loginSignUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.activity.loginSignUp.changePassword.ResetPassword
import com.emall.net.activity.loginSignUp.otp.LoginViaOTP
import com.emall.net.activity.loginSignUp.signup.SignUpActivity
import com.emall.net.network.api.*
import com.emall.net.network.model.customerToken.*
import com.emall.net.network.model.ecommerceLogin.LoginRequestParams
import com.emall.net.network.model.ecommerceLoginSignUp.*
import com.emall.net.utils.*
import com.emall.net.utils.Constants.COOKIES
import com.emall.net.utils.Constants.CUSTOMER_EMAIL
import com.emall.net.utils.Constants.CUSTOMER_PHONE_NUMBER
import com.emall.net.utils.Constants.CUSTOMER_TOKEN
import com.emall.net.utils.Constants.HOME_FRAGMENT
import com.emall.net.utils.Constants.SELLER
import com.emall.net.utils.Constants.STORAGE_PERMISSION
import com.emall.net.utils.Constants.USER_NAME
import com.emall.net.utils.Utility.afterTextChanged
import com.emall.net.utils.Utility.checkEmail
import com.emall.net.utils.Utility.checkPerm
import com.emall.net.utils.Utility.clear
import com.emall.net.utils.Utility.hideKeyboard
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.requestAllPermissions
import com.emall.net.utils.Utility.shouldRequestPermissionRationale
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.item_list.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.*
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var eCommerceViewModel: MainViewModel
    private lateinit var reCommerceViewModel: MainViewModel

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var isLoginWithEmailSelected = true

    private var countryDetailData = ArrayList<GetCountryDetailData>()
    private var countryListName = ArrayList<String>()
    private var flagUrlList = ArrayList<String>()
    private var cellCodeList = ArrayList<String>()
    private var type: String? = ""
    private var cellCode: String? = ""
    private var mobileNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (PreferenceHelper.readString(Constants.APP_LOCALE) == Constants.ENGLISH_LOCALE) {
           // setContentView(R.layout.activity_login)
            image_view_2.setBackgroundResource(R.drawable.ic_emallicon)
        } else {
          //  setContentView(R.layout.activity_login_ar)
            image_view_2.setBackgroundResource(R.drawable.ic_emallicon_ar)
        }
    }

    override fun onStart() {
        super.onStart()
        scrollView.hideKeyboard()
        when {
            checkPerm(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ->
                requestStoragePermission()
        }
        clearTextFields()
        setUpGoogleLogIn()
        setUpViewModel()
        back_btn.setOnClickListener(this)
        login_btn.setOnClickListener(this::onClick)
        register_btn.setOnClickListener(this::onClick)
        login_via_otp_btn.setOnClickListener(this::onClick)
        login_via_mobile.setOnClickListener(this::onClick)
        reset_password_btn.setOnClickListener(this::onClick)
        sign_in_button.setOnClickListener(this::onClick)

        username_edit_text.afterTextChanged {
            if (!checkEmail(it)) {
                username_edit_text.error = resources.getString(R.string.please_enter_your_name)
                username_edit_text.requestFocus()
            }
        }

        password_edit_text.password.afterTextChanged {
            if (it.isEmpty()) password_edit_text.password.error = getString(R.string.enter_password)
            if (it.length < 8) password_edit_text.password.error = getString(R.string.password_is_too_short)
            password_edit_text.password.requestFocus()
        }

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

    private fun requestStoragePermission() {
        when {
            shouldRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ->
                requestAllPermissions(permissions, STORAGE_PERMISSION)
            else -> requestAllPermissions(permissions, STORAGE_PERMISSION)
        }
    }

    private fun clearTextFields() {
        username_edit_text.clear()
        password_edit_text.password.clear()
    }

    private fun setUpGoogleLogIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
    }

    private fun setUpViewModel() {
        viewModelStore.clear()
        eCommerceViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        getCountryDetail()
    }

    private fun getCountryDetail() {
        eCommerceViewModel.getCountryDetail(Utility.getLanguage())
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { it ->
                                countryDetailData = it.data
                                countryListName.clear()
                                flagUrlList.clear()
                                for (e in resource.data.data) {
                                    countryListName.add(e.name + " " + "(" + e.cel_code + ")")
                                    flagUrlList.add(e.flag)
                                    cellCodeList.add(e.cel_code.toString())
                                }
                            }
                            showCountriesToList()
                        }
                        Status.ERROR -> {
                            scrollView.snack("Error fetching country code")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun showCountriesToList() {
        if (country_code_spinner != null) {
            val adapter = ArrayAdapter(this@LoginActivity, R.layout.spinner_item, countryListName)
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

                    cellCode = cellCodeList[position]
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Request for camera permission.
        when (requestCode) {
            STORAGE_PERMISSION -> when {
                grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                }
                else -> {
                    // Permission request was denied.
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_btn -> openActivity(SellerActivity::class.java)
            R.id.login_btn -> {
                type = "email"
                if (username_edit_text.text.isNullOrEmpty() && password_edit_text.password.text.isNullOrEmpty()) {
                    scrollView.snack("Please enter the valid credentials")
                } else {
                    if (mobile_number.text.toString().isNotBlank()) {
                        mobileNumber = mobile_number.text.toString()
                        type = "mobile"
                    }
                    eCommerceLogin(
                        username_edit_text.text.toString(),
                        mobileNumber,
                        type!!,
                        password_edit_text.password.text.toString(),
                        cellCode!!, ""
                    )

                }
            }
            R.id.register_btn -> openActivity(SignUpActivity::class.java)
            R.id.login_via_otp_btn -> openActivity(LoginViaOTP::class.java)
            R.id.reset_password_btn -> openActivity(ResetPassword::class.java)
            R.id.login_via_mobile -> {
                when {
                    isLoginWithEmailSelected -> {
                        login_via_mobile.text = getString(R.string.login_via_email)
                        username_edit_text.visibility = View.GONE
                        username_text.visibility = View.GONE
                        mobile_text_view.visibility = View.VISIBLE
                        mobile_frame_layout.visibility = View.VISIBLE
                        isLoginWithEmailSelected = false
                        type = getString(R.string.mobile).toLowerCase(Locale.getDefault())
                    }
                    else -> {
                        login_via_mobile.text = getString(R.string.login_via_mobile)
                        username_edit_text.visibility = View.VISIBLE
                        username_text.visibility = View.VISIBLE
                        mobile_text_view.visibility = View.GONE
                        mobile_frame_layout.visibility = View.GONE
                        isLoginWithEmailSelected = true
                        type = getString(R.string.email).toLowerCase(Locale.getDefault())
                    }
                }
            }
            R.id.sign_in_button -> {
                startActivityForResult(mGoogleSignInClient!!.signInIntent, 101)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            101 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                        try {
                            val result = task.result!!
                            doSignUp(result.givenName, result.familyName, result.email, result.id)
                        } catch (e: ApiException) {
                            Log.e("TAG", "signInResult:failed code=" + e.statusCode)
                        }
                    }
                }
            }
        }
    }

    private fun doSignUp(firstName: String?, lastName: String?, email: String?, id: String?) {

        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()

        jsonObject.apply {
            put("username", "$firstName $lastName")
            put("email", email)
            put("mobilenumber", "")
            put("type", "google")
            put("password", "")
            put("account_id", id)
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
                            hideProgressDialog()
                            eCommerceLogin(email!!, "", "google", "", "", id!!)
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            scrollView.snack("User Already Exists.")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun eCommerceLogin(
        email: String,
        mobileNumber: String,
        type: String,
        password: String,
        mobileCode: String,
        accountId: String,
    ) {

        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()
        val number = when {
            mobileNumber.isNotEmpty() -> "+$mobileCode$mobileNumber"
            else -> ""
        }
        jsonObject.apply {
            put("email", email)
            put("mobile_number", number)
            // put("mask_key", "")
            put("mask_key",PreferenceHelper.readString(Constants.GUEST_MASK_KEY))
            put("cart_id", "")
            put("type", type)
            put("password", password)
            put("account_id", accountId)
            put("device_id", "android")
        }
        jsonObject1.put("param", jsonObject)
        val jsonObjectString = jsonObject1.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        showProgressDialog()

        val apiInterface = ApiInterface.buildService(ApiService::class.java)
        val call = apiInterface.getEcommerceLogin(Utility.getLanguage(), requestBody)

        call.enqueue(object : Callback<LoginDataResponse> {
            override fun onResponse(
                call: Call<LoginDataResponse>?,
                response: Response<LoginDataResponse>?
            ) {
                hideProgressDialog()
                val res = response?.body()!!
                if (res.response.equals("success", true)) {
                    PreferenceHelper.writeString(
                        USER_NAME,
                        """${res.data[0].fname} ${res.data[0].lname}"""
                    )
                    PreferenceHelper.writeInt(
                        Constants.CUSTOMER_ID,
                        res.data[0].id.toInt()
                    )
                    PreferenceHelper.writeString(CUSTOMER_EMAIL, res.data[0].email)
                    PreferenceHelper.writeString(
                        CUSTOMER_PHONE_NUMBER,
                        res.data[0].mob
                    )

                    when {
                        response.headers()["Set-Cookie"]?.isNotEmpty()!! -> {
                            val cookie = response.headers()["Set-Cookie"]
                            PreferenceHelper.writeString(COOKIES, cookie!!)
                        }
                    }
                    getCustomerToken(
                        res.data[0].email,
                        password_edit_text.password.editableText.toString(), number
                    )
                } else {
                    scrollView.snack("Invalid Credentials")
                }
            }

            override fun onFailure(call: Call<LoginDataResponse>?, t: Throwable?) {

            }
        })
        /*eCommerceViewModel.getEcommerceLogin(requestBody, Utility.getLanguage())
            .observe(this, Observer { it ->
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val preferences =
                                PreferenceManager.getDefaultSharedPreferences(this).getStringSet(
                                    PREF_COOKIES, HashSet<String>()
                                )

                            PreferenceManager.getDefaultSharedPreferences(this).getStringSet(
                                SAVED_COOKIES, HashSet<String>()
                            )
                            Log.d("Preferences", "eCommerceLogin: cookies = " + preferences.toString())

                            hideProgressDialog()



                            if (it.data.response.equals("success", true)) {
                                PreferenceHelper.writeString(
                                    USER_NAME,
                                    """${it.data?.data!![0].fname} ${it.data.data[0].lname}"""
                                )
                                PreferenceHelper.writeInt(
                                    Constants.CUSTOMER_ID,
                                    it.data.data[0].id.toInt()
                                )
                                PreferenceHelper.writeString(CUSTOMER_EMAIL, it.data.data[0].email)
                                PreferenceHelper.writeString(
                                    CUSTOMER_PHONE_NUMBER,
                                    it.data.data[0].mob
                                )
                                getCustomerToken(
                                    it.data.data[0].email,
                                    password_edit_text.password.editableText.toString(), number
                                )
                            } else {
                                scrollView.snack("Invalid Credentials")
                            }
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            scrollView.snack("Invalid credentials")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })*/
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
                            loginForSellerEvaluator(email)
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


    private fun loginForSellerEvaluator(email: String) {
        val userInfo = LoginRequestParams(
            email,
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
        PreferenceHelper.writeBoolean(HOME_FRAGMENT, true)
        hideProgressDialog()
        return when {
            PreferenceHelper.readString(Constants.LOGIN_TYPE)!!
                .contentEquals(SELLER) -> {
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

