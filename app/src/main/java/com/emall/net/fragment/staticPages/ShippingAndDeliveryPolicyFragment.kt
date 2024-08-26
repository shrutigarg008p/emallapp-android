package com.emall.net.fragment.staticPages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_shipping_and_delivery_policy.*

class ShippingAndDeliveryPolicyFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipping_and_delivery_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("staticPages")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.shipping_and_delivery_policy))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("staticPages")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.shipping_and_delivery_policy))
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
        shipping_and_delivery_policy_web_view.webViewClient = MyBrowser(requireContext())
        shipping_and_delivery_policy_web_view.settings.javaScriptEnabled = true
        getShippingAndDeliveryPolicyContent()
    }

    private fun getShippingAndDeliveryPolicyContent() {
        viewModel.getStaticPageData("shipping-policy",
            Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    shipping_and_delivery_policy_web_view.loadDataWithBaseURL(null,
                                        it.data,
                                        "text/html",
                                        "UTF-8",
                                        null)
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
}