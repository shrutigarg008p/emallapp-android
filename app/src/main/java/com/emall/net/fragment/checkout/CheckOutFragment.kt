package com.emall.net.fragment.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.CheckOutAdapter
import com.emall.net.fragment.ecommerceAddress.AddressList
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.adminToken.AdminTokenParams
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.getShippingInformationRequest.AddressInformation
import com.emall.net.network.model.getShippingInformationRequest.BillingAddress
import com.emall.net.network.model.getShippingInformationRequest.GetShippingInformationParams
import com.emall.net.network.model.getShippingInformationRequest.ShippingAddress
import com.emall.net.network.model.getShippingInformationResponse.TotalSegment
import com.emall.net.network.model.getShippingMethodsRequest.Address
import com.emall.net.network.model.getShippingMethodsRequest.GetShippingMethodsParams
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.utils.*
import com.emall.net.utils.Constants.CUSTOMER_EMAIL
import com.emall.net.utils.Constants.CUSTOMER_ID
import com.emall.net.utils.Constants.CUSTOMER_TOKEN
import com.emall.net.utils.Constants.SELECTED_ADDRESS_CITY
import com.emall.net.utils.Constants.SELECTED_ADDRESS_COUNTRY
import com.emall.net.utils.Constants.SELECTED_ADDRESS_COUNTRY_ID
import com.emall.net.utils.Constants.SELECTED_ADDRESS_FIRST_NAME
import com.emall.net.utils.Constants.SELECTED_ADDRESS_LAST_NAME
import com.emall.net.utils.Constants.SELECTED_ADDRESS_PHONE_NUMBER
import com.emall.net.utils.Constants.SELECTED_ADDRESS_POSTCODE
import com.emall.net.utils.Constants.SELECTED_ADDRESS_STREET
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_check_out.*


