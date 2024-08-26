package com.emall.net.fragment.createProduct

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.ProductAdapter
import com.emall.net.fragment.auction.AuctionFragment
import com.emall.net.fragment.evaluation.EvaluationFragment
import com.emall.net.listeners.ItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.products.ProductList
import com.emall.net.utils.*
import com.emall.net.utils.Constants.IS_SELLER
import com.emall.net.utils.Constants.LOGIN_TYPE
import com.emall.net.utils.Constants.PRODUCT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : Fragment(), ItemClick {

    private lateinit var productAdapter: ProductAdapter
    private val productList = ArrayList<ProductList>()

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        setUpData()
    }

    private fun setUpData() {
        when {
            PreferenceHelper.readString(LOGIN_TYPE)!!.contentEquals(Constants.SELLER) -> {
                sell_a_new_device_btn.makeVisible()
                heading.makeVisible()
            }
            else -> {
                sell_a_new_device_btn.makeGone()
                heading.makeGone()
            }
        }
        auction_recycler_view.setHasFixedSize(true)
        auction_recycler_view.layoutManager = LinearLayoutManager(activity)
        productAdapter = ProductAdapter(productList, this)
        auction_recycler_view.adapter = productAdapter
        prepareData()
    }

    private fun prepareData() {
        when {
            PreferenceHelper.readBoolean(IS_SELLER)!! -> {
                // get product from auctions
                viewModel.getProductsForAuctions("Bearer $token",Utility.getLanguage())
                    .observe(viewLifecycleOwner,
                        {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        resource.data?.DATA?.data?.let { it ->
                                            progress_bar.makeGone()
                                            productList.clear()
                                            productList.addAll(it)
                                            productAdapter.notifyDataSetChanged()
                                        }
                                    }
                                    Status.ERROR -> progress_bar.makeGone()
                                    Status.LOADING -> progress_bar.makeVisible()
                                }
                            }
                        }
                    )
            }
            else -> {
                // get products from evaluations
                viewModel.getProductsForEvaluation("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.DATA?.data.let { it ->
                                    progress_bar.makeGone()
                                    productList.clear()
                                    productList.addAll(it!!)
                                    productAdapter.notifyDataSetChanged()
                                }
                            }
                            Status.LOADING -> progress_bar.makeVisible()
                            Status.ERROR -> progress_bar.makeGone()
                        }
                    }
                })
            }
        }
    }

    override fun itemClick(position: Int) {
        val fragment: Fragment = when {
            PreferenceHelper.readBoolean(IS_SELLER)!! -> AuctionFragment()
            else -> EvaluationFragment()
        }
        val bundle = Bundle()
        bundle.putInt(PRODUCT_ID, productList[position].id)
        bundle.putString("name", productList[position].name)
        fragment.arguments = bundle
        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
    }
}