package com.emall.net.fragment.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.SearchAdapter
import com.emall.net.adapter.SearchHistoryAdapter
import com.emall.net.fragment.product.ProductDetailFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequest
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequestParams
import com.emall.net.network.model.searchRequest.SearchRequestData
import com.emall.net.network.model.searchRequest.SearchRequestParams
import com.emall.net.network.model.searchResponse.SearchResponseData
import com.emall.net.utils.*
import com.emall.net.utils.Utility.hideKeyboard
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment(), OnItemClick {
    private lateinit var viewModel: MainViewModel
    private var searchResponseData = ArrayList<SearchResponseData>()
    private lateinit var searchAdapter: SearchAdapter
    private var scrollListener: PaginationListener? = null
    var gridLayoutManager: GridLayoutManager? = null
    var currentPage = 1
    var query: String = ""
    var searchHistoryList = ArrayList<String>()
    var filteredList = ArrayList<String>()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("hide")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.search))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("hide")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.search))
            }
        }
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        if (savedInstanceState != null) {
            return
        } else {
            setUp()
        }

    }


    private fun setUp() {
        if (PreferenceHelper.getArrayList("searchHistory").isEmpty()) {
            PreferenceHelper.saveArrayList("searchHistory", searchHistoryList)
        } else {
            searchHistoryList = PreferenceHelper.getArrayList("searchHistory")
        }

        gridLayoutManager = GridLayoutManager(requireContext(), 2)
        search_recycler_view.layoutManager = gridLayoutManager
        searchAdapter = SearchAdapter(searchResponseData, this, requireActivity())
        search_recycler_view.adapter = searchAdapter
        scrollListener = object : PaginationListener(
            gridLayoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                currentPage++
                search(query, currentPage)
            }
        }
        search_recycler_view.addOnScrollListener(scrollListener as PaginationListener)
        search_history_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        searchHistoryAdapter = SearchHistoryAdapter(searchHistoryList, this)
        search_history_recycler_view.adapter = searchHistoryAdapter

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String): Boolean {
                if (text.isNotEmpty()) {
                    filter(text, searchHistoryAdapter)
                    search_history_recycler_view.makeVisible()
                } else {
                    search_history_recycler_view.makeGone()
                }
                return true
            }

            override fun onQueryTextSubmit(text: String): Boolean {
                query = text
                currentPage = 1
                search(query, currentPage)
                setSearchHistoryList()
                search_view.isFocusable = false
                search_view.isIconified = false
                search_view.clearFocus()
                return true
            }
        })
    }

    private fun filter(searchWord: String, searchHistoryAdapter: SearchHistoryAdapter) {
        filteredList.clear()
        for (text in searchHistoryList) {
            if (text.toLowerCase().contains(searchWord.toLowerCase())) {
                filteredList.add(text)
            }
        }
        searchHistoryAdapter.filterList(filteredList)
    }

    private fun setSearchHistoryList() {
        if (!PreferenceHelper.getArrayList("searchHistory").contains(query)) {
            if (PreferenceHelper.getArrayList("searchHistory").size < 10) {
                searchHistoryList.add(query)
                search_history_recycler_view.makeGone()
                searchHistoryAdapter.notifyDataSetChanged()
            } else {
                searchHistoryList.removeAt(0)
                searchHistoryList.add(query)
                search_history_recycler_view.makeGone()
                searchHistoryAdapter.notifyDataSetChanged()
            }
        }
        PreferenceHelper.saveArrayList("searchHistory", searchHistoryList)
    }


    private fun search(query: String, currentPage: Int) {
        search_fragment_layout.hideKeyboard()
        val searchRequest = SearchRequestParams(
            param = SearchRequestData(
                currentPage.toString(),
                query,
                Utility.getDeviceType()
            )
        )
        viewModel.search(searchRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            if (currentPage == 1) {
                                searchResponseData.clear()
                                scrollListener!!.resetState()
                            }
                            for (item in it.data!!.data) {
                                searchResponseData.add(item)
                            }
                            searchAdapter.notifyDataSetChanged()
                            if (currentPage == 1) {
                                if (searchResponseData.isEmpty()) {
                                    no_product_found_text.makeVisible()
                                    search_recycler_view.makeGone()
                                    Toast.makeText(requireContext(),
                                        R.string.no_product_found,
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    no_product_found_text.makeGone()
                                    search_recycler_view.makeVisible()
                                }
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    override fun onItemClick(position: Int, type: String) {
        if (type == "searchHistoryText") {
            search_view.setQuery(filteredList[position], true)
            search_history_recycler_view.makeGone()
            scrollListener!!.resetValues()
        } else if (type.equals("productDetail", true)) {
            val bundle = Bundle()
            bundle.putInt("productid", searchResponseData[position].id.toInt())
            bundle.putString("from", "")
            bundle.putString("itemId", "")
            val transaction = this.parentFragmentManager.beginTransaction()
            val frag2 = ProductDetailFragment()
            frag2.arguments = bundle
            transaction.add(R.id.container, frag2)
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        } else if (type.equals("remove", true)) {
            if (Utility.isCustomerLoggedIn()) {
                unTickWishlist(position)
            } else {
                openLoginDialog()
            }
        } else {
            if (Utility.isCustomerLoggedIn()) {
                addToWishList(position)
            } else {
                openLoginDialog()
            }
        }
    }

    private fun openLoginDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(R.string.must_be_logged_in)
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            PreferenceHelper.writeBoolean(Constants.FROM_CART, false)
            when (activity) {
                is SellerActivity -> {
                    (activity as SellerActivity).openActivity(LoginActivity::class.java)
                    (activity as SellerActivity).finish()
                }
                else -> {
                    (activity as BuyerActivity).openActivity(LoginActivity::class.java)
                    (activity as BuyerActivity).finish()
                }
            }
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun unTickWishlist(position: Int) {
        val addAndUntickWishListRequest = AddAndUntickWishListRequest(
            param = AddAndUntickWishListRequestParams(
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!.toString(),
                searchResponseData[position].id
            )
        )
        viewModel.unTickWishlist(addAndUntickWishListRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.product_removed_from_wishlist,
                                Toast.LENGTH_SHORT).show()
                            searchAdapter.notifyDataSetChanged()
                            removeFromWishListItemArray(position)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })

    }

    private fun addToWishList(position: Int) {
        val addAndUntickWishListRequest = AddAndUntickWishListRequest(
            param = AddAndUntickWishListRequestParams(
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!.toString(),
                searchResponseData[position].id
            )
        )
        viewModel.addToWishList(addAndUntickWishListRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.product_added_to_wishlist,
                                Toast.LENGTH_SHORT).show()
                            searchAdapter.notifyDataSetChanged()
                            addToWishListItemArray(position)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            if (it.message!!.contains("400")) {
                                Toast.makeText(requireContext(),
                                    "Not able to add to WishList ${searchResponseData[position].id}",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                            }
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun addToWishListItemArray(position: Int) {
        var count = 0
        count = PreferenceHelper.readInt(Constants.TOTAL_WISHLIST_ITEMS)!! + 1
        getWishlistItemsCount(count)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.add(
                searchResponseData[position].id)
            else ->
                (activity as BuyerActivity).wishListItemsId.add(
                    searchResponseData[position].id)
        }
        searchAdapter.notifyDataSetChanged()
    }

    private fun removeFromWishListItemArray(position: Int) {
        var count = 0
        count = PreferenceHelper.readInt(Constants.TOTAL_WISHLIST_ITEMS)!! - 1
        getWishlistItemsCount(count)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.remove(
                searchResponseData[position].id)
            else ->
                (activity as BuyerActivity).wishListItemsId.remove(
                    searchResponseData[position].id)
        }
        searchAdapter.notifyDataSetChanged()
    }

    private fun getWishlistItemsCount(count: Int) {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).wishListItemCount(count)
            }
            else -> {
                (activity as BuyerActivity).wishListItemCount(count)
            }
        }
    }
}