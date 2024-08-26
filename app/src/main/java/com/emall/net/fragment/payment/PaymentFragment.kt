package com.emall.net.fragment.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.fragment.addressList.SellerAddressListFragment
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.payment.stc.OTPValue
import com.emall.net.utils.*
import com.emall.net.utils.Constants.IS_SELLER
import com.emall.net.utils.Constants.PRODUCT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.stc_tap_fragment.*
import java.util.*

class PaymentFragment : Fragment(), View.OnClickListener/*, SessionDelegate*/ {

    private var token: String? = ""
    private var date: String? = ""
    private var OTP: String? = ""
    private var paymentType: String? = ""
    private var orderNumber: Int? = 0
    private var transactionNumber: String? = ""
    private var auctionId: Int? = 0
    private var evaluationId: Int? = 0
    private var productId: Int? = 0
    private var amount: Double? = 0.00

    private lateinit var viewModel: MainViewModel
//    private var sdkSession: SDKSession? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return layoutInflater.inflate(R.layout.stc_tap_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        productId = arguments?.getInt(PRODUCT_ID)
        auctionId = arguments?.getInt("auctionId")
        evaluationId = arguments?.getInt("evaluationId")
        amount = arguments?.getDouble("amount")

        date = String.format("%1tY-%<tm-%<td %<tR", Calendar.getInstance())
        println("current date is $date")

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        stc_payment_btn.setOnClickListener(this)
        stc_payment_btn.text = "Proceed with ${PreferenceHelper.readString("payment")}"
    }

    /*override fun onStart() {
        super.onStart()
        configureApp()
    }*/

    override fun onClick(v: View) {
        when (v.id) {
            R.id.stc_payment_btn -> {
                if (PreferenceHelper.readBoolean(IS_SELLER)!!) {
                    viewModel.stcFailedPaymentForAuction(productId!!, auctionId!!, "Bearer $token",Utility.getLanguage())
                        .observe(viewLifecycleOwner,
                            {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                            constraintLayout.snack("Please enter OTP which has been sent to the registered number")
                                            progress_bar.makeGone()
                                            auctionId = it.auction_id
                                            showOTPDialog()
                                        }
                                        Status.ERROR -> progress_bar.makeGone()
                                        Status.LOADING -> progress_bar.makeVisible()
                                    }
                                }

                            })
                } else {
                    viewModel.stcFailedPaymentForEvaluation(productId!!,
                        evaluationId!!,
                        "Bearer $token",Utility.getLanguage())
                        .observe(viewLifecycleOwner,
                            {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                            constraintLayout.snack("Please enter OTP which has been sent to the registered number")
                                            progress_bar.makeGone()
                                            evaluationId = it.evaluation_id
                                            showOTPDialog()
                                        }
                                        Status.ERROR -> progress_bar.makeGone()
                                        Status.LOADING -> progress_bar.makeVisible()
                                    }
                                }
                            })
                }
            }
        }
    }

    private fun showOTPDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.otp_dialog, scrollView.findViewById(R.id.content), false)
        builder.setView(view)

        val editText = view.findViewById(R.id.stcBody) as AppCompatEditText
        builder.setCancelable(false)

        val okBtn = view.findViewById(R.id.otpLayout3) as ConstraintLayout
        val cancelBtn = view.findViewById(R.id.otpLayout1) as ConstraintLayout
        val alertDialog = builder.create()
        alertDialog.show()
        okBtn.setOnClickListener {
            OTP = editText.text.toString()

            if (PreferenceHelper.readBoolean(IS_SELLER)!!) {
                viewModel.stcOTPVerifyForAuction(
                    productId!!,
                    auctionId!!,
                    OTPValue(OTP.toString()),
                    "Bearer $token",Utility.getLanguage()
                )
                    .observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> resource.data?.let { it ->
                                    progress_bar.makeGone()
                                    if (it.STATUS ==200) {
                                        val fragment = SellerAddressListFragment()
                                        val bundle = Bundle()
                                        PreferenceHelper.writeInt(PRODUCT_ID, productId!!)
                                        fragment.arguments = bundle
                                        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
                                    }
                                    alertDialog.dismiss()
                                }
                                Status.ERROR -> {
                                    progress_bar.makeGone()
                                    alertDialog.dismiss()
                                }
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }

                    })
            } else {
                viewModel.stcOTPVerifyForEvaluation(
                    productId!!,
                    evaluationId!!,
                    OTPValue(OTP.toString()),
                    "Bearer $token",
                    Utility.getLanguage()
                )
                    .observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> resource.data?.let { it ->
                                    progress_bar.makeGone()
                                    if (it.STATUS == 200) {
                                        val fragment = SellerAddressListFragment()
                                        val bundle = Bundle()
                                        PreferenceHelper.writeInt(PRODUCT_ID, productId!!)
                                        fragment.arguments = bundle
                                        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
                                    }
                                    alertDialog.dismiss()
                                }
                                Status.ERROR -> {
                                    progress_bar.makeGone()
                                    alertDialog.dismiss()
                                }
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }
                    })
            }
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    /*     R.id.stc_radio_btn -> paymentType = stc_radio_btn.text.toString()
     R.id.tap_radio_btn -> paymentType = tap_radio_btn.text.toString()
     R.id.tap_payment -> {
         if (submitType.equals("auction", true)) {
             if (paymentType!!.toLowerCase() == "tap") {
                 viewModel.createNewAuctionForTAP(
                     productId!!,
                     "Bearer $token",
                     PaymentRequestBody(date!!, paymentType!!.toLowerCase())
                 ).observe(viewLifecycleOwner, {
                     it?.let { resource ->
                         when (resource.status) {
                             Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                 orderNumber = it.orderNumber
                                 transactionNumber = it.transactionNumber
                                 auctionId = it.auction_id
                                 sdkSession?.start(requireActivity())
                             }
                             Status.ERROR -> {
                             }
                             Status.LOADING -> {
                             }
                         }
                     }
                 })
             }
         } else {
             if (paymentType!!.toLowerCase() == "tap") {
                 viewModel.createNewEvaluationForTAP(
                     productId!!,
                     "Bearer $token",
                     PaymentRequestBody(date!!, paymentType!!.toLowerCase())
                 ).observe(viewLifecycleOwner, {
                     it?.let { resource ->
                         when (resource.status) {
                             Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                 orderNumber = it.orderNumber
                                 transactionNumber = it.transactionNumber
                                 auctionId = it.auction_id
                                 sdkSession?.start(requireActivity())
                             }
                             Status.ERROR -> {
                             }
                             Status.LOADING -> {
                             }
                         }
                     }
                 })
             }
         }
     }

            if (submitType.equals("auction", true)) {
                if (paymentType!!.toLowerCase() == "stc") {
                    viewModel.createNewAuctionForSTC(
                        productId!!, "Bearer $token",
                        PaymentRequestBody(date!!, paymentType!!.toLowerCase())
                    ).observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                    auctionId = it.auction_id
                                    showOTPDialog(submitType)
                                }
                                Status.ERROR -> {
                                }
                                Status.LOADING -> {
                                }
                            }
                        }
                    })

                }
            } else {
                if (paymentType!!.toLowerCase() == "stc") {
                    viewModel.createNewEvaluationForSTC(
                        productId!!, "Bearer $token",
                        PaymentRequestBody(date!!, paymentType!!.toLowerCase())
                    ).observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> resource.data?.DATA?.let { it ->
                                    auctionId = it.evaluation_id
                                    showOTPDialog(submitType)
                                }
                                Status.ERROR -> {
                                }
                                Status.LOADING -> {
                                }
                            }
                        }
                    })

                }

            }
private fun configureApp() {
    GoSellSDK.init(
        activity,
        Constants.TAP_PAYMENT_SECRET_STAGING_KEY,
        BuildConfig.APPLICATION_ID
    ) // to be replaced by merchant
    GoSellSDK.setLocale("en") //  language to be set by merchant
    startSdk()
}

/**
 * payment methods
 */
