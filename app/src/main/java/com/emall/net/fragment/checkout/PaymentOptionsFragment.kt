package com.emall.net.fragment.checkout

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.fragment.other.OrderPlaceFragment
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.cartRetainRequest.CartRetainParams
import com.emall.net.network.model.cartRetainRequest.CartRetainParamsData
import com.emall.net.network.model.codRequest.CodParams
import com.emall.net.network.model.codRequest.Param
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.sendTapPaymentRequest.SendTapPaymentParams
import com.emall.net.network.model.sendTapPaymentRequest.TapPaymentParam
import com.emall.net.network.model.setPaymentMethodRequest.BillingAddress
import com.emall.net.network.model.setPaymentMethodRequest.PaymentMethod
import com.emall.net.network.model.setPaymentMethodRequest.SetPaymentMethodParams
import com.emall.net.network.model.stcPaymentOtpRequest.StcOtpParam
import com.emall.net.network.model.stcPaymentOtpRequest.StcPaymentOtpRequestParams
import com.emall.net.network.model.verifyStcPaymentOtpRequest.VerifyStcOtpParam
import com.emall.net.network.model.verifyStcPaymentOtpRequest.VerifyStcPaymentOtpParams
import com.emall.net.network.model.verifyTapPaymentRequest.TapPaymentParamData
import com.emall.net.network.model.verifyTapPaymentRequest.VerifyTapPaymentParams
import com.emall.net.utils.*
import com.emall.net.utils.Constants.COOKIES
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.Authorize
import company.tap.gosellapi.internal.api.models.Charge
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.internal.api.models.Token
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.controllers.ThemeObject
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.AppearanceMode
import company.tap.gosellapi.open.enums.CardType
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.Customer.CustomerBuilder
import company.tap.gosellapi.open.models.Receipt
import company.tap.gosellapi.open.models.TapCurrency
import kotlinx.android.synthetic.main.fragment_payment_options.*
import java.math.BigDecimal


