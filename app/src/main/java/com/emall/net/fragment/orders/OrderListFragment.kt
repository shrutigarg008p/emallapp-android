package com.emall.net.fragment.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.OrderListAdapter
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.orderListResponse.OrderListResponseData
import com.emall.net.network.model.reOrderRequest.ReOrderRequest
import com.emall.net.network.model.reOrderRequest.ReOrderRequestParams
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_order_list.*

class OrderListFragment : Fragment(), OnItemClick {
    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var viewModel: MainViewModel
    private var orderList = ArrayList<OrderListResponseData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.my_orders))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.my_orders))
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
        orders_recycler_view.setHasFixedSize(true)
        orders_recycler_view.layoutManager = LinearLayoutManager(activity)
        getOrderList()
    }

    private fun getOrderList() {
        viewModel.getOrderList(PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    orderList.clear()
                                    orderList = it.data
                                    if (orderList.size > 0) {
                                        orders_recycler_view.makeVisible()
                                        no_order_in_my_orders.makeGone()
                                        orderListAdapter =
                                            OrderListAdapter(requireActivity(), orderList, this)
                                        orders_recycler_view.adapter = orderListAdapter
                                        orderListAdapter.notifyDataSetChanged()
                                    } else {
                                        orders_recycler_view.makeGone()
                                        no_order_in_my_orders.makeVisible()
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

    override fun onItemClick(position: Int, type: String) {
        if (type.equals("cancel", true)) {
            confirmCancelOrderDialog(position)
        } else {
            reOrder(position)
        }
    }

    private fun confirmCancelOrderDialog(position: Int) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(R.string.cancel_order_confirmation_message)
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            cancelOrder(position)
        }
        alertDialog.setNegativeButton(
            "No"
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

    private fun cancelOrder(position: Int) {
        viewModel.cancelOrder(orderList[position].order_id.toInt())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let {
                                    Toast.makeText(requireContext(),
                                        R.string.order_cancelled_message,
                                        Toast.LENGTH_SHORT).show()
                                    getOrderList()
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

    private fun reOrder(position: Int) {
        val reOrderRequest = ReOrderRequest(
            param = ReOrderRequestParams(
                orderList[position].increment_id,
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!.toString()
            )
        )
        viewModel.reOrder(reOrderRequest, Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                var count = PreferenceHelper.readInt(Constants.TOTAL_CART_ITEMS)!!
                                for (item in orderList[position].products) {
                                    count += item.quantity
                                }
                                getCartItemCount(count)
                                PreferenceHelper.writeInt(Constants.TOTAL_CART_ITEMS, count)
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    Toast.makeText(requireContext(),
                                        it.data,
                                        Toast.LENGTH_SHORT).show()
                                    getOrderList()
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

}