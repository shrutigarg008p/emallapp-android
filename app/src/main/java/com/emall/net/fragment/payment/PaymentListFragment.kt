package com.emall.net.fragment.payment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.PaymentListAdapter
import com.emall.net.model.PaymentData
import com.emall.net.network.api.*
import com.emall.net.utils.*
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_payment_list.*

class PaymentListFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private lateinit var paymentListAdapter: PaymentListAdapter
    private var paymentList = ArrayList<PaymentData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_payment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.my_payments))
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        payment_list_recycler_view.setHasFixedSize(true)
        payment_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        paymentListAdapter = PaymentListAdapter(paymentList)
        payment_list_recycler_view.adapter = paymentListAdapter

        viewModel.getPaymentList("Bearer $token", 1, Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            it.data?.DATA?.data.let { it ->
                                paymentList.clear()
                                paymentList.add(PaymentData(0, "Payment For", "Amount","Mode", "Status"))
                                it!!.forEach { data ->
                                    paymentList.add(
                                        PaymentData(
                                            data.id,
                                            data.action,
                                            data.amount,
                                            "offline",
                                            data.status
                                        )
                                    )
                                }
                                paymentListAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {
                            scrollView.snack(it.data?.MESSAGE!!)
                        }
                    }
                }
            })


    }
}