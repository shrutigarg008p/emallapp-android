package com.emall.net.fragment.addressList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.SellerAddressListAdapter
import com.emall.net.fragment.address.*
import com.emall.net.fragment.payment.ShipmentFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.sellerUserAddress.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_saved_address.*

class SellerAddressListFragment : Fragment(), OnItemClick {

    private lateinit var addressAdapter: SellerAddressListAdapter
    private var addressList = ArrayList<DataX>()

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var addressId: Int? = 0
    private var pos: Int? = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.shipping_address_title))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        address_recycler_view.setHasFixedSize(true)
        address_recycler_view.layoutManager = LinearLayoutManager(activity)

        addressAdapter = SellerAddressListAdapter(addressList, this)
        address_recycler_view.adapter = addressAdapter

        add_new_address.setOnClickListener {
            (activity as SellerActivity).replaceFragment(ReCommerceAddressFragment(),R.id.container)
        }
        addressList.clear()

        viewModel.getSellerAddressList("Bearer $token",Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data.let { it ->
                                progress_bar.makeGone()
                                addressList.addAll(it!!)
                                addressAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.ERROR -> progress_bar.makeGone()
                        Status.LOADING -> progress_bar.makeVisible()
                    }
                }
            })

        pickup_address_btn.text = "Pickup Address"
        pickup_address_btn.setOnClickListener {
            when {
                pos!! >= 0 -> {
                    val fragment = ShipmentFragment()
                    val bundle = Bundle()
                    bundle.putInt("addressId", addressId!!)
                    fragment.arguments = bundle
                    (activity as SellerActivity).replaceFragment(fragment,R.id.container)
                }
            }
        }
    }

    override fun onItemClick(position: Int, type: String) {
        val address = addressList[position]
        when (type) {
            Constants.EDIT -> {
                val fragment = ReCommerceAddressFragment()
                val bundle = Bundle()
                bundle.putInt("addressId", address.id)
                bundle.putBoolean("update", true)
                fragment.arguments = bundle
                (activity as SellerActivity).replaceFragment(fragment,R.id.container)
            }
            Constants.DELETE -> {
                viewModel.deleteAddressForSeller(address.id, "Bearer $token",Utility.getLanguage())
                    .observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data.let { it ->
                                        progress_bar.makeGone()
                                        scrollView.snack(it!!.MESSAGE)
                                        addressAdapter.notifyDataSetChanged()
                                    }
                                }
                                Status.ERROR -> progress_bar.makeGone()
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }
                    })
            }
            Constants.PICKUP -> {
                addressId = address.id
                when {
                    position >= 0 -> {
                        pos = position
                        pickup_address_btn.setBackgroundResource(R.drawable.ic_orange_gradient_btn_color)
                    }
                    else -> pickup_address_btn.setBackgroundResource(R.color.unselected_color)
                }
            }
        }
    }


}