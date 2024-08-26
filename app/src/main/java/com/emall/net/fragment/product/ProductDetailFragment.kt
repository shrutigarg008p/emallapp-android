package com.emall.net.fragment.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.AdditionalDataAdapter
import com.emall.net.adapter.ColorAdapter
import com.emall.net.adapter.SimilarProductsAdapter
import com.emall.net.fragment.search.PaginationListener
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.AdditionalData
import com.emall.net.model.ImageItem
import com.emall.net.model.SimilarProductsItem
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequest
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequestParams
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.AddToCartGuestConfigurableParams
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.CartItemConfigGuest
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.ExtensionAttributesConfigGuest
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.ProductOptionConfigGuest
import com.emall.net.network.model.addToCartGuestRequest.simpleProducts.AddToCartGuestSimpleParams
import com.emall.net.network.model.addToCartGuestRequest.simpleProducts.CartItemSimpleGuest
import com.emall.net.network.model.addToCartRequest.configurableProducts.*
import com.emall.net.network.model.addToCartRequest.simpleProducts.AddToCartSimpleParams
import com.emall.net.network.model.addToCartRequest.simpleProducts.CartItemSimple
import com.emall.net.network.model.configurableProduct.DataX
import com.emall.net.network.model.customerToken.CustomerParams
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequest
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequestParams
import com.emall.net.utils.*
import com.emall.net.utils.Constants.CUSTOMER_EMAIL
import com.emall.net.utils.Constants.CUSTOMER_ID
import com.emall.net.utils.Constants.CUSTOMER_PHONE_NUMBER
import com.emall.net.utils.Constants.CUSTOMER_TOKEN
import com.emall.net.utils.Constants.TOTAL_CART_ITEMS
import com.emall.net.utils.Constants.TOTAL_WISHLIST_ITEMS
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_otp_verify.*
import kotlinx.android.synthetic.main.fragment_product_detail.*

class ProductDetailFragment : Fragment(), OnItemClick, ItemClick {

