package com.emall.net.fragment.evaluationDevices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.EvaluationDevicesAdapter
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.evaluationDevices.DataX
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_product.*

class EvaluationDevicesFragment : Fragment(), OnItemClick {

    private lateinit var evaluationDevicesAdapter: EvaluationDevicesAdapter
    private val evaluationDevicesList = ArrayList<DataX>()

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

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        auction_recycler_view.setHasFixedSize(true)
        auction_recycler_view.layoutManager = LinearLayoutManager(activity)
        sell_a_new_device_btn.makeGone()
        heading.makeGone()

        evaluationDevicesAdapter = EvaluationDevicesAdapter(evaluationDevicesList, this)
        auction_recycler_view.adapter = evaluationDevicesAdapter

        prepareData()
    }

    private fun prepareData() {
        viewModel.getEvaluationDevices("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                progress_bar.makeGone()
                                evaluationDevicesList.clear()
                                evaluationDevicesList.addAll(it)
                                evaluationDevicesAdapter.notifyDataSetChanged()
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
            type.equals("evaluate", true) -> {
                val fragment = EvaluationDevicesDetailFragment()
                val bundle = Bundle()
                bundle.putInt("evaluationId", evaluationDevicesList[position].id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
            }
            type.equals("add/remove",true) -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("evaluation_id",evaluationDevicesList[position].product.id)
                viewModel.toggleEvaluationToWishList("Bearer $token",Utility.getLanguage(),jsonObject).observe(viewLifecycleOwner,{
                    it.let { resource ->
                        when(resource.status){
                            Status.SUCCESS ->{
                                resource.data?.MESSAGE.let { it ->
                                    scrollView.snack(it!!)
                                }
                            }Status.ERROR ->{
                                scrollView.snack(resource.data?.MESSAGE!!)
                            }Status.LOADING ->{

                            }
                        }
                    }
                })
            }
        }
    }
}