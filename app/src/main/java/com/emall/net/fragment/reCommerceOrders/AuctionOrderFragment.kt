package com.emall.net.fragment.reCommerceOrders

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.AuctionOrderAdapter
import com.emall.net.network.model.auctionOrders.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_evaluation_order.*
import kotlinx.coroutines.launch

class AuctionOrderFragment : BaseFragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private lateinit var auctionOrderAdapter: AuctionOrderAdapter
    private var auctionOrderList = ArrayList<DataX>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_evaluation_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.my_buying_orders))

        scopeMainThread.launch {
            viewModel = getReCommerceViewModel()
            token = getReCommerceToken()
            order_list_recycler_view.setHasFixedSize(true)
            order_list_recycler_view.layoutManager = LinearLayoutManager(activity)
            auctionOrderAdapter = AuctionOrderAdapter(auctionOrderList)
            order_list_recycler_view.adapter = auctionOrderAdapter
            getOrders()
        }

    }

    private fun getOrders() {
        viewModel.getAuctionOrders("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                // add order adapter
                                auctionOrderList.clear()
                                auctionOrderList.addAll(it)
                                auctionOrderAdapter.notifyDataSetChanged()
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