class CheckOutFragment : Fragment() {
    private lateinit var checkOutAdapter: CheckOutAdapter
    private var totalSegment = ArrayList<TotalSegment>()
    private var isDiscountBoxVisible: Boolean? = false
    private lateinit var viewModel: MainViewModel
    private var adminToken: String? = ""
    private var quote_id: String? = ""
    private var carrierCode: String? = ""
    private var methodCode: String? = ""
    private var grandTotal: Int? = 0
    private var isCouponCodeApplied: Boolean? = false
    private var tempCouponCode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.checkout))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.checkout))
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
        setUpData()
    }

    private fun setUpData() {
        total_amount_recyclerview.setHasFixedSize(true)
        total_amount_recyclerview.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)

        add_and_edit_button.setOnClickListener(View.OnClickListener {
            openAddressListFragment()
        })

        discount_code_button.setOnClickListener(View.OnClickListener {
            if (!isDiscountBoxVisible!!) {
                showDiscountContainer()
            } else {
                hideDiscountContainer()
            }
        })

        cancel_discount_container.setOnClickListener(View.OnClickListener {
            if (isCouponCodeApplied!!) {
                if (discount_coupon_value.text.toString().isNotEmpty()) {
                    removeCouponCode(quote_id!!)
                }
            }
            discount_coupon_value.setText("")
            hideDiscountContainer()
        })

        apply_discount_container.setOnClickListener(View.OnClickListener {
            if (!isCouponCodeApplied!!) {
                if (discount_coupon_value.text.toString().isNotEmpty()) {
                    applyCouponCode(quote_id!!)
                } else {
                    Toast.makeText(requireContext(),
                        R.string.please_enter_coupon_code,
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                if (tempCouponCode == discount_coupon_value.text.toString()) {
                    Toast.makeText(requireContext(),
                        R.string.coupon_code_already_applied,
                        Toast.LENGTH_SHORT).show()
                } else {
                    applyCouponCode(quote_id!!)
                }
            }
        })

        continue_to_payment_button.setOnClickListener(View.OnClickListener {
            openPaymentOptionFragment()
        })
        setUserInfo()
    }

    private fun showDiscountContainer() {
        isDiscountBoxVisible = true
        discount_code_text.text = resources.getText(R.string.discount_code_minus)
        discount_container.makeVisible()
    }

    private fun hideDiscountContainer() {
        isDiscountBoxVisible = false
        discount_code_text.text = resources.getText(R.string.discount_code_plus)
        discount_container.makeGone()
    }

    private fun openPaymentOptionFragment() {
        val fragment = PaymentOptionsFragment()
        val bundle = Bundle()
        bundle.putInt("grandTotal", grandTotal!!)
        fragment.arguments = bundle
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(fragment, R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
        }
    }

    private fun setUserInfo() {
        if (PreferenceHelper.readString(Constants.SELECTED_ADDRESS) != "") {
            user_name.text =
                PreferenceHelper.readString(SELECTED_ADDRESS_FIRST_NAME) + " " + PreferenceHelper.readString(
                    SELECTED_ADDRESS_LAST_NAME)
            user_street.text = PreferenceHelper.readString(SELECTED_ADDRESS_STREET)
            user_city.text = PreferenceHelper.readString(SELECTED_ADDRESS_CITY)
            user_country.text = PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY)
            user_phone_number.text =
                if (PreferenceHelper.readString(Constants.APP_LOCALE)
                        .equals(Constants.ENGLISH_LOCALE)
                ) {
                    "+${PreferenceHelper.readString(SELECTED_ADDRESS_PHONE_NUMBER)}"
                } else {
                    "${PreferenceHelper.readString(SELECTED_ADDRESS_PHONE_NUMBER)}+"
                }
            getAdminToken()
        } else {
            openAddressListFragment()
        }
    }

    private fun openAddressListFragment() {
        val fragment = AddressList()
        val bundle = Bundle()
        bundle.putString("from", "checkout")
        fragment.arguments = bundle
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(fragment, R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
        }
    }


    private fun getAdminToken() {
        activity?.let { ViewUtils.showProgressBar(it) }
        val userInfo = AdminTokenParams(
            "apiuser",
            "Admin@1234"
        )
        viewModel.getAdminToken(userInfo).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        adminToken = resource.data.toString()
                        if (PreferenceHelper.readString(CUSTOMER_TOKEN) != "") {
                            getQuoteIdFunc(PreferenceHelper.readString(CUSTOMER_TOKEN)!!)
                        }
                        else {
                            getCustomerToken(
                                PreferenceHelper.readString(CUSTOMER_EMAIL)!!,
                                PreferenceHelper.readString(
                                    Constants.CUSTOMER_PHONE_NUMBER)!!
                            )
                        }
                    }
                    Status.ERROR -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
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
                                CUSTOMER_TOKEN,
                                resource.data.toString()
                            )
                            getQuoteIdFunc(PreferenceHelper.readString(CUSTOMER_TOKEN)!!)
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
            PreferenceHelper.readInt(CUSTOMER_ID)!!
        )
        viewModel.getQuoteId("Bearer $token", quoteId).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        quote_id = resource.data.toString()
                        getShippingMethods(PreferenceHelper.readString(CUSTOMER_TOKEN)!!)
                    }
                    Status.ERROR -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    //    api to get shipping methods
    private fun getShippingMethods(token: String) {
        val streetData = ArrayList<String>()
        streetData.add(PreferenceHelper.readString(SELECTED_ADDRESS_STREET).toString())
        val shippingMethodsParams = GetShippingMethodsParams(
            address = Address(
                PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY).toString(),
                1,
                PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID).toString(),
                PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID).toString(),
                street = streetData,
                PreferenceHelper.readString(SELECTED_ADDRESS_POSTCODE).toString(),
                PreferenceHelper.readString(SELECTED_ADDRESS_CITY).toString(),
                PreferenceHelper.readString(SELECTED_ADDRESS_FIRST_NAME).toString(),
                PreferenceHelper.readString(SELECTED_ADDRESS_LAST_NAME).toString(),
                PreferenceHelper.readInt(CUSTOMER_ID)!!,
                PreferenceHelper.readString(CUSTOMER_EMAIL).toString(),
                PreferenceHelper.readString(SELECTED_ADDRESS_PHONE_NUMBER).toString(),
                1
            )
        )
        viewModel.getShippingMethods("Bearer $token",
            PreferenceHelper.readString(Constants.COOKIES)!!,
            shippingMethodsParams,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            carrierCode = resource.data?.get(0)?.carrier_code.toString()
                            methodCode = resource.data?.get(0)?.method_code.toString()
                            getShippingInformation(PreferenceHelper.readString(CUSTOMER_TOKEN)!!,
                                carrierCode!!,
                                methodCode!!)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    //    api to get shipping information
    private fun getShippingInformation(token: String, carrierCode: String, methodCode: String) {
        val streetInfo = ArrayList<String>()
        streetInfo.add(PreferenceHelper.readString(SELECTED_ADDRESS_STREET).toString())
        val shippingInformationParams = GetShippingInformationParams(
            addressInformation = AddressInformation(
                billing_address = BillingAddress(
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY).toString(),
                    1,
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID)!!.toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID)!!.toString(),
                    street = streetInfo,
                    PreferenceHelper.readString(SELECTED_ADDRESS_POSTCODE).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_CITY).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_FIRST_NAME).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_LAST_NAME).toString(),
                    PreferenceHelper.readString(CUSTOMER_EMAIL).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_PHONE_NUMBER).toString(),
                ),
                shipping_address = ShippingAddress(
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY).toString(),
                    1,
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID)!!.toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_COUNTRY_ID)!!.toString(),
                    street = streetInfo,
                    PreferenceHelper.readString(SELECTED_ADDRESS_POSTCODE).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_CITY).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_FIRST_NAME).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_LAST_NAME).toString(),
                    PreferenceHelper.readString(CUSTOMER_EMAIL).toString(),
                    PreferenceHelper.readString(SELECTED_ADDRESS_PHONE_NUMBER).toString(),
                ),
                shipping_carrier_code = carrierCode,
                shipping_method_code = methodCode
            )
        )
        viewModel.getShippingInformation("Bearer $token",
            PreferenceHelper.readString(Constants.COOKIES)!!,
            shippingInformationParams,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            checkout_fragment_layout.makeVisible()
                            grandTotal = resource.data?.totals!!.grand_total.toInt()
                            if (resource.data?.totals!!.discount_amount.toInt() != 0) {
                                isCouponCodeApplied = true
                                tempCouponCode = resource.data?.totals!!.coupon_code
                                discount_coupon_value.setText(tempCouponCode)
                                discount_coupon_value.setSelection(discount_coupon_value.text.length)
                                showDiscountContainer()
                            }
                            totalSegment.clear()
                            totalSegment = resource.data?.totals?.total_segments
                            checkOutAdapter = CheckOutAdapter(totalSegment)
                            total_amount_recyclerview.adapter = checkOutAdapter
                            checkOutAdapter.notifyDataSetChanged()
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    //    function to apply coupon code
    private fun applyCouponCode(quoteId: String) {
        activity?.let { ViewUtils.showProgressBar(it) }
        viewModel.addCouponCode("Bearer $adminToken",
            PreferenceHelper.readString(Constants.COOKIES)!!,
            quoteId,
            discount_coupon_value.text.toString(),
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isCouponCodeApplied = true
                            Toast.makeText(requireContext(),
                                R.string.coupon_code_applied_successfully,
                                Toast.LENGTH_SHORT).show()
                            getShippingInformation(PreferenceHelper.readString(CUSTOMER_TOKEN)!!,
                                carrierCode!!,
                                methodCode!!)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            if (it.message!!.contains("404")) {
                                Toast.makeText(requireContext(),
                                    R.string.invalid_coupon_code, Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                            }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    //    function to remove coupon code
    private fun removeCouponCode(quoteId: String) {
        activity?.let { ViewUtils.showProgressBar(it) }
        viewModel.removeCouponCode("Bearer $adminToken",
            PreferenceHelper.readString(Constants.COOKIES)!!,
            quoteId,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isCouponCodeApplied = false
                            discount_coupon_value.setText("")
                            discount_coupon_value.setSelection(discount_coupon_value.text.length)
                            Toast.makeText(requireContext(),
                                R.string.coupon_code_removed_successfully,
                                Toast.LENGTH_SHORT).show()
                            getShippingInformation(PreferenceHelper.readString(CUSTOMER_TOKEN)!!,
                                carrierCode!!,
                                methodCode!!)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }


}