package com.emall.net.fragment.createProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.emall.net.listeners.ItemClick
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.SellYourDeviceAdapter
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.ProductInfo
import com.emall.net.network.model.AddProduct
import com.emall.net.utils.*
import com.emall.net.viewmodel.ViewModelFactory
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.showPopUp
import kotlinx.android.synthetic.main.fragment_sell.*
import kotlinx.android.synthetic.main.fragment_sell.progress_bar

class SellFragment : Fragment(), ItemClick, View.OnClickListener {

    private lateinit var adapter: SellYourDeviceAdapter
    private val productList = ArrayList<ProductInfo>()
    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var position: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_device))
        setupDrawerList()
    }

    private fun setupDrawerList() {
        adapter = SellYourDeviceAdapter(productList, this)
        select_device_recycler_view.setHasFixedSize(true)
        select_device_recycler_view.layoutManager = GridLayoutManager(activity, 2)
        select_device_recycler_view.adapter = adapter

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        prepareDrawerList()

        next_btn.setOnClickListener(this::onClick)
    }

    private fun prepareDrawerList() {
        productList.clear()
        viewModel.getProductCategories("Bearer $token", Utility.getLanguage()).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.let { it ->
                            progress_bar.makeGone()
                            productList.addAll(it.data)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> progress_bar.makeGone()
                    Status.LOADING -> progress_bar.makeVisible()
                }
            }
        })
    }

    override fun itemClick(position: Int) {
        productList[position].id
        this.position = position
        next_btn.setBackgroundResource(R.drawable.ic_orange_gradient)
    }

    override fun onClick(v: View) {
        if(position!! > -1) {
            if (v.id == R.id.next_btn) {
                val bundle = Bundle()
                val fragment = ProductInfoFragment()
                val addProduct = AddProduct(categoryId = productList[this.position!!].id)
                bundle.putParcelable(Constants.PRODUCT, addProduct)
                fragment.arguments = bundle
                (activity as SellerActivity).replaceFragment(fragment, R.id.container)
            }
        }else{
            constraint_layout.showPopUp(getString(R.string.select_any_category))
        }
    }
}