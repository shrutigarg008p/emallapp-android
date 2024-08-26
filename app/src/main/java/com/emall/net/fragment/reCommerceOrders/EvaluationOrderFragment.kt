package com.emall.net.fragment.reCommerceOrders

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.EvaluationOrderAdapter
import com.emall.net.network.model.evaluationOrders.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_evaluation_order.*
import kotlinx.coroutines.launch

class EvaluationOrderFragment : BaseFragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private lateinit var evaluationOrderAdapter: EvaluationOrderAdapter
    private var evaluationOrderList = ArrayList<DataX>()

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
            evaluationOrderAdapter = EvaluationOrderAdapter(evaluationOrderList)
            order_list_recycler_view.adapter = evaluationOrderAdapter
            getOrders()
        }
    }

    private fun getOrders() {
        viewModel.getEvaluationOrders("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                evaluationOrderList.clear()
                                evaluationOrderList.addAll(it)
                                evaluationOrderAdapter.notifyDataSetChanged()
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