private fun startSdk() {
    configureSDKSession()
    configureSDKMode()
}

private fun configureSDKSession() {
    when (sdkSession) {
        null -> sdkSession = SDKSession()
    }
    // pass your activity as a session delegate to listen to SDK internal payment process follow
    sdkSession!!.addSessionDelegate(this)
    sdkSession!!.instantiatePaymentDataSource()
    sdkSession!!.setTransactionCurrency(TapCurrency("SAR"))
    sdkSession!!.setCustomer(getCustomer())
    sdkSession!!.setPaymentReference(
        Reference(
            "",
            "",
            "",
            "",
            transactionNumber,
            orderNumber.toString()
        )
    )
    sdkSession!!.setAmount(BigDecimal(40))
    sdkSession!!.isUserAllowedToSaveCard(false)
    sdkSession!!.isRequires3DSecure(true)
}

private fun getCustomer(): Customer? { // test customer id cus_Kh1b4220191939i1KP2506448
    val customer: Customer? = getCustomers()
    val phoneNumber: PhoneNumber? =
        if (customer != null) customer.phone else PhoneNumber("965", "65562630")
    return Customer.CustomerBuilder("").email("abc@abc.com").firstName("firstname")
        .lastName("lastname").metadata("")
        .phone(PhoneNumber(phoneNumber?.countryCode, phoneNumber?.number))
        .middleName("middlename").build()
}

