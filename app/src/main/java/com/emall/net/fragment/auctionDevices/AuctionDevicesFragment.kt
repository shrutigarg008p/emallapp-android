package com.emall.net.fragment.auctionDevices

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.AuctionDevicesAdapter
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.auctionDevices.DATAX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_product.*

class AuctionDevicesFragment : Fragment(), OnItemClick {

    private lateinit var auctionDevicesAdapter: AuctionDevicesAdapter
    private val auctionDevicesList = ArrayList<DATAX>()
    private lateinit var viewModel: MainViewModel
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        sell_a_new_device_btn.makeGone()
        heading.makeGone()
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        setupData()
    }

    private fun setupData() {
        auction_recycler_view.setHasFixedSize(true)
        auction_recycler_view.layoutManager = LinearLayoutManager(activity)

        auctionDevicesAdapter = AuctionDevicesAdapter(auctionDevicesList, this)
        auction_recycler_view.adapter = auctionDevicesAdapter

        prepareData()
    }

    private fun prepareData() {
        viewModel.getAuctionDevices("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                progress_bar.makeGone()
                                auctionDevicesList.clear()
                                auctionDevicesList.addAll(it)
                                auctionDevicesAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.ERROR -> progress_bar.makeGone()
                        Status.LOADING -> progress_bar.makeVisible()
                    }
                }
            })
    }

    override fun onItemClick(position: Int, type: String) {
        when {
            type.equals("bid", true) -> {
                val fragment = AuctionDeviceDetailFragment()
                val bundle = Bundle()
                bundle.putInt("auctionId", auctionDevicesList[position].id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
            }
            type.equals("add/remove", true) -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("auction_id", auctionDevicesList[position].product.id)
                viewModel.toggleAuctionWishList("Bearer $token", Utility.getLanguage(), jsonObject)
                    .observe(viewLifecycleOwner, {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.MESSAGE?.let { it ->
                                        scrollView.snack(it)
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