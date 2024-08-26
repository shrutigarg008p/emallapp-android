package com.emall.net.activity.loginSignUp.changePassword

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Utility.afterTextChanged
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.reset_password_activity.*
import kotlinx.android.synthetic.main.reset_password_activity.back_btn
import kotlinx.android.synthetic.main.reset_password_activity.linear_layout
import kotlinx.android.synthetic.main.reset_password_activity.reset_password_btn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ResetPassword : BaseActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel;
//    private var countrylistname = ArrayList<String>()
//    private var flagUrlList = ArrayList<String>()
//    private var type: String? = " "
//    private var cellCodeList = ArrayList<String>()
//    private var cellCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_activity)
    }

    override fun onStart() {
        super.onStart()

        reset_password_btn.setOnClickListener(this)
//        email_btn.setOnClickListener(this)
//        mobile_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

//        getCountryDetail()

       /* mobile_number_edit_text.afterTextChanged {
            if (cellCode.equals(
                    "91",
                    true
                ) && (mobile_number_edit_text.length() > 10 || mobile_number_edit_text.length() < 10)
            ) {
                mobile_number_edit_text.error = "Please enter the valid mobile number"
                mobile_number_edit_text.requestFocus()
            } else if (!cellCode.equals(
                    "91",
                    true
                ) && (mobile_number_edit_text.length() > 9 || mobile_number_edit_text.length() < 9)
            ) {
                mobile_number_edit_text.error = "Please enter the valid mobile number"
                mobile_number_edit_text.requestFocus()
            } 
        }
*/
        email_edit_text.afterTextChanged {
            if (!Utility.checkEmail(it)) {
                email_edit_text.error = "Please enter correct username"
                email_edit_text.requestFocus()
            }
        }
    }

   /* private fun getCountryDetail() {
        showProgressDialog()
        viewModel.getCountryDetail(Utility.getLanguage()).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let {
                            for (e in resource.data.data) {
                                countrylistname.add(e.name + " " + "(" + e.cel_code + ")")
                                flagUrlList.add(e.flag)
                                cellCodeList.add(e.cel_code.toString())
                            }
                        }
                        setCountriesDataToList()
                        hideProgressDialog()
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                        linear_layout.snack(it.message!!)
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun setCountriesDataToList() {
        if (country_code_spinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, countrylistname
            )
            country_code_spinner.adapter = adapter
            adapter.setDropDownViewResource(R.layout.checkedradiotextviewenglish)

            country_code_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    Picasso.get().load("https://staging.emall.net" + flagUrlList[position])
                        .into(flag_image)

                    cellCode = cellCodeList[position]
                    if (cellCode.equals("91", true)) {
                        mobile_number_edit_text.filters = arrayOf(InputFilter.LengthFilter(10))
                    } else {
                        mobile_number_edit_text.filters = arrayOf(InputFilter.LengthFilter(9))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }
*/
    override fun onClick(v: View) {
        when (v.id) {
            R.id.reset_password_btn -> generateOTP()
//            R.id.email_btn -> setEmailLayout()
//            R.id.mobile_btn -> setMobileLayout()
            R.id.back_btn -> {
                openActivity(LoginActivity::class.java)
                this.finish()
            }
        }
    }

    private fun generateOTP() =
        when {
            email_text_view.text.toString().isNotEmpty() -> changePasswordByEmail()/*when (type) {
                getString(R.string.email).toLowerCase(Locale.getDefault()) ->
                else -> changePasswordByMobile(mobile_number_edit_text.text.toString())
            }*/
            else -> Toast.makeText(
                applicationContext,
                getString(R.string.enter_mobile_number),
                Toast.LENGTH_SHORT
            ).show()
        }

   /* private fun setEmailLayout() {
        email_text_view.visibility = View.VISIBLE
        email_edit_text.visibility = View.VISIBLE
        // frame_layout.visibility = View.GONE
        //mobile_number_text.visibility = View.GONE

        email_btn.background =
            ContextCompat.getDrawable(this, R.drawable.solid_white_background)
        email_btn.setTextColor(Color.parseColor("#FFFFFF"))
        mobile_btn.background =
            ContextCompat.getDrawable(this, R.drawable.white_solid_corner)
        mobile_btn.setTextColor(Color.parseColor("#000000"))
        type = getString(R.string.email).toLowerCase(Locale.getDefault())
    }

    private fun setMobileLayout() {
        email_text_view.visibility = View.GONE
        email_edit_text.visibility = View.GONE
        frame_layout.visibility = View.VISIBLE
        mobile_number_text_view.visibility = View.VISIBLE

        mobile_btn.background =
            ContextCompat.getDrawable(this, R.drawable.solid_white_background)
        mobile_btn.setTextColor(Color.parseColor("#FFFFFF"))
        email_btn.background = ContextCompat.getDrawable(this, R.drawable.white_solid_corner)
        email_btn.setTextColor(Color.parseColor("#000000"))

        type = getString(R.string.mobile).toLowerCase(Locale.getDefault())
    }*/

    private fun changePasswordByEmail() {
        showProgressDialog()        
        val jsonObject = JSONObject()
        val jsonObject1 = JSONObject()

        jsonObject.apply {
            put("email", email_edit_text.text.toString())
            put("mobile", "")
            put("type", "email")

        }
        jsonObject1.put("param", jsonObject)
        val jsonObjectString = jsonObject1.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        viewModel.changePasswordByEmail(requestBody)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            it.data?.let { it ->
                                hideProgressDialog()
                                if(it.response.equals("success",true)) {
                                    openActivity(CreateNewPasswordActivity::class.java) {
                                        putString(
                                            "value",
                                            ""
                                        )
                                        putString("customerId", it.data.customer_id)
                                    }
                                }else{
                                    linear_layout.snack(it.message)
                                }
                            }

                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            linear_layout.snack(it.message!!)
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    /*private fun changePasswordByMobile(mobileNumber: String) {
        showProgressDialog()
        val numberParam: HashMap<String, String> = HashMap()
        numberParam["mobile"] = "+${cellCode.toString()}$mobileNumber"
        numberParam["resend"] = "0"
        numberParam["application"] = "1"

        viewModel.generateOTP(numberParam)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            openActivity(VerifyOTPForChangedPassword::class.java) {
                                putString("value", "${cellCode.toString()}$mobileNumber")
                            }
                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            linear_layout.snack(it.message!!)
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }*/
}