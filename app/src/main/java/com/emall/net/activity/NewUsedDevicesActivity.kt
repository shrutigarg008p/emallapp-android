package com.emall.net.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.BottomSheetCountryAdapter
import com.emall.net.listeners.*
import com.emall.net.network.api.*
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.network.model.getCurrencyResponse.ExchangeRate
import com.emall.net.utils.*
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.Constants.ARAB_LOCALE
import com.emall.net.utils.Constants.CURRENCY_RATE
import com.emall.net.utils.Constants.ENGLISH_LOCALE
import com.emall.net.utils.Constants.HOME_FRAGMENT
import com.emall.net.utils.Constants.SELECTED_COUNTRY
import com.emall.net.utils.Constants.SELECTED_COUNTRY_FLAG
import com.emall.net.utils.Constants.SELECTED_COUNTRY_NAME
import com.emall.net.utils.Constants.SELECTED_CURRENCY
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_new_used_devices.*
import java.util.*

class NewUsedDevicesActivity : BaseActivity(), View.OnClickListener, ItemClick {

    private lateinit var confirmCountry: TextView
    private lateinit var confirmLanguage: TextView
    private lateinit var englishLanguage: RadioButton
    private lateinit var arabLanguage: RadioButton
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private lateinit var sheetViewCountry: View
    private lateinit var sheetViewLanguage: View
    private lateinit var viewModel: MainViewModel
    private var currencyCodes = ArrayList<ExchangeRate>()
    private lateinit var countryRecyclerView: RecyclerView
    private var countryDetail = ArrayList<GetCountryDetailData>()
    private lateinit var bottomSheetCountryAdapter: BottomSheetCountryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_used_devices)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        mBottomSheetDialog =
            BottomSheetDialog(this@NewUsedDevicesActivity, R.style.AppBottomSheetDialogTheme)

        sheetViewCountry =
            this@NewUsedDevicesActivity.layoutInflater.inflate(
                R.layout.bottom_sheet_dialog_country,
                null
            )
        sheetViewLanguage =
            this@NewUsedDevicesActivity.layoutInflater.inflate(
                R.layout.bottom_sheet_dialog_language,
                null
            )
        countryRecyclerView = sheetViewCountry.findViewById(R.id.country_recyclerview)
        countryRecyclerView.layoutManager = LinearLayoutManager(this@NewUsedDevicesActivity)
        confirmCountry = sheetViewCountry.findViewById(R.id.confirm_country)

        arabLanguage = sheetViewLanguage.findViewById(R.id.arab_radio_btn)
        englishLanguage = sheetViewLanguage.findViewById(R.id.english_radio_btn)
        confirmLanguage = sheetViewLanguage.findViewById(R.id.confirm_btn)

        arabLanguage.setOnClickListener(this)
        englishLanguage.setOnClickListener(this)

        confirmLanguage.setOnClickListener(this)
        confirmCountry.setOnClickListener(this)

        new_click.setOnClickListener(this)
        old_click.setOnClickListener(this)
        getCountryDetails()
    }

    private fun getCountryDetails() {
        showProgressDialog()
        viewModel.getCountryDetail(Utility.getLanguage())
            .observe(this, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { it ->
                                countryDetail.clear()
                                countryDetail = it.data
                                bottomSheetCountryAdapter =
                                    BottomSheetCountryAdapter(countryDetail, this)
                                countryRecyclerView.adapter = bottomSheetCountryAdapter
                                bottomSheetCountryAdapter.notifyDataSetChanged()
                                getCurrencyCodes()
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

    private fun getCurrencyCodes() {
        viewModel.getCurrencyCodes().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        currencyCodes.clear()
                        currencyCodes = it.data!!.exchange_rates
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.old_click -> {
                PreferenceHelper.writeBoolean(Constants.IS_NEW_DEVICE, false)
                mBottomSheetDialog!!.setContentView(sheetViewCountry)
                mBottomSheetDialog!!.show()
            }
            R.id.new_click -> {
                PreferenceHelper.writeBoolean(Constants.IS_NEW_DEVICE, true)
                mBottomSheetDialog!!.setContentView(sheetViewCountry)
                mBottomSheetDialog!!.show()
            }
            R.id.english_radio_btn -> PreferenceHelper.writeString(APP_LOCALE, ENGLISH_LOCALE)
            R.id.arab_radio_btn -> PreferenceHelper.writeString(APP_LOCALE, ARAB_LOCALE)
            R.id.confirm_country -> {
                mBottomSheetDialog!!.dismiss()
                mBottomSheetDialog!!.setContentView(sheetViewLanguage)
                mBottomSheetDialog!!.show()
            }
            R.id.confirm_btn -> {
                setCurrencyAsPerLocale()
                mBottomSheetDialog!!.dismiss()
                finishAffinity()
                PreferenceHelper.writeBoolean(HOME_FRAGMENT, true)
                openActivity(SellerActivity::class.java)
            }
        }
    }

    private fun setCurrencyAsPerLocale() {
        when {
            PreferenceHelper.readString(SELECTED_COUNTRY)!!.toString()
                .isEmpty() -> {
                if (Locale.getDefault().toString().equals(ENGLISH_LOCALE, true)
                ) {
                    PreferenceHelper.writeString(SELECTED_COUNTRY, "IN")
                    setCurrency()
                } else {
                    PreferenceHelper.writeString(SELECTED_COUNTRY, "SA")
                    setCurrency()
                }
            }
            else -> {
                setCurrency()
            }
        }
    }

    private fun setCurrency() {
        if (countryDetail.size != 0) {
            for (item in countryDetail) {
                if (PreferenceHelper.readString(SELECTED_COUNTRY)!!.equals(item.code, true)) {
                    PreferenceHelper.writeString(SELECTED_COUNTRY_NAME, item.name)
                    if (currencyCodes.size != 0) {
                        for (data in currencyCodes) {
                            if (item.currency.equals(data.currency_to, true)) {
                                PreferenceHelper.writeString(SELECTED_CURRENCY, data.currency_to)
                                PreferenceHelper.writeFloat(CURRENCY_RATE, data.rate)
                                break
                            } else if (item.currency.equals("INR", true)) {
                                PreferenceHelper.writeString(SELECTED_CURRENCY, "USD")
                                PreferenceHelper.writeFloat(CURRENCY_RATE, 1.0f)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    override fun itemClick(position: Int) {
        PreferenceHelper.writeString(SELECTED_COUNTRY, countryDetail[position].code)
        PreferenceHelper.writeString(SELECTED_COUNTRY_FLAG,
            "https://staging.emall.net" + countryDetail[position].flag)
        PreferenceHelper.writeString(SELECTED_COUNTRY_NAME, countryDetail[position].name)
    }
}