private fun getCustomers(): Customer? {
    return Customer.CustomerBuilder(null).firstName("Name").middleName("MiddleName")
        .lastName("Surname")
        .email("hello@tap.company").phone(PhoneNumber("965", "69045932")).metadata("meta")
        .build()
}

private fun configureSDKMode() {
    if (sdkSession != null) {
        sdkSession!!.transactionMode = company.tap.gosellapi.open.enums.TransactionMode.PURCHASE
    }
}*/

    /*override fun paymentSucceed(charge: Charge) {
        if (submitType.equals("auction", true)) {
            viewModel.tapVerifyForAuction(
                productId!!,
                auctionId!!,
                TAPChargeId(charge.id),
                "Bearer $token"
            )
                .observe(viewLifecycleOwner, {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> resource.data?.let { it ->
                                if (it.MESSAGE.contentEquals("Payment OK")) {
                                    (activity as MainActivity).openActivity(MainActivity::class.java)
                                }
                            }
                            Status.ERROR -> {
                            }
                            Status.LOADING -> {
                            }
                        }
                    }

                })
        } else {
            viewModel.tapVerifyForEvaluation(
                productId!!,
                auctionId!!,
                TAPChargeId(charge.id),
                "Bearer $token"
            )
                .observe(viewLifecycleOwner, {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> resource.data?.let { it ->
                                if (it.MESSAGE.contentEquals("Payment OK")) {
                                    (activity as MainActivity).openActivity(MainActivity::class.java)
                                }
                            }
                            Status.ERROR -> {
                            }
                            Status.LOADING -> {
                            }
                        }
                    }

                })
        }
        println("Payment Succeeded : charge status : " + charge.status)
        println("Payment Succeeded : charge status : id = " + charge.id)
        println("Payment Succeeded : orderId : " + charge.reference.order)
        println("Payment Succeeded : transaction ID : " + charge.reference.transaction)
        println("Payment Succeeded : description : " + charge.description)
        println("Payment Succeeded : message : " + charge.response.message)
        println("##############################################################################")
        if (charge.card != null) {
            println("Payment Succeeded : first six : " + charge.card!!.firstSix)
            println("Payment Succeeded : last four: " + charge.card!!.lastFour)
            println("Payment Succeeded : card object : " + charge.card!!.getObject())
            println("Payment Succeeded : brand : " + charge.card!!.brand)
            println(
                "Payment Succeeded : exp mnth : " + charge.card!!.expiry!!
                    .month
            )
            println(
                "Payment Succeeded : exp year : " + charge.card!!.expiry!!
                    .year
            )
        }

        println("##############################################################################")
        if (charge.acquirer != null) {
            println("Payment Succeeded : acquirer id : " + charge.acquirer!!.id)
            println(
                "Payment Succeeded : acquirer response code : " + charge.acquirer!!
                    .response.code
            )
            println(
                "Payment Succeeded : acquirer response message: " + charge.acquirer!!
                    .response.message
            )
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
        configureSDKSession()
    }

    override fun paymentFailed(charge: Charge?) {
        println("Payment Failed : " + charge!!.status)
        println("Payment Failed : " + charge.description)
        println("Payment Failed : " + charge.response.message)
    }

    override fun authorizationSucceed(authorize: Authorize) {
        println("Authorize Succeeded : " + authorize.status)
        println("Authorize Succeeded : " + authorize.response.message)

        if (authorize.card != null) {
            println(
                "Payment Authorized Succeeded : first six : " + authorize.card!!
                    .firstSix
            )
            println(
                "Payment Authorized Succeeded : last four: " + authorize.card!!
                    .last4
            )
            println(
                "Payment Authorized Succeeded : card object : " + authorize.card!!
                    .getObject()
            )
        }

        println("##############################################################################")
        if (authorize.acquirer != null) {
            println(
                "Payment Authorized Succeeded : acquirer id : " + authorize.acquirer!!
                    .id
            )
            println(
                "Payment Authorized Succeeded : acquirer response code : " + authorize.acquirer!!
                    .response.code
            )
            println(
                "Payment Authorized Succeeded : acquirer response message: " + authorize.acquirer!!
                    .response.message
            )
        }
        println("##############################################################################")
        if (authorize.source != null) {
            println("Payment Authorized Succeeded : source id: " + authorize.source.id)
            println("Payment Authorized Succeeded : source channel: " + authorize.source.channel)
            println("Payment Authorized Succeeded : source object: " + authorize.source.getObject())
            println("Payment Authorized Succeeded : source payment method: " + authorize.source.paymentMethodStringValue)
            println("Payment Authorized Succeeded : source payment type: " + authorize.source.paymentType)
            println("Payment Authorized Succeeded : source type: " + authorize.source.type)
        }

        println("##############################################################################")
        if (authorize.expiry != null) {
            println(
                "Payment Authorized Succeeded : expiry type :" + authorize.expiry!!
                    .type
            )
            println(
                "Payment Authorized Succeeded : expiry period :" + authorize.expiry!!
                    .period
            )
        }
        configureSDKSession()
    }

    override fun authorizationFailed(authorize: Authorize?) {
        println("Authorize Failed : " + authorize!!.status)
        println("Authorize Failed : " + authorize.description)
        println("Authorize Failed : " + authorize.response.message)
    }

    override fun cardSaved(charge: Charge) {
        // Cast charge object to SaveCard first to get all the Card info.

        // Cast charge object to SaveCard first to get all the Card info.
        if (charge is SaveCard) {
            println(
                "Card Saved Succeeded : first six digits : " + charge.card
                    ?.firstSix
                    .toString() + "  last four :" + charge.card?.last4
            )
        }
        println("Card Saved Succeeded : " + charge.status)
        println("Card Saved Succeeded : " + charge.card!!.brand)
        println("Card Saved Succeeded : " + charge.description)
        println("Card Saved Succeeded : " + charge.response.message)
        println(
            "Card Saved Succeeded : " + (charge as SaveCard).cardIssuer.name
        )
        println("Card Saved Succeeded : " + charge.cardIssuer.id)
    }

    override fun cardSavingFailed(charge: Charge) {
        println("Card Saved Failed : " + charge.status)
        println("Card Saved Failed : " + charge.description)
        println("Card Saved Failed : " + charge.response.message)
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        println("Card Tokenized Succeeded : ")
        println("Token card : " + token.card.firstSix.toString() + " **** " + token.card.lastFour)
        println("Token card : " + token.card.fingerprint + " **** " + token.card.funding)
        println("Token card : " + token.card.id.toString() + " ****** " + token.card.name)
        println("Token card : " + token.card.address.toString() + " ****** " + token.card.getObject())
        println("Token card : " + token.card.expirationMonth.toString() + " ****** " + token.card.expirationYear)
    }

    override fun savedCardsList(cardsList: CardsList) {
        if (cardsList != null && cardsList.cards != null) {
            println(" Card List Response Code : " + cardsList.responseCode)
            println(" Card List Top 10 : " + cardsList.cards.size)
            println(" Card List Has More : " + cardsList.isHas_more)
            println("----------------------------------------------")
            for (sc in cardsList.cards) {
                println(sc.brandName)
            }
            println("----------------------------------------------")
        }
    }

    override fun sdkError(goSellError: GoSellError?) {
        if (goSellError != null) {
            println("SDK Process Error : " + goSellError.errorBody)
            println("SDK Process Error : " + goSellError.errorMessage)
            println("SDK Process Error : " + goSellError.errorCode)
        }
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

    override fun backendUnknownError(message: String?) {
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
    }*/
}