class PaymentOptionsFragment : Fragment() , SessionDelegate {
    private var grandTotal: Int = 0
    private lateinit var viewModel: MainViewModel
    private var orderId: String = ""
    private var countryCode: String = "+966"
    private var incrementOrderId: String = ""
    private var paymentMethod: String = ""
    private var OtpReference: String = ""
    private var STCPayPmtReference: String = ""
    private lateinit var alertDialog: AlertDialog
    lateinit var sdkSession: SDKSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) grandTotal = arguments?.getInt("grandTotal")!!
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUpData()
    }

    private fun setUpData() {

        startSDK()


        grand_total_value.text =
            "${getString(R.string.grand_total)}  ${PreferenceHelper.readString(Constants.SELECTED_CURRENCY)} $grandTotal"
        if (PreferenceHelper.readString(Constants.SELECTED_ADDRESS_COUNTRY_ID) == "SA") {
            stc_layout.makeVisible()
        } else {
            stc_layout.makeGone()
        }
        card_layout.setOnClickListener(View.OnClickListener {
            card_radio_selector.setImageResource(R.drawable.ic_yellow_check)
            stc_radio_selector.setImageResource(R.drawable.grey_circle)
            cod_radio_selector.setImageResource(R.drawable.grey_circle)
            paymentMethod = "tap"
            stc_mobile_layout.makeGone()
        })
        stc_layout.setOnClickListener(View.OnClickListener {
            card_radio_selector.setImageResource(R.drawable.grey_circle)
            stc_radio_selector.setImageResource(R.drawable.ic_yellow_check)
            cod_radio_selector.setImageResource(R.drawable.grey_circle)
            paymentMethod = "stcpay"
            stc_mobile_layout.makeVisible()
        })
        cod_layout.setOnClickListener(View.OnClickListener {
            card_radio_selector.setImageResource(R.drawable.grey_circle)
            stc_radio_selector.setImageResource(R.drawable.grey_circle)
            cod_radio_selector.setImageResource(R.drawable.ic_yellow_check)
            paymentMethod = "cashondelivery"
            stc_mobile_layout.makeGone()
        })
        place_order_button.setOnClickListener(View.OnClickListener {
           if (paymentMethod != "") {
                setPaymentMethod(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
            } else {
                Toast.makeText(requireContext(),
                    R.string.please_select_payment_method,
                    Toast.LENGTH_SHORT).show()
            }
            //startSDK()
            //sdkSession.start(activity)

        })
    }

    private fun startSDK() {
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        configureApp()

        /**
         * Optional step
         * Here you can configure your app theme (Look and Feel).
         */
        configureSDKThemeObject()

        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession()

        /**
         * Required step.
         * Choose between different SDK modes
         */
        configureSDKMode()

        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
        initPayButton()
    }

    private fun configureSDKSession() {

        // Instantiate SDK Session
        sdkSession = SDKSession()   //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow


        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this)

        // initiate PaymentDataSource

        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource() //** Required **


        // set transaction currency associated to your account

        // set transaction currency associated to your account
        sdkSession.setTransactionCurrency(TapCurrency("SAR")) //** Required **


        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession.setCustomer(getCustomer()) //** Required **


        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(BigDecimal(grandTotal)) //** Required **


        // Set Payment Items array list

        // Set Payment Items array list
        sdkSession.setPaymentItems(java.util.ArrayList()) // ** Optional ** you can pass empty array list


        sdkSession.setPaymentType("ALL") //** Merchant can pass paymentType


        // Set Taxes array list

        // Set Taxes array list
        sdkSession.setTaxes(java.util.ArrayList()) //** Optional ** you can pass empty array list


        // Set Shipping array list

        // Set Shipping array list
        sdkSession.setShipping(java.util.ArrayList()) //** Optional ** you can pass empty array list


        // Post URL

        // Post URL
        sdkSession.setPostURL(null) //** Optional **


        // Payment Description

        // Payment Description
        sdkSession.setPaymentDescription("") //** Optional **


        // Payment Extra Info

        // Payment Extra Info
        sdkSession.setPaymentMetadata(HashMap()) //** Optional ** you can pass empty array hash map


        // Payment Reference

        // Payment Reference
        sdkSession.setPaymentReference(null) //** Optional ** you can pass null


        // Payment Statement Descriptor

        // Payment Statement Descriptor
        sdkSession.setPaymentStatementDescriptor("") //** Optional **


        // Enable or Disable Saving Card

        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true) //** Required ** you can pass boolean


        // Enable or Disable 3DSecure

        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]

        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(
            Receipt(
                false,
                false
            )
        ) // ** Optional ** you can pass Receipt object or null


        // Set Authorize Action

        // Set Authorize Action
        sdkSession.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null


        sdkSession.setDestination(null) // ** Optional ** you can pass Destinations object or null


        sdkSession.setMerchantID(incrementOrderId) // ** Optional ** you can pass merchant id or null


        sdkSession.setCardType(CardType.ALL)

    }

    private fun getCustomer(): Customer? { // test customer id cus_Kh1b4220191939i1KP2506448
        // cus_TS060420211633j3KO1606527

        return CustomerBuilder(null).email("abc@abc.com").firstName("firstname")
            .lastName("lastname").metadata("")
            .phone(PhoneNumber("+66","123456789"))
            .middleName("middlename").build()
    }

    private fun configureSDKMode() {

        /**
         * You have to choose only one Mode of the following modes:
         * Note:-
         *      - In case of using PayButton, then don't call sdkSession.start(this)  because the SDK will start when user clicks the tap pay button.
         */
        /**
         * Start using  SDK features through SDK main activity (With Tap CARD FORM)
         */
        startSDKUI()

    }

    private fun startSDKUI() {
        if (sdkSession != null) {
            val trx_mode =
                TransactionMode.PURCHASE
            // set transaction mode [TransactionMode.PURCHASE - TransactionMode.AUTHORIZE_CAPTURE - TransactionMode.SAVE_CARD - TransactionMode.TOKENIZE_CARD ]
            sdkSession.transactionMode = trx_mode //** Required **
            // if you are not using tap button then start SDK using the following call
            //sdkSession.start(activity)
        }
    }

    private fun configureSDKThemeObject() {
        ThemeObject.getInstance()
            .setAppearanceMode(AppearanceMode.WINDOWED_MODE)
            .setSdkLanguage("en")
            // .setHeaderFont(Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf"))
            .setHeaderTextColor(resources.getColor(R.color.black1))
            .setHeaderTextSize(17)
            .setHeaderBackgroundColor(resources.getColor(R.color.french_gray_new))
            // .setCardInputFont(Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf"))
            .setCardInputTextColor(resources.getColor(R.color.black))
            .setCardInputInvalidTextColor(resources.getColor(R.color.red))
            .setCardInputPlaceholderTextColor(resources.getColor(R.color.gray))
            .setSaveCardSwitchOffThumbTint(resources.getColor(R.color.french_gray_new))
            .setSaveCardSwitchOnThumbTint(resources.getColor(R.color.vibrant_green))
            .setSaveCardSwitchOffTrackTint(resources.getColor(R.color.french_gray))
            .setSaveCardSwitchOnTrackTint(resources.getColor(R.color.vibrant_green_pressed))
            .setScanIconDrawable(resources.getDrawable(R.drawable.btn_card_scanner_normal))
            .setPayButtonResourceId(R.drawable.btn_pay_selector) //btn_pay_merchant_selector
            // .setPayButtonFont(Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf"))
            .setPayButtonDisabledTitleColor(resources.getColor(R.color.white))
            .setPayButtonEnabledTitleColor(resources.getColor(R.color.white))
            .setPayButtonTextSize(14)
            .setPayButtonLoaderVisible(true)
            .setPayButtonSecurityIconVisible(true)
            .setPayButtonText("PAY WITH TAP PAY") // **Optional**
            // setup dialog textcolor and textsize
            .setDialogTextColor(resources.getColor(R.color.black1)).dialogTextSize =
            17 // **Optional**
    }

    private fun initPayButton() {

       // var payButtonView = view?.findViewById(R.id.payButtonId) as PayButtonView

       // var payButtonView = view?.findViewById(R.id.place_order_button) as Button

        /*if (ThemeObject.getInstance().payButtonFont != null) payButtonView.setupFontTypeFace(
            ThemeObject.getInstance().payButtonFont
        )
        if (ThemeObject.getInstance().payButtonDisabledTitleColor != 0 && ThemeObject.getInstance().payButtonEnabledTitleColor != 0) payButtonView.setupTextColor(
            ThemeObject.getInstance().payButtonEnabledTitleColor,
            ThemeObject.getInstance().payButtonDisabledTitleColor
        )
        if (ThemeObject.getInstance().payButtonTextSize != 0) payButtonView.payButton.textSize =
            ThemeObject.getInstance().payButtonTextSize.toFloat()
        if (ThemeObject.getInstance().isPayButtSecurityIconVisible) payButtonView.securityIconView.visibility =
            if (ThemeObject.getInstance().isPayButtSecurityIconVisible) View.VISIBLE else View.INVISIBLE
        if (ThemeObject.getInstance().payButtonResourceId != 0) payButtonView.setBackgroundSelector(
            ThemeObject.getInstance().payButtonResourceId
        )*/

    /*    if (sdkSession != null) {
            val trx_mode = sdkSession.transactionMode
            if (trx_mode != null) {
                if (TransactionMode.SAVE_CARD == trx_mode) {
                    payButtonView.payButton.text =
                        getString(company.tap.gosellapi.R.string.save_card)
                } else if (TransactionMode.TOKENIZE_CARD == trx_mode) {
                    payButtonView.payButton.text =
                        getString(company.tap.gosellapi.R.string.tokenize)
                } else {
                    payButtonView.payButton.text = getString(company.tap.gosellapi.R.string.pay)
                }
            } else {
                configureSDKMode()
            }

        }*/
       // sdkSession.setButtonView(place_order_button, activity, Companion.SDK_REQUEST_CODE)
    }

    private fun configureApp() {
        GoSellSDK.init(context, "sk_test_0vpqyXdxl7LhGzWbZ6kracmi", "com.emall.net")
        GoSellSDK.setLocale("EN")
    }

    private fun setPaymentMethod(token: String) {
        activity?.let { ViewUtils.showProgressBar(it) }
        val streetInfo = ArrayList<String>()
        streetInfo.add(PreferenceHelper.readString(Constants.SELECTED_ADDRESS_STREET).toString())
        val setPaymentMethodParams = SetPaymentMethodParams(
            paymentMethod = PaymentMethod(
                paymentMethod
            ),
            billing_address = BillingAddress(
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_COUNTRY).toString(),
                1,
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_COUNTRY_ID).toString(),
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_COUNTRY_ID).toString(),
                street = streetInfo,
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_POSTCODE).toString(),
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_CITY).toString(),
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_FIRST_NAME).toString(),
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_LAST_NAME).toString(),
                PreferenceHelper.readString(Constants.CUSTOMER_EMAIL).toString(),
                PreferenceHelper.readString(Constants.SELECTED_ADDRESS_PHONE_NUMBER).toString(),
            )
        )
        viewModel.setPaymentMethod(
            "Bearer $token",
            PreferenceHelper.readString(COOKIES)!!,
            setPaymentMethodParams, Utility.getLanguage()
        )
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            orderId = resource.data.toString()
                            codPayment(orderId)
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

    private fun codPayment(orderId: String) {
        val codParams = CodParams(
            param = Param(
                orderId
            )
        )
        viewModel.codPayment(codParams, PreferenceHelper.readString(COOKIES)!!)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            incrementOrderId = resource.data?.increment_id.toString()
                            if (paymentMethod.equals("cashondelivery", true)) {
                                openThankYouPage()
                            } else if (paymentMethod.equals("stcpay", true)) {
                                sendOtpForStcPayment()
                            } else {
                                //sendTapPayment()
                                ViewUtils.hideProgressBar()
                                sdkSession.start(activity)
                            }
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

    private fun sendTapPayment() {
        val sendTapPaymentParams = SendTapPaymentParams(
            param = TapPaymentParam(
                incrementOrderId,
                grandTotal.toString()
            )
        )
        viewModel.sendTapPayment(sendTapPaymentParams)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let { it1 ->
                                tap_payment_webview.makeVisible()
                                Log.e("tap_payment_webview", "makeVisible()" + it1.redirect_link)
                                tap_payment_webview.webViewClient =
                                    MyWebViewClient(requireActivity())
                                tap_payment_webview.invalidate()
                                tap_payment_webview.settings.setSupportZoom(true)
                                tap_payment_webview.settings.javaScriptEnabled = true

                                tap_payment_webview.loadUrl(it1.redirect_link)

//                                Handler().postDelayed({
//                                    Log.e("tap payment", "webview close")
//                                    tap_payment_webview.destroy()
//                                    quoteId(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
//                                }, 2000)
//
//                                val timer = object : CountDownTimer(5000, 1000) {
//                                    override fun onTick(millisUntilFinished: Long) {
//                                        Log.e("onTick", "1000")
//                                    }
//
//                                    override fun onFinish() {
//                                        Log.e("onFinish", "5000")
//                                    }
//                                }
//                                timer.start()
                                //verifyTapPayment()
                            }

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

    private fun verifyTapPayment( orderId: String, chargeId:String, sourceId:String) {
        val verifyTapPaymentParams = VerifyTapPaymentParams(
            param = TapPaymentParamData(
                incrementOrderId,chargeId,sourceId
            )
        )
        viewModel.verifyTapPayment(verifyTapPaymentParams)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            openThankYouPage()
                           /* if (resource.data?.msg.equals("failed", true)) {
                                Handler().postDelayed({ verifyTapPayment() }, 500)
                            } else {
                                tap_payment_webview.destroy()
                                openThankYouPage()
                            }*/
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

    private fun sendOtpForStcPayment() {
        val stcPaymentOtpRequestParams = StcPaymentOtpRequestParams(
            param = StcOtpParam(
                incrementOrderId,
                grandTotal.toString(),
                countryCode + stc_mobile_number.text.toString()
            )
        )
        viewModel.stcPaymentgetOtp(
            stcPaymentOtpRequestParams,
            PreferenceHelper.readString(COOKIES)!!
        )
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            OtpReference = resource.data?.OtpReference.toString()
                            STCPayPmtReference = resource.data?.STCPayPmtReference.toString()
                            Log.d("showStcOtpDialog()", "showStcOtpDialog()")
                            showStcOtpDialog()
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

    private fun showStcOtpDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.otp_dialog,
                payment_option_container.findViewById(R.id.content),
                false
            )
        builder.setView(view)
        val stcOtpValue = view.findViewById(R.id.stcBody) as AppCompatEditText
        builder.setCancelable(false)
        val okBtn = view.findViewById(R.id.otpLayout3) as ConstraintLayout
        val cancelBtn = view.findViewById(R.id.otpLayout1) as ConstraintLayout
        alertDialog = builder.create()
        alertDialog.show()
        okBtn.setOnClickListener {
            verifyOtp(stcOtpValue.text.toString())
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun verifyOtp(otpValue: String) {
        activity?.let { ViewUtils.showProgressBar(it) }
        val verifyStcPaymentOtpParams = VerifyStcPaymentOtpParams(
            param = VerifyStcOtpParam(
                OtpReference,
                otpValue,
                STCPayPmtReference,
                incrementOrderId,
                "android"
            )
        )
        viewModel.verifyStcPaymentOtp(
            verifyStcPaymentOtpParams,
            PreferenceHelper.readString(COOKIES)!!
        )
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            alertDialog.dismiss()
                            if (resource.data?.msg.equals("success")) {
                                openThankYouPage()
                            } else {
                                if (PreferenceHelper.readString(Constants.CUSTOMER_TOKEN) != "") {
                                    getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
                                } else {
                                    getCustomerToken(
                                        PreferenceHelper.readString(Constants.CUSTOMER_EMAIL)!!,
                                        PreferenceHelper.readString(
                                            Constants.CUSTOMER_PHONE_NUMBER
                                        )!!
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            alertDialog.dismiss()
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            alertDialog.dismiss()
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun getCustomerToken(email: String, mobileNumber: String) {
        val userInfo = CustomerTokenParams(param = CustomerParams(email, mobileNumber, ""))
        viewModel.getCustomerToken(userInfo, Utility.getLanguage()).observe(viewLifecycleOwner,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(
                                Constants.CUSTOMER_TOKEN,
                                resource.data.toString()
                            )
                            getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun getQuoteIdFunc(token: String) {
        val quoteId = QuoteIdParams(
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!
        )
        viewModel.getQuoteId("Bearer $token", quoteId).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        cartRetain(resource.data.toString())
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

    private fun cartRetain(quoteId: String) {
        val cartRetainParams = CartRetainParams(
            param = CartRetainParamsData(
                incrementOrderId,
                "android",
                quoteId
            )
        )
        viewModel.cartRetain(cartRetainParams)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(
                                requireContext(),
                                resource.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun openThankYouPage() {
        getCartItemCount(0)
        activity?.let { ViewUtils.hideProgressBar() }
        val fragment = OrderPlaceFragment()
        val bundle = Bundle()
        bundle.putString("orderId", orderId)
        fragment.arguments = bundle
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(
                    fragment,
                    R.id.container
                )
            }
            else -> (activity as BuyerActivity).replaceFragment(
                fragment,
                R.id.container
            )
        }
    }

    private fun getCartItemCount(count: Int) {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).cartItemCount(count)
            }
            else -> {
                (activity as BuyerActivity).cartItemCount(count)
            }
        }
    }

    companion object {
        private const val SDK_REQUEST_CODE = 1001
    }

    override fun sessionIsStarting() {
        println(" Session Is Starting.....")
    }

    override fun sessionHasStarted() {
        println(" Session Has Started .......")
    }


    override fun sessionCancelled() {
        Log.d("MainActivity", "Session Cancelled.........")
    }

    override fun sessionFailedToStart() {
        Log.d("MainActivity", "Session Failed to start.........")
    }


    override fun invalidCardDetails() {
        println(" Card details are invalid....")
    }

    override fun backendUnknownError(message: String) {
        println("Backend Un-Known error.... : $message")
    }

    override fun invalidTransactionMode() {
        println(" invalidTransactionMode  ......")
    }

    override fun invalidCustomerID() {

        println("Invalid Customer ID .......")
    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        println("userEnabledSaveCardOption :  $saveCardEnabled")
    }

    override fun paymentSucceed(charge: Charge) {
        verifyTapPayment(orderId,charge.id,charge.source.id)

        System.out.println(charge.getId());
        System.out.println(charge.getRedirect().toString());


        println("Payment Succeeded : charge status : " + charge.status)
        println("Payment Succeeded : description : " + charge.description)
        println("Payment Succeeded : message : " + charge.response.message)
        println("##############################################################################")
        if (charge.card != null) {
            println("Payment Succeeded : first six : " + charge.card!!.firstSix)
            println("Payment Succeeded : last four: " + charge.card!!.lastFour)
            println("Payment Succeeded : card object : " + charge.card!!.getObject())
            println("Payment Succeeded : brand : " + charge.card!!.brand)
            //   System.out.println("Payment Succeeded : exp mnth : "+ charge.getCard().getExpiry().getMonth());
            //   System.out.println("Payment Succeeded : exp year : "+ charge.getCard().getExpiry().getYear());
        }
        println("##############################################################################")
        if (charge.topup != null) {
            println("Payment Succeeded : topupWalletId : " + charge.topup!!.walletId)
            println("Payment Succeeded : Id : " + charge.topup!!.id)
            println("Payment Succeeded : TopUpApp : " + charge.topup!!.application.amount)
            println("Payment Succeeded : status : " + charge.topup!!.status)
            println("Payment Succeeded : post : " + charge.topup!!.post!!.url)
            println("Payment Succeeded : amount : " + charge.topup!!.amount)
        }


        println("##############################################################################")
        if (charge.acquirer != null) {
            println("Payment Succeeded : acquirer id : " + charge.acquirer!!.id)
            println("Payment Succeeded : acquirer response code : " + charge.acquirer!!.response.code)
            println("Payment Succeeded : acquirer response message: " + charge.acquirer!!.response.message)
        }
        println("##############################################################################")
        if (charge.source != null) {
            println("Payment Succeeded : source id: " + charge.source.id)
            println("Payment Succeeded : source channel: " + charge.source.channel)
            println("Payment Succeeded : source object: " + charge.source.getObject())
            println("Payment Succeeded : source payment method: " + charge.source.paymentMethodStringValue)
            println("Payment Succeeded : source payment type: " + charge.source.paymentType)
            println("Payment Succeeded : source type: " + charge.source.type)
        }

        println("##############################################################################")
        if (charge.expiry != null) {
            println("Payment Succeeded : expiry type :" + charge.expiry!!.type)
            println("Payment Succeeded : expiry period :" + charge.expiry!!.period)
        }

        //saveCustomerRefInSession(charge)
        configureSDKSession()
        openThankYouPage()
    }

    override fun paymentFailed(charge: Charge?) {
        cartRetain(PreferenceHelper.readString(Constants.CUSTOMER_ID)!!)
       // getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
        println("Payment Failed : " + charge!!.status)
        println("Payment Failed : " + charge.description)
        println("Payment Failed : " + charge.response.message)





    }

    override fun authorizationSucceed(authorize: Authorize) {
        TODO("Not yet implemented")
    }

    override fun authorizationFailed(authorize: Authorize?) {
        TODO("Not yet implemented")
    }

    override fun cardSaved(charge: Charge) {
        TODO("Not yet implemented")
    }

    override fun cardSavingFailed(charge: Charge) {
        TODO("Not yet implemented")
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        println("userEnabledSaveCardOption :  ${token.toString()}")
        println("cardTokenizedSuccessfully Succeeded : ")
        println("Token card : " + token.card.firstSix + " **** " + token.card.lastFour)
        println("Token card : " + token.card.fingerprint + " **** " + token.card.funding)
        println("Token card : " + token.card.id + " ****** " + token.card.name)
        println("Token card : " + token.card.address + " ****** " + token.card.getObject())
        println("Token card : " + token.card.expirationMonth + " ****** " + token.card.expirationYear)
    }

    override fun cardTokenizedSuccessfully(@NonNull token: Token, saveCardEnabled: Boolean) {
        println("userEnabledSaveCardOption :  $saveCardEnabled")
        println("cardTokenizedSuccessfully Succeeded : ")
        println("Token card : " + token.card.firstSix + " **** " + token.card.lastFour)
        println("Token card : " + token.card.fingerprint + " **** " + token.card.funding)
        println("Token card : " + token.card.id + " ****** " + token.card.name)
        println("Token card : " + token.card.address + " ****** " + token.card.getObject())
        println("Token card : " + token.card.expirationMonth + " ****** " + token.card.expirationYear)

    }

    override fun savedCardsList(cardsList: CardsList) {
        TODO("Not yet implemented")
    }

    override fun sdkError(goSellError: GoSellError?) {
        println(goSellError?.errorMessage.toString())
        getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
    }

}
    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
//        Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }




    }
