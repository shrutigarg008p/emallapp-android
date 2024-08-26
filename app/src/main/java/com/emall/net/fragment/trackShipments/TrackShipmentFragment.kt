package com.emall.net.fragment.trackShipments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.TrackShipmentAdapter
import com.emall.net.fragment.createProduct.SerialNumberWebViewFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.trackShipments.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Constants.EVALUATOR_ADDRESS
import com.emall.net.utils.Constants.SELLER_ADDRESS
import com.emall.net.utils.Constants.TRACK_MY_SHIPMENT
import com.emall.net.utils.Constants.WAY_BILL
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_track_shipment.*
import kotlinx.coroutines.launch

class TrackShipmentFragment : BaseFragment(), OnItemClick {

    private lateinit var viewModel: MainViewModel
    private var token: String = ""
    private lateinit var trackShipmentAdapter: TrackShipmentAdapter
    private var trackShipmentList = ArrayList<DataX>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_track_shipment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.track_sold_devices))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.track_sold_devices))
            }
        }

        scopeMainThread.launch {
            viewModel = getReCommerceViewModel()
            token = getReCommerceToken()
            shipment_list_recycler_view.setHasFixedSize(true)
            shipment_list_recycler_view.layoutManager = LinearLayoutManager(activity)
            trackShipmentAdapter =
                TrackShipmentAdapter(trackShipmentList, this@TrackShipmentFragment as OnItemClick)
            shipment_list_recycler_view.adapter = trackShipmentAdapter
            getTrackShipmentList()
        }


    }

    private fun getTrackShipmentList() {
        viewModel.getShipmentTrackingList("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                trackShipmentList.clear()
                                trackShipmentList.addAll(it)
                                trackShipmentAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.ERROR -> {
                            scrollView.snack(resource.data?.MESSAGE!!)
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            })
    }

    override fun onItemClick(position: Int, type: String) {
        scopeMainThread.launch {
            when {
                type.equals(WAY_BILL, true) -> {
                    // open pdf only
                    when (activity) {
                        is SellerActivity -> {
                            val fragment = WayBillFragment()
                            val bundle = Bundle()
                            bundle.putString(WAY_BILL, trackShipmentList[position].bill_sticker_pdf)
                            bundle.putString(
                                EVALUATOR_ADDRESS,
                                trackShipmentList[position].ship_to_str
                            )
                            bundle.putString(
                                SELLER_ADDRESS,
                                trackShipmentList[position].ship_from_str
                            )
                            fragment.arguments = bundle
                            (activity as SellerActivity).replaceFragment(fragment, R.id.container)
                        }
                    }

                }
                else -> {
                    // open track shipment webview
                    viewModel.trackShipmentDetails(
                        "Bearer $token",
                        Utility.getLanguage(),
                        trackShipmentList[position].id
                    )
                        .observe(viewLifecycleOwner, {
                            it.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        resource.data?.DATA?.let { it ->
                                            val fragment = SerialNumberWebViewFragment()
                                            val bundle = Bundle()
                                            bundle.putString(TRACK_MY_SHIPMENT, it)
                                            fragment.arguments = bundle
                                            when (activity) {
                                                is SellerActivity -> (activity as SellerActivity).replaceFragment(
                                                    fragment,
                                                    R.id.container
                                                )
                                                else -> (activity as BuyerActivity).replaceFragment(
                                                    fragment,
                                                    R.id.container
                                                )
                                            }
                                        }
                                    }
                                    Status.ERROR -> {
                                        scrollView.snack(resource.data?.MESSAGE!!)
                                    }
                                    Status.LOADING -> {

                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}