    private lateinit var similarProductsAdapter: SimilarProductsAdapter
    private val productsItem = ArrayList<SimilarProductsItem>()
    private lateinit var colorAdapter: ColorAdapter
    private val colorItems = ArrayList<ImageItem>()
    private lateinit var additionalDataAdapter: AdditionalDataAdapter
    private val additionalData = ArrayList<AdditionalData>()
    private val specificationData = ArrayList<AdditionalData>()
    private var productId: Int = -1
    private var itemId: String? = ""
    private var from: String? = ""
    private var isExpanded = false
    private var isAddedToWishList = false
    private var isReturnExpanded = false
    private lateinit var viewModel: MainViewModel
    private var configurableData = ArrayList<DataX>()
    val imageList = ArrayList<SlideModel>()
    val zoomImageList = ArrayList<String>()
    private var count = 1
    private var typeOfProduct = ""
    private var sku: String = ""
    private var quoteId: Int = 0
    private var itemOptionList = ArrayList<ConfigurableItemOption>()
    private var inStock: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).showHideToolbar("hide")
            else -> (activity as BuyerActivity).showHideToolbar("hide")
        }

        if (arguments != null) {
            productId = arguments?.getInt("productid")!!
            itemId = arguments?.getString("itemId")
            from = arguments?.getString("from")
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUpData()
    }

    private fun setUpData() {
        similarProductsAdapter = SimilarProductsAdapter(productsItem, this)
        similarProductRecyclerView.setHasFixedSize(true)
        similarProductRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        similarProductRecyclerView.adapter = similarProductsAdapter

        colorAdapter = ColorAdapter(colorItems, this)
        color_recycler_view.setHasFixedSize(true)
        color_recycler_view.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        color_recycler_view.adapter = colorAdapter

        additionalDataAdapter = AdditionalDataAdapter(additionalData, this)
        additional_info_recyclerView.setHasFixedSize(true)
        additional_info_recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        additional_info_recyclerView.adapter = additionalDataAdapter

        additionalDataAdapter = AdditionalDataAdapter(specificationData, this)
        specifications_recyclerView.setHasFixedSize(true)
        specifications_recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        specifications_recyclerView.adapter = additionalDataAdapter

        prepareData()
        back_btn_container.setOnClickListener(View.OnClickListener {
            redirectToPreviousFragment()
        })
        specifications.setOnClickListener {
            if (!isExpanded) {
                isExpanded = true
                specifications_recyclerView.makeVisible()
            } else {
                isExpanded = false
                specifications_recyclerView.makeGone()
            }

        }
        shipping_n_return.setOnClickListener {
            if (!isReturnExpanded) {
                isReturnExpanded = true
                shipping_n_return_details.makeVisible()
            } else {
                isReturnExpanded = false
                shipping_n_return_details.makeGone()
            }

        }

        when (activity) {
            is SellerActivity -> {
                if ((activity as SellerActivity).wishListItemsId.contains(productId.toString())) {
                    addedToWishlist()
                } else {
                    notAddedToWishlist()
                }
            }
            else -> {
                if ((activity as BuyerActivity).wishListItemsId.contains(productId.toString())) {
                    addedToWishlist()
                } else {
                    notAddedToWishlist()
                }
            }
        }

        item_add_remove_container.setOnClickListener {
            if (Utility.isCustomerLoggedIn()) {
                if (!isAddedToWishList) {
                    isAddedToWishList = true
                    item_add_remove.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                        R.drawable.ic_heart));
                    addToWishList()
                } else {
                    isAddedToWishList = false
                    item_add_remove.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                        R.drawable.ic_heart_outline));
                    unTickWishlist()
                }
            } else {
                openLoginDialog()
            }
        }
        share_container.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.google.co.in/")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        increase_count.setOnClickListener {
            count++
            totalCount.text = count.toString();
        }
        decrease_count.setOnClickListener {
            if (count > 1) {
                count--
                totalCount.text = count.toString();
            }
        }

        add_to_cart_btn.setOnClickListener {
            if (!inStock) {
                Toast.makeText(requireContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show()
            } else {
                if (PreferenceHelper.readInt(CUSTOMER_ID) != 0) {
                    if (PreferenceHelper.readString(CUSTOMER_TOKEN) != "") {
                        getQuoteIdFunc(sku, count)
                    } else {
                        getCustomerToken(
                            PreferenceHelper.readString(CUSTOMER_EMAIL)!!,
                            PreferenceHelper.readString(
                                CUSTOMER_PHONE_NUMBER)!!
                        )
                    }
                } else if (typeOfProduct.equals("simple", true)) {
                    if (PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID) == 0) {
                        getGuestToken()
                    } else {
                        addSimpleItemToCartGuest(PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID)!!
                            .toInt(),
                            PreferenceHelper.readString(Constants.GUEST_MASK_KEY)!!, sku, count)
                    }
                } else {
                    if (PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID) == 0) {
                        getGuestToken()
                    } else {
                        addConfigurableItemToCartGuest(PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID)!!
                            .toInt(),
                            PreferenceHelper.readString(Constants.GUEST_MASK_KEY), sku, count)
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
                                CUSTOMER_TOKEN,
                                resource.data.toString()
                            )
                            getQuoteIdFunc(sku, count)
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
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
                            Constants.GUEST_MASK_KEY,
                            resource.data?.mask_key.toString()
                        )
                        PreferenceHelper.writeInt(
                            Constants.GUEST_QUOTE_ID,
                            resource.data?.quote_id!!
                        )
                        if (typeOfProduct.equals("simple", true)) {
                            addSimpleItemToCartGuest(PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID)!!
                                .toInt(),
                                PreferenceHelper.readString(Constants.GUEST_MASK_KEY)!!, sku, count)
                        } else {
                            addConfigurableItemToCartGuest(PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID)!!
                                .toInt(),
                                PreferenceHelper.readString(Constants.GUEST_MASK_KEY), sku, count)
                        }
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

    private fun addedToWishlist() {
        isAddedToWishList = true
        item_add_remove.setImageDrawable(ContextCompat.getDrawable(requireContext(),
            R.drawable.ic_heart))
    }

    private fun notAddedToWishlist() {
        isAddedToWishList = false
        item_add_remove.setImageDrawable(ContextCompat.getDrawable(requireContext(),
            R.drawable.ic_heart_outline))
    }

    private fun addToWishList() {
        val addAndUntickWishListRequest = AddAndUntickWishListRequest(
            param = AddAndUntickWishListRequestParams(
                PreferenceHelper.readInt(CUSTOMER_ID)!!.toString(),
                productId.toString()
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
                            addToWishListItemArray()
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            if (it.message!!.contains("400")) {
                                Toast.makeText(requireContext(),
                                    "Not able to add to WishList $productId", Toast.LENGTH_SHORT)
                                    .show()
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

    private fun unTickWishlist() {
        val addAndUntickWishListRequest = AddAndUntickWishListRequest(
            param = AddAndUntickWishListRequestParams(
                PreferenceHelper.readInt(CUSTOMER_ID)!!.toString(),
                productId.toString()
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
                            removeFromWishListItemArray()
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

    private fun redirectToPreviousFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).popBackStack()
            }
            else -> {
                (activity as BuyerActivity).popBackStack()
            }
        }
    }


    private fun prepareData() {
        activity?.let { ViewUtils.showProgressBar(it) }
        if (productId != null) {
            viewModel.productInfo(productId,Utility.getLanguage()).observe(viewLifecycleOwner, { it ->
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
                                itemName.text = it.data.name
                                inStock = it.data.stock
                                if (it.data.old_price > 0) {
                                    itemOldAmount.makeVisible()
                                } else {
                                    itemOldAmount.makeGone()
                                }
                                itemOldAmount.text = Utility.changedCurrencyPrice(it.data.old_price)
                                itemAmount.text = Utility.changedCurrencyPrice(it.data.price)
                                itemDetail.text = it.data.description
                                shipping_n_return_details.text = it.data.short_description
                                typeOfProduct = it.data.type
                                sku = it.data.sku
                                itemSku.text = "SKU: $sku"
                                for (item in it.data.image) {
                                    imageList.add(SlideModel(item,
                                        ScaleTypes.FIT))
                                }
                                imageSlider.setImageList(imageList)
                                for (item in imageList) {
                                    item.imageUrl?.let { zoomImageList.add(it) }
                                }
                                imageSlider.setItemClickListener(object : ItemClickListener {
                                    override fun onItemSelected(position: Int) {
                                        val fm = requireActivity().supportFragmentManager
                                        val bund = Bundle()
                                        bund.putStringArrayList("zoomimagelist", zoomImageList)
                                        bund.putInt("position", position)
                                        val alertDialog = ZoomImageFragment()
                                        alertDialog.arguments = bund
                                        alertDialog.show(fm, "fragment_alert")
                                    }
                                })
                                if (typeOfProduct.equals("simple", true)) {
                                    activity?.let { ViewUtils.hideProgressBar() }
                                    product_detail_fragment_layout.makeVisible()
                                    colorItems.clear()
                                    val colorItem =
                                        ImageItem(it.data.color_img)
                                    colorItems.add(colorItem)
                                    colorAdapter.notifyDataSetChanged()
                                    specificationData.clear()
                                    val item = it.data
                                    if (item.color_value.isNotEmpty())
                                        specificationData.add(AdditionalData("Color",
                                            item.color_name))
                                    additionalDataAdapter.notifyDataSetChanged()

                                } else {
                                    getConfigProductDetails(it.data.sku)
                                }
                            }
                        }
                        Status.ERROR -> {
                            it.message?.let { it1 -> Log.e("error", it1) }
                            activity?.let { ViewUtils.hideProgressBar() }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
        }

        productsItem.clear()
        var item =
            SimilarProductsItem("https://affordablephonesng.com/ap/wp-content/uploads/2019/09/apple-iphone-11-pro-max-4.jpg",
                "iPhone 11 Pro - silver 256 GB",
                "SAR 290")
        productsItem.add(item)
        item =
            SimilarProductsItem("https://i2.wp.com/maplestore.in/wp-content/uploads/2020/04/iPhoneXr-Black-1.png?fit=500%2C500&ssl=1",
                "iphone XR - white 120 GB",
                "SAR 256")
        productsItem.add(item)
        item =
            SimilarProductsItem("https://9to5mac.com/wp-content/uploads/sites/6/2021/06/Purple-iPhone-12-Pro-Max.jpeg?quality=82&strip=all",
                "iPhone 13",
                "SAR 256")
        productsItem.add(item)
        similarProductsAdapter.notifyDataSetChanged()

        additionalData.clear()
        /*Not handle language and data hardcode */
        /*var data = AdditionalData("Categories", "Smartphones")
        additionalData.add(data)
        data = AdditionalData("Year of Model", "2019")
        additionalData.add(data)*/
        /* handle language and data set blank */
        /*var data = AdditionalData(getString(R.string.categories), "Smartphones")
        additionalData.add(data)*/
        var data = AdditionalData(getString(R.string.year_of_model), "2019")
        additionalData.add(data)
        additionalDataAdapter.notifyDataSetChanged()

    }


    private fun getConfigProductDetails(sku: String) {
        viewModel.configurableProductDetails(sku,Utility.getLanguage()).observe(viewLifecycleOwner, { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        activity?.let { ViewUtils.hideProgressBar() }
                        resource.data?.let {
                            configurableData = it.data
                            itemOldAmount.text =
                                Utility.changedCurrencyPrice(configurableData[0].old_price)
                            itemAmount.text =
                                Utility.changedCurrencyPrice(configurableData[0].price)
                            product_detail_fragment_layout.makeVisible()
                            specificationData.clear()
                            val item = configurableData[0]
                            if (item.ram_value.isNotEmpty())
                                specificationData.add(AdditionalData("RAM", item.ram_value))
                            if (item.storage_value.isNotEmpty())
                                specificationData.add(AdditionalData("ROM", item.storage_value))
                            if (item.color_value.isNotEmpty())
                                specificationData.add(AdditionalData("Color", item.color_value))

                            additionalDataAdapter.notifyDataSetChanged()
                            itemOptionList.clear()
                            for (item in specificationData) {
                                when (item.itemTitle) {
                                    "RAM" -> {
                                        item.itemValue = configurableData[0].ram_info
                                        itemOptionList.add(ConfigurableItemOption(configurableData[0].ram_id,
                                            configurableData[0].ram_value.toInt()))
                                    }
                                    "ROM" -> {
                                        item.itemValue = configurableData[0].storage
                                        itemOptionList.add(ConfigurableItemOption(configurableData[0].storage_id,
                                            configurableData[0].storage_value.toInt()))
                                    }
                                    "Color" -> {
                                        item.itemValue = configurableData[0].color_name
                                        itemOptionList.add(ConfigurableItemOption(configurableData[0].color_id,
                                            configurableData[0].color_value.toInt()))
                                    }
                                    else -> {
                                        println("specificationData does not contains RAM, ROM, Color")
                                    }
                                }
                            }
                            additionalDataAdapter.notifyDataSetChanged()
                            colorItems.clear()
                            for (item in it.data) {
                                var colorItem =
                                    ImageItem(item.color_img)
                                colorItems.add(colorItem)
                            }
                            colorAdapter.notifyDataSetChanged()
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Log.e("error", it1) }
                        activity?.let { ViewUtils.hideProgressBar() }
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun getQuoteIdFunc(sku: String, count: Int) {
        activity?.let { ViewUtils.showProgressBar(it) }
        val quoteId = QuoteIdParams(PreferenceHelper.readInt(CUSTOMER_ID)!!)
        val token = PreferenceHelper.readString(CUSTOMER_TOKEN)
        viewModel.getQuoteId("Bearer $token", quoteId).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (typeOfProduct.equals("simple", true)) {
                            addSimpleItemToCart(PreferenceHelper.readString(CUSTOMER_TOKEN)!!,
                                resource.data.toString(), sku, count)
                        } else {
                            addConfigurableItemToCart(sku,
                                PreferenceHelper.readString(CUSTOMER_TOKEN)!!,
                                resource.data.toString())
                        }
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

    private fun addSimpleItemToCart(token: String, quoteId: String, sku: String, count: Int) {
        val productDetail = AddToCartSimpleParams(
            cart_item = CartItemSimple(
                sku,
                count,
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
                            if (from.equals("wishlist", true)) {
                                Log.e("from", "wishlist")
                                removeFromWishList()
                            }
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

    private fun addConfigurableItemToCart(sku: String,token: String, quoteId: String) {
        val productDetail = AddToCartConfigurableParams(
            cartItem = CartItem(
                sku,
                count,
                quoteId,
                product_option = ProductOption(
                    extension_attributes = ExtensionAttributes(
                        configurable_item_options = itemOptionList
                    )
                )
            )
        )
        viewModel.addToCartConfigurable(Utility.getLanguage(),"Bearer $token", productDetail)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(requireContext(),
                                R.string.product_added_to_cart,
                                Toast.LENGTH_SHORT).show()
                            if (from.equals("wishlist", true)) {
                                removeFromWishList()
                            }
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

    private fun removeFromWishList() {
        val removeFromWishListRequest = RemoveFromWishlistRequest(
            param = RemoveFromWishlistRequestParams(
                PreferenceHelper.readInt(CUSTOMER_ID)!!.toString(),
                itemId!!
            )
        )
        viewModel.removeFromWishlist(removeFromWishListRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            removeFromWishListItemArray()
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


    private fun addSimpleItemToCartGuest(quoteId: Int?, maskKey: String?, sku: String, count: Int) {
        activity?.let { ViewUtils.showProgressBar(it) }
        val productDetail = AddToCartGuestSimpleParams(
            cart_item = CartItemSimpleGuest(
                "simple",
                count,
                maskKey.toString(),
                sku)
        )
        viewModel.addToCartGuestSimple(Utility.getLanguage(),quoteId, productDetail)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(requireContext(),
                                R.string.product_added_to_cart, Toast.LENGTH_SHORT).show()
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

    private fun addConfigurableItemToCartGuest(
        quoteId: Int?,
        maskKey: String?,
        sku: String,
        count: Int,
    ) {
        Log.e("quoteId", quoteId.toString())
        Log.e("maskKey", maskKey.toString())
        Log.e("sku", sku.toString())
        Log.e("count", count.toString())
        activity?.let { ViewUtils.showProgressBar(it) }
        val productDetail = AddToCartGuestConfigurableParams(
            cartItem = CartItemConfigGuest(
                sku,
                count,
                maskKey.toString(),
                product_option = ProductOptionConfigGuest(
                    extension_attributes = ExtensionAttributesConfigGuest(
                        itemOptionList
                    )
                )
            )
        )
        viewModel.addToCartGuestConfigurable(quoteId, productDetail, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(requireContext(),
                                R.string.product_added_to_cart,
                                Toast.LENGTH_SHORT).show()
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

    override fun itemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int, type: String) {
        if (type.equals("colors", true)) {
            itemOldAmount.text = Utility.changedCurrencyPrice(configurableData[position].old_price)
            itemAmount.text = Utility.changedCurrencyPrice(configurableData[position].price)
            specificationData.clear()
            if (configurableData[position].ram_value.isNotEmpty())
                specificationData.add(AdditionalData("RAM", configurableData[position].ram_value))
            if (configurableData[position].storage_value.isNotEmpty())
                specificationData.add(AdditionalData("ROM",
                    configurableData[position].storage_value))
            if (configurableData[position].color_value.isNotEmpty())
                specificationData.add(AdditionalData("Color",
                    configurableData[position].color_value))
            itemOptionList.clear()
            for (item in specificationData) {
                when (item.itemTitle) {
                    "RAM" -> {
                        item.itemValue = configurableData[position].ram_info
                        itemOptionList.add(ConfigurableItemOption(configurableData[position].ram_id,
                            configurableData[position].ram_value.toInt()))
                    }
                    "ROM" -> {
                        item.itemValue = configurableData[position].storage
                        itemOptionList.add(ConfigurableItemOption(configurableData[position].storage_id,
                            configurableData[position].storage_value.toInt()))
                    }
                    "Color" -> {
                        item.itemValue = configurableData[position].color_name
                        itemOptionList.add(ConfigurableItemOption(configurableData[position].color_id,
                            configurableData[position].color_value.toInt()))
                    }
                    else -> {
                        println("specificationData does not contains RAM, ROM, Color")
                    }
                }
            }
            additionalDataAdapter.notifyDataSetChanged()
        }
    }

    private fun getCartItemCount() {
        activity?.let { ViewUtils.hideProgressBar() }
        var totalCartItems = 0
        totalCartItems = PreferenceHelper.readInt(TOTAL_CART_ITEMS)!! + count
        PreferenceHelper.writeInt(TOTAL_CART_ITEMS, totalCartItems)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).cartItemCount(totalCartItems)
            }
            else -> {
                (activity as BuyerActivity).cartItemCount(totalCartItems)
            }
        }
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

    private fun addToWishListItemArray() {
        var count = 0
        count = PreferenceHelper.readInt(TOTAL_WISHLIST_ITEMS)!! + 1
        getWishlistItemsCount(count)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.add(
                productId.toString())
            else ->
                (activity as BuyerActivity).wishListItemsId.add(
                    productId.toString())
        }
    }

    private fun removeFromWishListItemArray() {
        var count = 0
        count = PreferenceHelper.readInt(TOTAL_WISHLIST_ITEMS)!! - 1
        getWishlistItemsCount(count)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.remove(
                productId.toString())
            else ->
                (activity as BuyerActivity).wishListItemsId.remove(
                    productId.toString())
        }
    }
}
