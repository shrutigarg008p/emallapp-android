package com.emall.net.fragment.auction

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.AuctionAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.auctions.auctionList.Auction
import com.emall.net.utils.*
import com.emall.net.utils.Constants.PRODUCT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_auction.*

class AuctionFragment : Fragment(), ItemClick {

    private val auctionItemList = ArrayList<Auction>()
    private var token: String? = ""
    private var itemName: String? = ""
    private lateinit var viewModel: MainViewModel
    private lateinit var auctionAdapter: AuctionAdapter
    private var productId: Int? = null
    private val TAG = AuctionFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)
        when {
            arguments != null -> {
                productId = arguments?.getInt(PRODUCT_ID)
                itemName = arguments?.getString("name")
            }
        }

        heading.text = itemName

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        auction_list_recycler_view.setHasFixedSize(true)
        auction_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        auctionAdapter = AuctionAdapter(auctionItemList, this)
        auction_list_recycler_view.adapter = auctionAdapter

        setData()
    }

    private fun setData() {
        viewModel.getAuctionList(productId!!, "Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.data?.let { it ->
                            auctionItemList.clear()
                            progress_bar.makeGone()
                            auctionItemList.addAll(it)
                            auctionAdapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> progress_bar.makeGone()
                    Status.LOADING -> progress_bar.makeVisible()
                }
            }
        })
    }

    override fun itemClick(position: Int) {
        val fragment = BiddingFragment()
        val bundle = Bundle()
        Log.d(TAG, "itemClick: $productId")
        bundle.putInt(PRODUCT_ID,productId!!)
        bundle.putInt("auctionId",auctionItemList[position].id)
        bundle.putString("name",itemName)
        fragment.arguments = bundle
        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
    }
}