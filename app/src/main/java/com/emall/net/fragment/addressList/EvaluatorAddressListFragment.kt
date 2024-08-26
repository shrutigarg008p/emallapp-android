package com.emall.net.fragment.addressList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.EvaluatorAddressAdapter
import com.emall.net.fragment.address.*
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.evaluationUserAddress.*
import com.emall.net.utils.*
import com.emall.net.utils.Constants.DELETE
import com.emall.net.utils.Constants.EDIT
import com.emall.net.utils.Constants.LOGIN_TYPE
import com.emall.net.utils.Constants.PICKUP
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_saved_address.*
import kotlinx.android.synthetic.main.fragment_saved_address.add_new_address
import kotlinx.android.synthetic.main.fragment_saved_address.progress_bar

class EvaluatorAddressListFragment : Fragment(), OnItemClick {

    private lateinit var evaluatorAddressAdapter: EvaluatorAddressAdapter
    private var evaluatorAddressList = ArrayList<Address>()

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var shipmentId: Int? = 0
    private var auctionWonId: Int? = 0
    private var evaluationWonId: Int? = 0
    private var TAG: String = EvaluatorAddressListFragment::class.java.simpleName
    private var pos: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        when {
            arguments != null -> {
                shipmentId = arguments?.getInt("shipmentId")
                auctionWonId = arguments?.getInt("auctionWonId")
                evaluationWonId = arguments?.getInt("evaluationWonId")
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        address_recycler_view.setHasFixedSize(true)
        address_recycler_view.layoutManager = LinearLayoutManager(activity)
        evaluatorAddressAdapter = EvaluatorAddressAdapter(evaluatorAddressList, this)
        address_recycler_view.adapter = evaluatorAddressAdapter

        prepareDrawerList()

        add_new_address.setOnClickListener {
            (activity as BuyerActivity).replaceFragment(ReCommerceAddressFragment(), R.id.container)
        }
        pickup_address_btn.text = "Delivery Address"

        pickup_address_btn.setOnClickListener {
            if (pos!! >= 0) {
                if (PreferenceHelper.readString(LOGIN_TYPE)!!.contentEquals(Constants.SELLER)) {
                    viewModel.auctionDeviceWonProceedCheckout(
                        auctionWonId!!,
                        "Bearer $token",
                        shipmentId!!,
                        Utility.getLanguage()
                    ).observe(viewLifecycleOwner, {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.let { it ->
                                        scrollView.snack(it.MESSAGE)
                                        evaluatorAddressAdapter.notifyDataSetChanged()
                                    }
                                }
                                Status.LOADING -> {
                                }
                                Status.ERROR -> scrollView.snack(it.message!!)
                            }
                        }
                    })
                } else {
                    viewModel.evaluationDeviceWonProceedCheckout(evaluationWonId!!,
                        "Bearer $token",
                        shipmentId!!,Utility.getLanguage()).observe(viewLifecycleOwner, {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.let { it ->
                                        scrollView.snack(it.MESSAGE)
                                        evaluatorAddressAdapter.notifyDataSetChanged()
                                    }
                                }
                                Status.LOADING -> {
                                }
                                Status.ERROR -> scrollView.snack(it.message!!)
                            }
                        }
                    })
                }
            }
        }

    }

    private fun prepareDrawerList() {

        evaluatorAddressList.clear()
        viewModel.getEvaluationAddressList("Bearer $token",Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data.let { it ->
                                progress_bar.makeGone()
                                evaluatorAddressList.addAll(it!!)
                                evaluatorAddressAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.LOADING -> progress_bar.makeVisible()
                        Status.ERROR -> progress_bar.makeGone()
                    }
                }
            })
    }

    override fun onItemClick(position: Int, type: String) {
        val address = evaluatorAddressList[position]
        when (type) {
            EDIT -> {
                val fragment = ReCommerceAddressFragment()
                val bundle = Bundle()
                bundle.putInt("addressId", address.id)
                bundle.putBoolean("update", true)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
            }
            DELETE -> {
                viewModel.deleteAddressForEvaluationUser(address.id, "Bearer $token",Utility.getLanguage())
                    .observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data.let { it ->
                                        progress_bar.makeGone()
                                        scrollView.snack(it!!.MESSAGE)
                                        evaluatorAddressAdapter.notifyDataSetChanged()
                                    }
                                }
                                Status.ERROR -> progress_bar.makeGone()
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }
                    })
            }
            PICKUP -> {
                when {
                    position >= 0 -> {
                        pos = position
                        pickup_address_btn.setBackgroundResource(R.drawable.ic_orange_gradient_btn_color)
                    }
                    else -> pickup_address_btn.setBackgroundResource(R.color.unselected_color)
                }
            }
        }
    }
}