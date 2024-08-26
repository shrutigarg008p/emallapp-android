package com.emall.net.fragment.auctionWishList

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.AuctionWishListAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.auctionWishList.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_wish_list.*
import kotlinx.coroutines.launch

class AuctionWishListFragment : BaseFragment(), ItemClick {

    private lateinit var viewModel: MainViewModel
    private var token: String = ""
    private lateinit var auctionWishListAdapter: AuctionWishListAdapter
    private var auctionWishList = ArrayList<DataX>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.wishlist))

        scopeMainThread.launch {
            viewModel = getReCommerceViewModel()
            token = getReCommerceToken()
            wish_list_recycler_view.setHasFixedSize(true)
            wish_list_recycler_view.layoutManager = LinearLayoutManager(activity)
            auctionWishListAdapter =
                AuctionWishListAdapter(auctionWishList, this@AuctionWishListFragment as ItemClick)
            wish_list_recycler_view.adapter = auctionWishListAdapter
            getWishList()
        }

    }

    private fun getWishList() {
        viewModel.getAuctionWishList("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                when {
                                    it.isNullOrEmpty() -> {
                                        wish_list_recycler_view.makeGone()
                                        no_item_in_wish_list.makeVisible()
                                    }
                                    else -> {
                                        wish_list_recycler_view.makeVisible()
                                        no_item_in_wish_list.makeGone()
                                        auctionWishList.clear()
                                        auctionWishList.addAll(it)
                                        auctionWishListAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            constraint_layout.snack(resource.data?.MESSAGE!!)
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            })
    }

    override fun itemClick(position: Int) {
        scopeMainThread.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("auction_id", auctionWishList[position].id)
            viewModel.toggleAuctionWishList("Bearer $token", Utility.getLanguage(), jsonObject)
                .observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { it ->
                                    constraint_layout.snack(it.MESSAGE)
                                }
                            }
                            Status.ERROR -> {
                                constraint_layout.snack(resource.data?.MESSAGE!!)
                            }
                            Status.LOADING -> {

                            }
                        }
                    }
                })
        }
    }


}