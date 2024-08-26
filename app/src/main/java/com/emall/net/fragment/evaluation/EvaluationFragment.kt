package com.emall.net.fragment.evaluation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.EvaluationAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.evaluationDetail.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_auction.*

class EvaluationFragment : Fragment(),ItemClick {

    private val evaluationList = ArrayList<DataX>()
    private var token: String? = ""
    private var itemName: String? = ""
    private lateinit var viewModel: MainViewModel
    private lateinit var evaluationAdapter: EvaluationAdapter
    private var productId: Int? = null
    private val TAG = EvaluationFragment::class.java.simpleName


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
                productId = arguments?.getInt(Constants.PRODUCT_ID)
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
        evaluationAdapter = EvaluationAdapter(evaluationList, this)
        auction_list_recycler_view.adapter = evaluationAdapter

        viewModel.getEvaluationList(productId!!,"Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner,{
            it.let {resource ->
            when(resource.status){
                Status.SUCCESS ->{
                    resource.data?.DATA?.data.let{ it ->
                        progress_bar.makeGone()
                        evaluationList.clear()
                        evaluationList.addAll(it!!)
                        evaluationAdapter.notifyDataSetChanged()
                    }
                }Status.LOADING -> progress_bar.makeVisible()
                Status.ERROR-> progress_bar.makeGone()
            }
            }
        })
    }

    override fun itemClick(position: Int) {
        val fragment = EvaluationAcceptFragment()
        val bundle = Bundle()
        Log.d(TAG, "itemClick: $productId")
        bundle.putInt(Constants.PRODUCT_ID,productId!!)
        bundle.putInt("evaluationId",evaluationList[position].id)
        bundle.putString("name",itemName)
        fragment.arguments = bundle
        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
    }
}