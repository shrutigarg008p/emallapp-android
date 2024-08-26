package com.emall.net.fragment.wishList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.WishListAdapter
import com.emall.net.fragment.product.ProductDetailFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.addToCartRequest.simpleProducts.AddToCartSimpleParams
import com.emall.net.network.model.addToCartRequest.simpleProducts.CartItemSimple
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequest
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequestParams
import com.emall.net.network.model.wishlistResponse.Data
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_wish_list.*

class WishListFragment : Fragment(), OnItemClick {
    private lateinit var viewModel: MainViewModel
    private var wishListItems = ArrayList<Data>()
    private lateinit var wishListAdapter: WishListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("wishlist")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.wishlist))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("wishlist")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.wishlist))
            }
        }
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUpWishlistFragment()
    }

    private fun setUpWishlistFragment() {
        wish_list_recycler_view.setHasFixedSize(true)
        wish_list_recycler_view.layoutManager = LinearLayoutManager(activity)
        getWishlistItems()
    }

    private fun getWishlistItems() {
        viewModel.getWishlistItems(PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    wishListItems.clear()
                                    wishListItems = it.data
                                    resetWishListItemArray()
                                    if (wishListItems.size > 0) {
                                        wish_list_recycler_view.makeVisible()
                                        no_item_in_wish_list.makeGone()
                                        wishListAdapter = WishListAdapter(wishListItems, this)
                                        wish_list_recycler_view.adapter = wishListAdapter
                                        wishListAdapter.notifyDataSetChanged()
                                    } else {
                                        wish_list_recycler_view.makeGone()
                                        no_item_in_wish_list.makeVisible()
                                    }
                                    wishListItemCount(wishListItems.size)
                                }
                            }
                            Status.ERROR -> {
                                it.message?.let { it1 -> Log.e("error", it1) }
                                activity?.let { ViewUtils.hideProgressBar() }

                            }
                            Status.LOADING -> {
                                activity?.let { ViewUtils.showProgressBar(it) }
                            }
                        }
                    }
                })
    }

    private fun resetWishListItemArray() {
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.clear()
            else -> (activity as BuyerActivity).wishListItemsId.clear()
        }
        for (items in wishListItems) {
            when (activity) {
                is SellerActivity -> {
                    (activity as SellerActivity).wishListItemsId.add(items.product_id)
                }
                else -> {
                    (activity as BuyerActivity).wishListItemsId.add(items.product_id)
                }
            }
        }
    }

    private fun wishListItemCount(count: Int) {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).wishListItemCount(
                    count)
            }
            else -> {
                (activity as BuyerActivity).wishListItemCount(
                    count)
            }
        }
    }

    override fun onItemClick(position: Int, type: String) {
        when {
            type.equals("itemDelete", true) -> {
                confirmDeleteDialog(position)
            }
            type.equals("itemImage", true) -> {
                openProductDetailFragment(position)
            }
            else -> {
                addToCart(position)
            }
        }
    }

    private fun confirmDeleteDialog(position: Int) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(R.string.delete_item_confirmation_message_from_wishlist)
        alertDialog.setPositiveButton(
            getText(R.string.delete)
        ) { _, _ ->
            removeFromWishlist(position)
        }
        alertDialog.setNegativeButton(
            getText(R.string.cancel)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_color));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.selected_color));
    }

    //    remove item from wishlist
    private fun removeFromWishlist(position: Int) {
        val removeFromWishlistRequest = RemoveFromWishlistRequest(
            param = RemoveFromWishlistRequestParams(
                PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!.toString(),
                wishListItems[position].item_id
            )
        )
        viewModel.removeFromWishlist(removeFromWishlistRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            getWishlistItems()
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

    //    open product detail fragment
    private fun openProductDetailFragment(position: Int) {
        val bundle = Bundle()
        bundle.putInt("productid", wishListItems[position].product_id.toInt())
        bundle.putString("from", "wishlist")
        bundle.putString("itemId", wishListItems[position].item_id)
        val transaction = this.parentFragmentManager.beginTransaction()
        val fragment = ProductDetailFragment()
        fragment.arguments = bundle
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    //    add to cart
    private fun addToCart(position: Int) {
        if (wishListItems[position].type.equals("simple", true)) {
            if (PreferenceHelper.readString(Constants.CUSTOMER_TOKEN) != "") {
                getQuoteIdFunc(position)
            } else {
                getCustomerToken(
                    PreferenceHelper.readString(Constants.CUSTOMER_EMAIL)!!,
                    PreferenceHelper.readString(
                        Constants.CUSTOMER_PHONE_NUMBER)!!, position
                )
            }
        } else {
            openProductDetailFragment(position)
        }
    }

    //    get quote id function
    private fun getQuoteIdFunc(position: Int) {
        activity?.let { ViewUtils.showProgressBar(it) }
        val quoteId = QuoteIdParams(PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!)
        val token = PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)
        viewModel.getQuoteId("Bearer $token", quoteId).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        addSimpleItemToCart(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!,
                            resource.data.toString(), position)
                    }
                    Status.ERROR -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    //    get customer token
    private fun getCustomerToken(email: String, mobileNumber: String, position: Int) {
        val userInfo = CustomerTokenParams(param = CustomerParams(email, mobileNumber, ""))
        viewModel.getCustomerToken(userInfo, Utility.getLanguage()).observe(viewLifecycleOwner,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            PreferenceHelper.writeString(
                                Constants.CUSTOMER_TOKEN,
                                resource.data.toString()
                            )
                            getQuoteIdFunc(position)
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    //    add simple item to cart
    private fun addSimpleItemToCart(token: String, quoteId: String, position: Int) {
        val productDetail = AddToCartSimpleParams(
            cart_item = CartItemSimple(
                wishListItems[position].sku,
                1,
                quoteId
            )
        )
        viewModel.addToCartSimple("Bearer $token", productDetail)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(requireContext(),
                                R.string.product_added_to_cart,
                                Toast.LENGTH_SHORT).show()
                            removeFromWishlist(position)
                            getCartItemCount()
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun getCartItemCount() {
        activity?.let { ViewUtils.hideProgressBar() }
        var totalCartItems = 0
        totalCartItems = PreferenceHelper.readInt(Constants.TOTAL_CART_ITEMS)!! + 1
        PreferenceHelper.writeInt(Constants.TOTAL_CART_ITEMS, totalCartItems)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).cartItemCount(
                    totalCartItems)
            }
            else -> {
                (activity as BuyerActivity).cartItemCount(
                    totalCartItems)
            }
        }
    }

}