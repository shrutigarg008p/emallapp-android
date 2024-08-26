package com.emall.net.fragment.ecommerceAddress

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.addAddressRequest.AddAddressParams
import com.emall.net.network.model.addAddressRequest.AddAddressParamsData
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.network.model.numberVerifiedRequest.NumberVerifiedRequest
import com.emall.net.network.model.numberVerifiedRequest.NumberVerifiedRequestParam
import com.emall.net.network.model.updateAddressRequest.UpdateAddressParamData
import com.emall.net.network.model.updateAddressRequest.UpdateAddressParams
import com.emall.net.utils.*
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_address_detail.*
import java.util.*

class AddressDetailFragment : BaseFragment() {
    private lateinit var viewModel: MainViewModel
    private var AddressId: String = ""
    private var FullName: String = ""
    private var City: String = ""
    private var ZipCode: String = ""
    private var Address: String = ""
    private var CountryCode: String = ""
    private var PhoneNumber: String = ""
    private var Latitude: String = ""
    private var Longitude: String = ""
    private var Type: String = ""
    private var CountryCellCode: String = ""
    private var countryList = ArrayList<GetCountryDetailData>()
    private var countryName = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.address))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.address))
            }
        }

        getArgumentsFromMapFragment()
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

    private fun getArgumentsFromMapFragment() {
        if (arguments != null) {
            AddressId = arguments?.getString("addressId")!!
            Type = arguments?.getString("type")!!
            FullName = arguments?.getString("fullName")!!
            City = arguments?.getString("city")!!
            PhoneNumber = arguments?.getString("phoneNumber")!!
            CountryCode = arguments?.getString("countryCode")!!
            Address = arguments?.getString("address")!!
            ZipCode = arguments?.getString("zipCode")!!
            Latitude = arguments?.getString("latitude")!!
            Longitude = arguments?.getString("longitude")!!
        }
    }

    private fun setUp() {
        getCountryDetail(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN))
        full_name_value.addTextChangedListener(mTextWatcher)
        phone_number_value.addTextChangedListener(mTextWatcher)
        city_value.addTextChangedListener(mTextWatcher)
        zip_value.addTextChangedListener(mTextWatcher)
        address_value.addTextChangedListener(mTextWatcher)
        full_name_value.setText(FullName)
        phone_number_value.setText(PhoneNumber)
        city_value.setText(City)
        address_value.setText(Address)
        zip_value.setText(ZipCode)

        country_value.setOnClickListener(View.OnClickListener {
            if (countryList.size > 0) {
                val popUp: PopupWindow = popUpCountryWindow()
                popUp.showAtLocation(address_detail_layout, Gravity.CENTER, 10, 10)
            }
        })
    }

    private fun popUpCountryWindow(): PopupWindow {
        val popupWindow = PopupWindow(context)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(),
                R.layout.checkedradiotextviewenglish,
                countryName)
        val listViewSort = ListView(context)
        listViewSort.adapter = adapter
        listViewSort.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                country_value.setText(countryName[position])
                CountryCode = countryList[position].code
                CountryCellCode = countryList[position].cel_code.toString()
                popupWindow.dismiss()
            }
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(address_detail_layout, Gravity.CENTER, 10, 200)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        popupWindow.setBackgroundDrawable(this.resources.getDrawable(R.drawable.solid_white_round_background))
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = displayMetrics.widthPixels
        popupWindow.contentView = listViewSort
        return popupWindow
    }

    private fun getCountryDetail(token: String?) {
        viewModel.getCountryDetail(Utility.getLanguage())
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
                                countryList.clear()
                                countryName.clear()
                                countryList = it.data
                                for (country in it.data) {
                                    countryName.add(country.name + " (+${country.cel_code})")
                                }
                                if (CountryCode != "") {
                                    for (item in countryList) {
                                        if (CountryCode == item.code) {
                                            country_value.setText(item.name + " (+${item.cel_code})")
                                            CountryCellCode = item.cel_code.toString()
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            it.message?.let { it1 -> Log.e("error", it1) }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    //    Edit Text field watcher
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            checkFieldsForEmptyValues()
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }

    // check Fields For Empty Values
    private fun checkFieldsForEmptyValues() {
        if (full_name_value.text.toString() == "" || phone_number_value.text
                .toString() == "" || city_value.text.toString() == "" || zip_value.text
                .toString() == "" || address_value.text.toString() == ""
        ) {
            add_address_button.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_gray_gradient)
        } else {
            add_address_button.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_charcoal_black_gradient_btn_color)
            add_address_button.setOnClickListener(View.OnClickListener {
                isNumberVerified()
            })
        }
    }

    private fun isNumberVerified() {
        val numberVerifiedRequest = NumberVerifiedRequest(
            param = NumberVerifiedRequestParam(
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!.toString(),
                CountryCellCode + phone_number_value.text.toString()
            )
        )
        viewModel.isNumberVerified(numberVerifiedRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                if (it.data == 0) {
                                    sendOtp()
                                } else {
                                    if (Type.equals("edit", true)) {
                                        updateAddress()
                                    } else {
                                        addAddress()
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun sendOtp() {
        viewModel.sendOtp(CountryCellCode + phone_number_value.text.toString(),
            0,
            PreferenceHelper.readInt(
                Constants.CUSTOMER_ID)!!, 0)
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    if (it.success) {
                                        Toast.makeText(requireContext(),
                                            R.string.code_sent_successfully,
                                            Toast.LENGTH_SHORT).show()
                                        openAddressOtpDialog()
                                    } else {
                                        Toast.makeText(requireContext(),
                                            R.string.send_failed_retry,
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            Status.ERROR -> {
                                it.message?.let { it1 -> Log.e("error", it1) }
                                activity?.let { ViewUtils.hideProgressBar() }
                            }
                            Status.LOADING -> {
                                activity?.let { ViewUtils.showProgressBar(it) }
                            }
                        }
                    }
                })
    }

    private fun openAddressOtpDialog() {
        val prFrag: DialogFragment = AddressOtpDialog()
        val bundle = Bundle()
        bundle.putString("mobile_number", CountryCellCode + phone_number_value.text.toString())
        prFrag.arguments = bundle
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        val prev = requireActivity().supportFragmentManager.findFragmentByTag("otpDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        prFrag.setTargetFragment(this, 1002)
        prFrag.show(ft, "otpDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1002) {
            if (Type.equals("edit", true)) {
                updateAddress()
            } else {
                addAddress()
            }
        }
    }

    //    add address api
    private fun addAddress() {
        val addAddressParams = AddAddressParams(
            param = AddAddressParamsData(
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
                full_name_value.text.toString(),
                "",
                CountryCode,
                city_value.text.toString(),
                CountryCellCode + phone_number_value.text.toString(),
                address_value.text.toString(),
                Latitude,
                Longitude,
                zip_value.text.toString()
            )
        )
        viewModel.addAddress(addAddressParams)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.address_added_successfully,
                                Toast.LENGTH_SHORT).show()
                            openAddressListFragment()
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

    //    update address api
    private fun updateAddress() {
        val updateAddressParams = UpdateAddressParams(
            param = UpdateAddressParamData(
                AddressId.toInt(),
                FullName,
                "",
                CountryCode,
                city_value.text.toString(),
                CountryCellCode + phone_number_value.text.toString(),
                address_value.text.toString(),
                Latitude,
                Longitude,
                zip_value.text.toString()
            )
        )
        viewModel.updateAddress(updateAddressParams, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.address_updated_successfully,
                                Toast.LENGTH_SHORT).show()
                            openAddressListFragment()
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

    private fun openAddressListFragment() {
        requireActivity().supportFragmentManager.popBackStack(MapFragment::class.java.simpleName, 0)
        requireActivity().supportFragmentManager.popBackStack()
    }
}
