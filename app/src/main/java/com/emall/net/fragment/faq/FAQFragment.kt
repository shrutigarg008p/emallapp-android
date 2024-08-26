package com.emall.net.fragment.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.adapter.FAQListAdapter
import com.emall.net.model.FaqData
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<FaqData>()
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        (activity as BuyerActivity).showHideToolbar("")
//        (activity as BuyerActivity).setToolbarTitle("FAQ")

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)!!
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        faq_recycler_view.setHasFixedSize(true)
        faq_recycler_view.layoutManager = LinearLayoutManager(activity)
        faqListAdapter = FAQListAdapter(faqList)
        faq_recycler_view.adapter = faqListAdapter

        viewModel.getFAQ("Bearer $token", Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        it.data?.DATA?.data?.let { it ->
                            faqList.clear()
                            for(item in it){
                                faqList.add(FaqData(item.faq_question, item.faq_answer))
                            }
                            faqListAdapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> {
                        scrollView.snack(it.data?.MESSAGE!!)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }
}