package com.emall.net.activity.loginSignUp.otp

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.*
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.utils.BaseActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login_via_otp.*

class LoginViaOTP : BaseActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel

    private var countrylistname = ArrayList<String>()
    private var flagUrlList = ArrayList<String>()

    private var cellCodeList = ArrayList<String>()
    private var cellCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_via_otp)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        generate_otp_btn.setOnClickListener(this)
        back_btn.setOnClickListener(this)

        getCountryDetail()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.generate_otp_btn -> {
                if (mobile_number.text.toString().isNotBlank() &&
                    (mobile_number.text.toString().length == 10 || mobile_number.text.toString().length == 9)
                ) generateOTP("+$cellCode${mobile_number.text.toString()}")
                else linear_layout.snack("Please enter valid mobile number ")
            }
            R.id.back_btn -> {
                openActivity(LoginActivity::class.java)
                this.finish()
            }
        }
    }

    private fun getCountryDetail() {
        showProgressDialog()
        viewModel.getCountryDetail(Utility.getLanguage()).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let {
                            countrylistname.clear()
                            flagUrlList.clear()
                            for (e in resource.data.data) {
                                countrylistname.add(e.name + " " + "(" + e.cel_code + ")")
                                flagUrlList.add(e.flag)
                                cellCodeList.add(e.cel_code.toString())

                            }
                        }
                        addCountriesListToSpinner()
                        hideProgressDialog()
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                        linear_layout.snack("Unable to fetch country codes")
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun addCountriesListToSpinner() {
        if (countrycodeSpinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, countrylistname
            )
            countrycodeSpinner.adapter = adapter
            adapter.setDropDownViewResource(R.layout.checkedradiotextviewenglish)

            countrycodeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Picasso.get().load("https://staging.emall.net" + flagUrlList[position])
                        .into(flagImage)
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

    private fun generateOTP(number: String) {

        showProgressDialog()
        val numberParam: HashMap<String, String> = HashMap()
        numberParam["mobile"] = number
        numberParam["resend"] = "0"
        numberParam["application"] = "1"
        viewModel.generateOTP(numberParam)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {

                        Status.SUCCESS -> {
                            hideProgressDialog()
                            /* openActivity(VerifyOtp::class.java) {
                                 putString("mobile", number)
                             }*/

                            openActivity(LoginViaOTPVerify::class.java) {
                                putString("value", number)
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
}