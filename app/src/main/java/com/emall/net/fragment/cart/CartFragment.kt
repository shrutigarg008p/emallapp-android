package com.emall.net.fragment.cart

import android.os.Bundle
import android.util.DisplayMetrics
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
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.CartAdapter
import com.emall.net.fragment.checkout.CheckOutFragment
import com.emall.net.fragment.product.ProductDetailFragment
import com.emall.net.fragment.search.PaginationListener
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.adminToken.AdminTokenParams
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.getCartItems.Item
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.setCurrencyToQuoteRequest.SetCurrencyToQuoteRequest
import com.emall.net.network.model.setCurrencyToQuoteRequest.SetCurrencyToQuoteRequestParam
import com.emall.net.network.model.updateCartItems.UpdateCartItem
import com.emall.net.network.model.updateCartItems.UpdateCartItemParams
import com.emall.net.network.model.updateCartItemsGuest.CartItemGuest
import com.emall.net.network.model.updateCartItemsGuest.UpdateCartItemGuestParams
import com.emall.net.utils.*
import com.emall.net.utils.Constants.COOKIES
import com.emall.net.utils.Constants.CUSTOMER_ID
import com.emall.net.utils.Constants.GUEST_MASK_KEY
import com.emall.net.utils.Constants.GUEST_QUOTE_ID
import com.emall.net.utils.Constants.TOTAL_CART_ITEMS
import com.emall.net.utils.Utility.isCustomerLoggedIn
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_cart.*


class CartFragment : Fragment(), OnItemClick {

