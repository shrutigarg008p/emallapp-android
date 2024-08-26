package com.emall.net.fragment.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.BottomSheetCountryAdapter
import com.emall.net.adapter.SettingsAdapter
import com.emall.net.fragment.staticPages.AboutUsFragment
import com.emall.net.fragment.staticPages.HelpAndSupportFragment
import com.emall.net.fragment.staticPages.PrivacyPolicyFragment
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.SettingsItemList
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.network.model.getCurrencyResponse.ExchangeRate
import com.emall.net.utils.Constants
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.Constants.CURRENCY_RATE
import com.emall.net.utils.Constants.ENGLISH_LOCALE
import com.emall.net.utils.Constants.SELECTED_COUNTRY
import com.emall.net.utils.Constants.SELECTED_COUNTRY_FLAG
import com.emall.net.utils.Constants.SELECTED_COUNTRY_NAME
import com.emall.net.utils.Constants.SELECTED_CURRENCY
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


class SettingsFragment : Fragment(), ItemClick, OnItemClick, View.OnClickListener {
    private lateinit var countryRecyclerView: RecyclerView
    private lateinit var viewModel: MainViewModel
    private var countryDetail = ArrayList<GetCountryDetailData>()
    private var currencyCodes = ArrayList<ExchangeRate>()
    private var settingsItemList = ArrayList<SettingsItemList>()
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var bottomSheetCountryAdapter: BottomSheetCountryAdapter
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private lateinit var sheetViewCountry: View
    private lateinit var sheetViewLanguage: View
    private lateinit var confirmCountry: TextView
    private lateinit var confirmLanguage: TextView
    private lateinit var englishLanguage: RadioButton
    private lateinit var arabLanguage: RadioButton
    private var confirmButtonClicked: Boolean = false
    private var confirmCountryButtonClicked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.settings))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.settings))
            }
        }
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUp()
    }

    private fun setUp() {
        setUpSettingsItemData()
        if (currencyCodes.isEmpty()) {
            getCountryDetails()
        }
        sheetViewCountry = layoutInflater.inflate(R.layout.bottom_sheet_dialog_country, null)
        confirmCountry = sheetViewCountry.findViewById(R.id.confirm_country)
        countryRecyclerView = sheetViewCountry.findViewById(R.id.country_recyclerview)
        countryRecyclerView.layoutManager = LinearLayoutManager(activity)
        mBottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        sheetViewLanguage = layoutInflater.inflate(R.layout.bottom_sheet_dialog_language, null)
        arabLanguage = sheetViewLanguage.findViewById(R.id.arab_radio_btn)
        englishLanguage = sheetViewLanguage.findViewById(R.id.english_radio_btn)
        confirmLanguage = sheetViewLanguage.findViewById(R.id.confirm_btn)
        arabLanguage.setOnClickListener(this)
        englishLanguage.setOnClickListener(this)
        confirmLanguage.setOnClickListener(this)
        confirmCountry.setOnClickListener(this)
        if (PreferenceHelper.readString(APP_LOCALE) == ENGLISH_LOCALE) {
            englishLanguage.isChecked = true
        } else {
            arabLanguage.isChecked = true
        }
    }

    private fun setUpSettingsItemData() {
        settingsItemList.clear()
        settingsItemList.add(SettingsItemList(R.drawable.notification_icon,
            getString(R.string.notifications),
            "", "notification"))
        settingsItemList.add(SettingsItemList(R.drawable.language_icon,
            getString(R.string.change_language),
            getString(R.string.english_or_arabic), "language"))
        settingsItemList.add(SettingsItemList(R.drawable.country_flag_icon,
            getString(R.string.change_country),
            getString(R.string.current_selection), "country"))
        when (activity) {
            is SellerActivity -> {
                settingsItemList.add(SettingsItemList(R.drawable.help_and_support_icon,
                    getString(R.string.help_support),
                    getString(R.string.contact_shipping_return_faq), "help"))
            }
        }
        settingsItemList.add(SettingsItemList(R.drawable.emall_icon,
            getString(R.string.emall),
            getString(R.string.learn_more_about_us), "aboutUs"))
        settingsItemList.add(SettingsItemList(R.drawable.follow_us_icon,
            getString(R.string.follow_us_on),
            "", "followUs"))

        when (activity) {
            is SellerActivity -> {
                settingsItemList.add(SettingsItemList(R.drawable.privacy_policy_icon,
                    getString(R.string.privacy_policy),
                    "", "privacy"))
            }
        }
        setting_recycler_view.setHasFixedSize(true)
        setting_recycler_view.layoutManager = LinearLayoutManager(activity)
        settingsAdapter = SettingsAdapter(settingsItemList, this)
        setting_recycler_view.adapter = settingsAdapter
        settingsAdapter.notifyDataSetChanged()
    }

    private fun getCountryDetails() {
        ViewUtils.showProgressBar(requireActivity())
        val locale = if (PreferenceHelper.readString(APP_LOCALE) == ENGLISH_LOCALE) {
            "en"
        } else {
            "ar"
        }
        viewModel.getCountryDetail(locale)
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
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
                            activity?.let { ViewUtils.hideProgressBar() }
                            it.message?.let { it1 -> Log.e("error", it1) }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun setCountryData() {
        if (countryDetail.size != 0) {
            for (item in countryDetail) {
                if (PreferenceHelper.readString(SELECTED_COUNTRY)!!.equals(item.code, true)) {
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
                    PreferenceHelper.writeString(SELECTED_COUNTRY_NAME, item.name)
                    PreferenceHelper.writeString(SELECTED_COUNTRY_FLAG,
                        "https://staging.emall.net" + item.flag)
                    settingsAdapter.notifyItemChanged(2)
                    if (confirmButtonClicked) {
                        Toast.makeText(requireContext(),
                            R.string.language_changed_successfully,
                            Toast.LENGTH_SHORT).show()
                        PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, true)
                        confirmButtonClicked = false
                        recreateActivity()
                    } else if (confirmCountryButtonClicked) {
                        Toast.makeText(requireContext(),
                            R.string.country_changed_successfully,
                            Toast.LENGTH_SHORT).show()
                    }
                    break

                }
            }
        }
    }


    private fun getCurrencyCodes() {
        viewModel.getCurrencyCodes().observe(viewLifecycleOwner, { it ->
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        setCountryData()
                        currencyCodes.clear()
                        currencyCodes = it.data!!.exchange_rates
                    }
                    Status.ERROR -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    override fun itemClick(position: Int) {
        PreferenceHelper.writeString(SELECTED_COUNTRY, countryDetail[position].code)
    }

    override fun onItemClick(position: Int, type: String) {
        when {
            type.equals("notification", true) -> {
                // notification switch
            }
            type.equals("language", true) -> {
                mBottomSheetDialog!!.setContentView(sheetViewLanguage)
                mBottomSheetDialog!!.show()
            }
            type.equals("country", true) -> {
                mBottomSheetDialog!!.setContentView(sheetViewCountry)
                mBottomSheetDialog!!.show()
            }
            type.equals("help", true) -> {
                openHelpAndSupportFragment()
            }
            type.equals("aboutUs", true) -> {
                openAboutUsFragment()
            }
//            follow us
            type.equals("facebook", true) -> {
                openFaceBook()
            }
            type.equals("twitter", true) -> {
                openTwitter()
            }
            type.equals("instagram", true) -> {
                openInstagram()
            }
//
            type.equals("privacy", true) -> {
                openPrivacyPolicyFragment()
            }
        }
    }

    private fun openFaceBook() {
//        val uri = Uri.parse("fb://page/1883727135173361")
        val uri = Uri.parse("fb://page/emall.net")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.setPackage("com.facebook.katana")
        try {
            requireContext().startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            requireContext().startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/emall.net")))
        }
    }

    private fun openTwitter() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=858678197977829376"))
//            Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=Prikaps5"))
        intent.setPackage("com.twitter.android")
        try {
            requireContext().startActivity(intent)
        } catch (e: Exception) {
            // no Twitter app, revert to browser
            requireContext().startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/emall_net")))
        }
    }

    private fun openInstagram() {
        val uri = Uri.parse("http://instagram.com/_u/emall_net")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.setPackage("com.instagram.android")
        try {
            requireContext().startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            requireContext().startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/emall_net/")))
        }
    }

    private fun openHelpAndSupportFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(HelpAndSupportFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(HelpAndSupportFragment(),
                R.id.container)
        }
    }

    private fun openAboutUsFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(AboutUsFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(AboutUsFragment(),
                R.id.container)
        }
    }

    private fun openPrivacyPolicyFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(PrivacyPolicyFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(PrivacyPolicyFragment(),
                R.id.container)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.english_radio_btn ->
                PreferenceHelper.writeString(APP_LOCALE, ENGLISH_LOCALE)
            R.id.arab_radio_btn -> PreferenceHelper.writeString(APP_LOCALE,
                Constants.ARAB_LOCALE)
            R.id.confirm_country -> {
                mBottomSheetDialog!!.dismiss()
                confirmCountryButtonClicked = true
                setCountryData()
            }
            R.id.confirm_btn -> {
                mBottomSheetDialog!!.dismiss()
                confirmButtonClicked = true
                getCountryDetails()
            }
        }
    }

    private fun recreateActivity() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).finish()
                (activity as SellerActivity).overridePendingTransition(0, 0)
                startActivity((activity as SellerActivity).intent)
                (activity as SellerActivity).overridePendingTransition(0, 0)
                (activity as SellerActivity).finish()
            }
            else -> {
                (activity as BuyerActivity).finish()
                (activity as BuyerActivity).overridePendingTransition(0, 0)
                startActivity((activity as BuyerActivity).intent)
                (activity as BuyerActivity).overridePendingTransition(0, 0)
                (activity as BuyerActivity).finish()
            }
        }
    }

}