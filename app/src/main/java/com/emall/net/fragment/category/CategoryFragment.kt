package com.emall.net.fragment.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.CategoryImageAdapter
import com.emall.net.adapter.CategoryItemAdapter
import com.emall.net.adapter.CategoryListAdapter
import com.emall.net.fragment.product.ProductListFragment
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnCategoryItemClick
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.CategoryImageList
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.categoryList.Data
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment(), OnItemClick, ItemClick, OnCategoryItemClick {

    private lateinit var categoryItemAdapter: CategoryItemAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var categoryImageAdapter: CategoryImageAdapter
    private lateinit var viewModel: MainViewModel

    private val categoryList = ArrayList<Data>()
    private val categoryItemList = ArrayList<String>()
    private val categoryImageList = ArrayList<CategoryImageList>()
    var van = 0
    var mainlistposition: Int = 0
    var leftlistposition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.categories))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.categories))
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setupDrawerList()


    }

    private fun setupDrawerList() {
        categoryItemAdapter = CategoryItemAdapter(categoryItemList, this)
        categories_recycler_view.setHasFixedSize(true)
        categories_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        categories_recycler_view.adapter = categoryItemAdapter

        categoryListAdapter = CategoryListAdapter(categoryList, this)
        categories_type_recycler_view.setHasFixedSize(true)
        categories_type_recycler_view.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categories_type_recycler_view.adapter = categoryListAdapter

        categoryImageAdapter = CategoryImageAdapter(categoryImageList, this)
        categories_image_recycler_view.setHasFixedSize(true)
        categories_image_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        categories_image_recycler_view.adapter = categoryImageAdapter
        prepareDrawerList()
    }

    private fun prepareDrawerList() {
        categoryList.clear()
        viewModel.getCategoryList(Utility.getStoreCode(),
            Utility.getDeviceType(),
            Utility.getLanguage()).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let {
                            categoryList.addAll(it.data)
                            categoryListAdapter.notifyDataSetChanged()
                            categoryItemList.clear()
                            categoryItemList.add("Promotions")
                            for (item in categoryList[0].subcategory) {
                                categoryItemList.add(item.name)
                            }
                            categoryItemAdapter.notifyDataSetChanged()
                            categoryImageList.clear()
                            categoryImageList.add(
                                CategoryImageList(
                                    categoryList[0].promotionals[0].image
                                )
                            )
                            categoryImageAdapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Log.e("error", it1) }
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    override fun onItemClick(position: Int, type: String) {
        if (type == "categoryImageAdapter") {
            openProductListFragment(position)
        } else {
            leftlistposition = position
            when {
                type.equals("Promotions", true) -> {
                    categoryImageList.clear()
                    categoryImageList.add(CategoryImageList(categoryList[van].promotionals[position].image));
                    categoryImageAdapter.notifyDataSetChanged()
                }
                else -> {
                    categoryImageList.clear()
                    categoryList[van].subcategory[position - 1].banners.forEach {
                        categoryImageList.add(CategoryImageList(it.image));
                        categoryImageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun openProductListFragment(position: Int) {
        PreferenceHelper.writeBoolean(Constants.IS_FROM_HOME_FRAGMENT, false)
        val bundle = Bundle()

        if (leftlistposition == 0) {
            bundle.putInt("productid",
                categoryList[mainlistposition].promotionals[position].id.toInt())
        } else {
            bundle.putInt("productid",
                categoryList[mainlistposition].subcategory[leftlistposition - 1].banners[position].id.toInt())
        }
        val transaction = this.parentFragmentManager.beginTransaction()
        val frag2 = ProductListFragment()
        frag2.arguments = bundle
        transaction.add(R.id.container, frag2)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun itemClick(position: Int) {

        categoryItemAdapter = CategoryItemAdapter(categoryItemList, this)
        categories_recycler_view.setHasFixedSize(true)
        categories_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        categories_recycler_view.adapter = categoryItemAdapter

        van = position
        leftlistposition = 0
        categoryItemList.clear()
        categoryItemList.add("Promotions")
        for (item in categoryList[position].subcategory) {
            categoryItemList.add(item.name)
        }
        categoryItemAdapter.notifyDataSetChanged()
        categoryImageList.clear()
        categoryImageList.add(
            CategoryImageList(
                categoryList[position].promotionals.get(
                    0
                ).image
            )
        );
        categoryImageAdapter.notifyDataSetChanged()
        mainlistposition = position
    }

    override fun onCategoryItemClick(type: String, name: String) {
        when {
            type.equals("categoriesList", true) -> {
            }
        }
    }

}