    private lateinit var cartAdapter: CartAdapter
    private var cartItemList = ArrayList<Item>()
    private lateinit var viewModel: MainViewModel
    private var quoteId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.shopping_cart))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.shopping_cart))
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
        setupDrawerList()
    }

    private fun setupDrawerList() {
        shopping_recycler_view.setHasFixedSize(true)
        shopping_recycler_view.layoutManager = LinearLayoutManager(activity)

        when {
            isCustomerLoggedIn() -> {
                getCustomerToken(
                    PreferenceHelper.readString(Constants.CUSTOMER_EMAIL)!!,
                    PreferenceHelper.readString(
                        Constants.PASSWORD)!!)
             /*  if (PreferenceHelper.readString(Constants.CUSTOMER_TOKEN) != "") {
                    getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
                } else {
                    getCustomerToken(
                        PreferenceHelper.readString(Constants.CUSTOMER_EMAIL)!!,
                        PreferenceHelper.readString(
                            Constants.CUSTOMER_PHONE_NUMBER)!!)
                }*/
            }
            else -> {
                if (PreferenceHelper.readInt(GUEST_QUOTE_ID) == 0) {
                    getGuestToken()
                } else {

                    getCartItemListGuest(PreferenceHelper.readInt(GUEST_QUOTE_ID)!!)
                }
            }
        }
        cart_proceed_button.setOnClickListener(View.OnClickListener {
            when {
                isCustomerLoggedIn() -> {
                    openCheckOutFragment()
                }
                else -> {
                    openLoginDialog()
                }
            }
        })
    }

    private fun getGuestToken() {
        viewModel.getGuestToken().observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        PreferenceHelper.writeString(
                            GUEST_MASK_KEY,
                            resource.data?.mask_key.toString()
                        )
                        PreferenceHelper.writeInt(
                            GUEST_QUOTE_ID,
                            resource.data?.quote_id!!
                        )
                        getCartItemListGuest(PreferenceHelper.readInt(GUEST_QUOTE_ID)!!)
                    }
                    Status.ERROR -> {
                        Log.d(PaginationListener.TAG, "error: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d(PaginationListener.TAG, "loading: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    private fun openCheckOutFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(CheckOutFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(CheckOutFragment(),
                R.id.container)
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
            PreferenceHelper.writeBoolean(Constants.FROM_CART, true)
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

    //    api to get cart items for logged in users
    private fun getCartItemList() {
        viewModel.getCartItems(PreferenceHelper.readInt(CUSTOMER_ID)!!, Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                cart_layout.makeVisible()
                                resource.data?.let {
                                    subTotalValue.text =
                                        Utility.changedCurrencyPrice(it.data.subtotal)
                                    cartItemList.clear()
                                    cartItemList = it.data.items
                                    setRecyclerViewData()
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

    //    api to get cart items for guest users
    private fun getCartItemListGuest(quoteId: Int) {
        viewModel.getCartItemsGuest(quoteId, Utility.getLanguage())
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            cart_layout.makeVisible()
                            resource.data?.let {
                                subTotalValue.text = Utility.changedCurrencyPrice(it.data.subtotal)
                                cartItemList.clear()
                                cartItemList = it.data.items
                                setRecyclerViewData()
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

    private fun setRecyclerViewData() {
        getCartItemCount()
        if (cartItemList.size > 0) {
            cart_layout.makeVisible()
            no_item_in_cart_text.makeGone()
            if (cartItemList.size > 3) {
                val displayMetrics = DisplayMetrics()
                activity?.windowManager?.defaultDisplay?.getRealMetrics(
                    displayMetrics)
                val height = displayMetrics.heightPixels * 48 / 100
                shopping_recycler_view.layoutParams.height = height
                shopping_recycler_view.setPaddingRelative(0, 0, 10, 0)
                shopping_recycler_view.isVerticalScrollBarEnabled = true
            } else {
                shopping_recycler_view.setPaddingRelative(0, 0, 0, 0)
                shopping_recycler_view.isVerticalScrollBarEnabled = false
            }
            cartAdapter = CartAdapter(cartItemList, this)
            shopping_recycler_view.adapter = cartAdapter
            cartAdapter.notifyDataSetChanged()
        } else {
            cart_layout.makeGone()
            no_item_in_cart_text.makeVisible()
        }
    }

    override fun onItemClick(position: Int, type: String) {
        val isCustomerLoggedIn = isCustomerLoggedIn()
        when {
            type.equals("itemDelete", true) -> {
                confirmDeleteDialog(position, type, isCustomerLoggedIn)
            }
            type.equals("itemImage", true) -> {
                openProductDetailFragment(position)
            }
            else -> {
                val qty = type
                when {
                    isCustomerLoggedIn -> {
                        updateItemCount(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!,
                            quoteId,
                            position,
                            qty)
                    }
                    else -> {
                        updateItemCountGuest(position, qty)
                    }
                }
            }
        }

    }

    private fun getCustomerToken(email: String, mobileNumber: String) {
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
                            getQuoteIdFunc(PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)!!)
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun openProductDetailFragment(position: Int) {
        val bundle = Bundle()
        bundle.putInt("productid", cartItemList[position].product_id.toInt())
        bundle.putString("from", "")
        bundle.putString("itemId", "")
        val transaction = this.parentFragmentManager.beginTransaction()
        val fragment = ProductDetailFragment()
        fragment.arguments = bundle
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    private fun confirmDeleteDialog(position: Int, type: String, isCustomerLoggedIn: Boolean) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(R.string.delete_item_confirmation_message)
        alertDialog.setPositiveButton(
            getText(R.string.delete)
        ) { _, _ ->
            when {
                isCustomerLoggedIn -> getAdminToken(position)
                else -> deleteItemGuest(position)
            }
        }
        alertDialog.setNegativeButton(
            getText(R.string.cancel)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_color))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.selected_color))
    }


    private fun getAdminToken(position: Int) {
        val userInfo = AdminTokenParams(
            "apiuser",
            "Admin@1234"
        )
        viewModel.getAdminToken(userInfo).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        deleteItem(resource.data!!.toString(), quoteId.toInt(), position)
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    private fun updateItemCountGuest(position: Int, qty: String) {
        val productDetail = UpdateCartItemGuestParams(
            cart_item = CartItemGuest(
                cartItemList[position].id,
                qty,
                PreferenceHelper.readString(GUEST_MASK_KEY)!!,
                cartItemList[position].sku
            )
        )
        viewModel.updateCartItemsGuest(PreferenceHelper.readInt(GUEST_QUOTE_ID)!!,
            cartItemList[position].id,
            productDetail,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            getCartItemListGuest(PreferenceHelper.readInt(GUEST_QUOTE_ID)!!)
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

    private fun getQuoteIdFunc(token: String) {
        var quoteIdParams = QuoteIdParams(
            PreferenceHelper.readInt(CUSTOMER_ID)!!
        )
        viewModel.getQuoteId("Bearer $token", quoteIdParams).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        quoteId = resource.data!!
                        setCurrencyToQuote()
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    private fun setCurrencyToQuote() {
        val setCurrencyToQuoteRequest = SetCurrencyToQuoteRequest(
            param = SetCurrencyToQuoteRequestParam(
                quoteId.toInt(),
                PreferenceHelper.readString(Constants.SELECTED_CURRENCY)!!
            )
        )
        viewModel.setCurrencyToQuote(setCurrencyToQuoteRequest,
            Utility.getLanguage(), PreferenceHelper.readString(COOKIES)!!)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            getCartItemList()
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

    private fun updateItemCount(token: String, quoteId: String, position: Int, qty: String) {
        val productDetail = UpdateCartItemParams(
            cartItem = UpdateCartItem(
                cartItemList[position].id,
                qty.toInt(),
                quoteId,
                cartItemList[position].sku
            )
        )
        viewModel.updateCartItems("Bearer $token",
            cartItemList[position].id,
            productDetail,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            getCartItemList()
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

    private fun deleteItem(token: String, quoteId: Int, position: Int) {
        viewModel.deleteItem("Bearer $token",
            quoteId,
            cartItemList[position].id,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.item_deleted_successfully,
                                Toast.LENGTH_SHORT)
                            getCartItemList()
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

    private fun deleteItemGuest(position: Int) {
        viewModel.deleteItemGuest(PreferenceHelper.readString(GUEST_MASK_KEY)!!,
            cartItemList[position].id,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(requireContext(),
                                R.string.item_deleted_successfully,
                                Toast.LENGTH_SHORT)
                            getCartItemListGuest(PreferenceHelper.readInt(GUEST_QUOTE_ID)!!)
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

    private fun getCartItemCount() {
        var totalCartItems = 0
        for (item in cartItemList) {
            totalCartItems += item.qty
        }
        PreferenceHelper.writeInt(TOTAL_CART_ITEMS, totalCartItems)
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