package com.emall.net.fragment.staticPages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.FAQListAdapter
import com.emall.net.model.FaqData
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_faq_ecom.*

class FaqEcomFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<FaqData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq_ecom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("staticPages")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.faq))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("staticPages")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.faq))
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
        faq_ecom_recycler_view.setHasFixedSize(true)
        faq_ecom_recycler_view.layoutManager = LinearLayoutManager(activity)
        faqListAdapter = FAQListAdapter(faqList)
        faq_ecom_recycler_view.adapter = faqListAdapter
        getFaqData()
    }

    private fun getFaqData() {
        viewModel.getFaqEcom(Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    faqList.clear()
                                    for (item in it.data) {
                                        faqList.add(FaqData(item.title, item.content))
                                    }
                                    faqListAdapter.notifyDataSetChanged()
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
