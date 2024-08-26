package com.emall.net.fragment.ecommerceAddress

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.AddressListAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.addressList.AddressListData
import com.emall.net.network.model.adminToken.AdminTokenParams
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.utils.*
import com.emall.net.utils.Constants.ADDRESS_TYPE
import com.emall.net.utils.Constants.SELECTED_ADDRESS
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
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_address_list.*


class AddressList : Fragment(), ItemClick {
    private lateinit var viewModel: MainViewModel
    private lateinit var addressListAdapter: AddressListAdapter
    private var addressList: ArrayList<AddressListData> = ArrayList()
    private var from: String = ""
    private var AddressId: String = ""
    private var FullName: String = ""
    private var City: String = ""
    private var ZipCode: String = ""
    private var Address: String = ""
    private var CountryCode: String = ""
    private var PhoneNumber: String = ""
    private var Latitude: String = ""
    private var Longitude: String = ""
    private var countryDetail = ArrayList<GetCountryDetailData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.shipping_address_title))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.shipping_address_title))
            }
        }
        if (arguments != null) from = arguments?.getString("from")!!
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUpAddressList()
    }

    private fun setUpAddressList() {
        address_list_recycler_view.setHasFixedSize(true)
        address_list_recycler_view.layoutManager = LinearLayoutManager(activity)

        if (from.equals("checkout", true)) {
            proceed_to_checkout.makeVisible()
        } else {
            proceed_to_checkout.makeGone()
        }

        add_new_address.setOnClickListener {
            openMapFragment("new")
        }

        proceed_to_checkout.setOnClickListener {
            redirectToPreviousFragment()
        }
        getCountryDetails()
    }

    private fun getCountryDetails() {
        activity?.let { ViewUtils.showProgressBar(it) }
        viewModel.getCountryDetail(Utility.getLanguage())
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
                                countryDetail.clear()
                                countryDetail = it.data
                                getAddressList()
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


    private fun openMapFragment(type: String) {
        PreferenceHelper.writeString(ADDRESS_TYPE, type)
        val bundle = Bundle()
        bundle.putString("type", type)
        bundle.putString("addressId", AddressId)
        bundle.putString("fullName", FullName)
        bundle.putString("city", City)
        bundle.putString("zipCode", ZipCode)
        bundle.putString("address", Address)
        bundle.putString("countryCode", CountryCode)
        bundle.putString("phoneNumber", PhoneNumber)
        bundle.putString("latitude", Latitude)
        bundle.putString("longitude", Longitude)
        var fragment = MapFragment()
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
        resetArguments()
    }

    private fun resetArguments() {
        AddressId = ""
        FullName = ""
        City = ""
        ZipCode = ""
        Address = ""
        CountryCode = ""
        PhoneNumber = ""
        Latitude = ""
        Longitude = ""
    }

    private fun redirectToPreviousFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).popBackStack()
            }
            else -> {
                (activity as BuyerActivity).popBackStack()
            }
        }
    }

    private fun getAddressList() {
        viewModel.getAddressList(PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!)
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    addressList.clear()
                                    addressList = it.data
                                    addressList.reverse()
                                    addressListAdapter = AddressListAdapter(addressList, this)
                                    address_list_recycler_view.adapter = addressListAdapter
                                    addressListAdapter.notifyDataSetChanged()
                                    Log.e("addressListAdapter", "addressListAdapter")
                                    if (addressList.size > 0) {
                                        Log.e("addressList", addressList.size.toString())
                                        if (addressList.size == 1) {
                                            setSelectedAddressData(0)
                                        }
                                    }
                                    val itemTouchHelper = ItemTouchHelper(object :
                                        SwipeHelper(address_list_recycler_view) {
                                        override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                                            var buttons = listOf<UnderlayButton>()
                                            val deleteButton = deleteButton(position)
                                            val editButton = editButton(position)
                                            when (position) {
                                                position -> buttons =
                                                    listOf(deleteButton, editButton)
                                                else -> Unit
                                            }
                                            return buttons
                                        }
                                    })
                                    itemTouchHelper.attachToRecyclerView(address_list_recycler_view)
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

    private fun editButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            getString(R.string.edit),
            14.0f,
            android.R.color.darker_gray,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    AddressId = addressList[position].address_id
                    FullName =
                        addressList[position].firstname + " ${addressList[position].lastname}"
                    City = addressList[position].city
                    ZipCode = addressList[position].postcode
                    Address = addressList[position].street
                    CountryCode = addressList[position].country_id
                    if (countryDetail.size > 0) {
                        for (item in countryDetail) {
                            if (addressList[position].telephone.startsWith(item.cel_code.toString())) {
                                PhoneNumber =
                                    addressList[position].telephone.replace(item.cel_code.toString(),
                                        "")
                                break
                            }
                        }
                    }
                    if (addressList[position].latitude != null) {
                        Latitude = addressList[position].latitude.toString()
                    }
                    if (addressList[position].longitude != null) {
                        Longitude = addressList[position].longitude.toString()
                    }
                    openMapFragment("edit")
                }
            })

    }


    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            getString(R.string.delete),
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    confirmDeleteAddressDialog(position)
                }
            })

    }

    private fun confirmDeleteAddressDialog(position: Int) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(R.string.delete_address_confirmation_message)
        alertDialog.setPositiveButton(
            getText(R.string.delete)
        ) { _, _ ->
            getAdminToken(position)
        }
        alertDialog.setNegativeButton(
            getText(R.string.cancel)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_color))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.selected_color))
    }

    private fun getAdminToken(position: Int) {
        val userInfo = AdminTokenParams(
            "apiuser",
            "Admin@1234"
        )
        viewModel.getAdminToken(userInfo).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        deleteAddress(position, resource.data.toString())
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    private fun deleteAddress(position: Int, token: String) {
        viewModel.deleteAddress("Bearer $token",
            addressList[position].address_id,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                "Address deleted successfully",
                                Toast.LENGTH_SHORT)
                            addressList.removeAt(position)
                            addressListAdapter.notifyDataSetChanged()
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

    override fun itemClick(position: Int) {
        if (position > 0) {
            setSelectedAddressData(position)
        }
    }

    private fun setSelectedAddressData(position: Int) {
        Log.e("addressList", addressList[position].toString())
        PreferenceHelper.writeString(SELECTED_ADDRESS, addressList[position].address_id!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_FIRST_NAME, addressList[position].firstname!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_LAST_NAME, addressList[position].lastname!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_STREET, addressList[position].street!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_CITY, addressList[position].city!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_POSTCODE, addressList[position].postcode!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_COUNTRY, addressList[position].country!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_COUNTRY_ID,
            addressList[position].country_id!!)
        PreferenceHelper.writeString(SELECTED_ADDRESS_PHONE_NUMBER,
            addressList[position].telephone!!)
    }
}