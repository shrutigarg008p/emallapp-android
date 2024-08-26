package com.emall.net.fragment.product

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.AdditionalDataAdapter
import com.emall.net.adapter.ProductListAdapter
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.AdditionalData
import com.emall.net.model.ImageItem
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.ProductListData
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequest
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequestParams
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponseList
import com.emall.net.network.model.getFilterNavigationListRequest.GetFilterNavigationListRequest
import com.emall.net.network.model.getFilterNavigationListRequest.GetFilterNavigationListRequestParam
import com.emall.net.network.model.getFilterNavigationResponse.GetFilterNavigationResponseData
import com.emall.net.network.model.getFilterNavigationResponse.GetFilterNavigationResponseDataList
import com.emall.net.utils.*
import com.emall.net.utils.Constants.IS_FROM_HOME_FRAGMENT
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : Fragment(), OnItemClick {

    private val colorItems = ArrayList<ImageItem>()
    private lateinit var additionalDataAdapter: AdditionalDataAdapter
    private val specificationData = ArrayList<AdditionalData>()
    private var productId: Int = 0
    private lateinit var viewModel: MainViewModel
    private var configurableData = ArrayList<ProductListData>()
    lateinit var mAdapter: ProductListAdapter
    var lm: GridLayoutManager? = null
    private var dialog: Dialog? = null
   /* private var selectstorageList = ArrayList<String>()
    private var selectramList = ArrayList<String>()
    private var selectcolorList = ArrayList<String>()
    private var selectsizeList = ArrayList<String>()*/
    private var recentlyArrived: TextView? = null
    private var highToLow: TextView? = null
    private var lowToHigh: TextView? = null
    private var defaultList: TextView? = null
    private var currentpage = 1
    private var tickImage: ImageView? = null
    private var tickImage2: ImageView? = null
    private var tickImage3: ImageView? = null
    private var tickImage4: ImageView? = null

    private var sortingflag = 0
    private var sortOrder = "desc"
    private var sortBy = "created_at"
    private var productType: String? = ""

    private var storageList = ArrayList<String>()
    private var ramList = ArrayList<String>()
    private var colorList = ArrayList<String>()
    private var sizeList = ArrayList<String>()
    private var filterData = ArrayList<GetFilterDataResponseList>()
    private var count: Int = 0
    private var OPEN_FILTER_FRAGMENT_REQUEST_CODE = 100
    private var OPEN_FILTER_FRAGMENT_RESULT_CODE = 99
    private var filterNavigationData = ArrayList<GetFilterNavigationResponseData>()
    private var filterNavigationDataList =     ArrayList<GetFilterNavigationResponseDataList>()
    private var filterNavigationDataList2 =     ArrayList<GetFilterNavigationResponseDataList>()
    private var filterNavigationDataList3 =     ArrayList<GetFilterNavigationResponseDataList>()
    private var filterNavigationDataList4 =     ArrayList<GetFilterNavigationResponseDataList>()
    //private lateinit var storagedata :GetFilterNavigationResponseData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getInt("productid")!!
        productType = arguments?.getString("productType")

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)


        lm = object : GridLayoutManager(view.context, 2) {
            override fun isLayoutRTL(): Boolean {
                return false
            }
        }
        if(PreferenceHelper.readBoolean(IS_FROM_HOME_FRAGMENT)!!){
            filter_sort_layout.makeGone()
        }else
            filter_sort_layout.makeVisible()
        getProductList()

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.latestproduct))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.latestproduct))
            }
        }

        sortapplybtn.setOnClickListener { initializeSortDialogue() }
        filterapplybtn.setOnClickListener(View.OnClickListener {
            openFilterFragment()
        })
    }

    private fun openFilterFragment() {
        /*val storageValues = storageList.joinToString { it -> it }
        val sizeValues = sizeList.joinToString { it -> it }
        val colorValues = colorList.joinToString { it -> it }
        val ramValues = ramList.joinToString { it -> it }

        val filternavdata = filterNavigationData.joinToString { it.data.get(0).value  }
        Log.e("Storage Value from productlist-->>",storageValues)
        Log.e("sizeValues from productlist-->>",sizeValues)
        Log.e("colorValues from productlist-->>",colorValues)
        Log.e("ramValues from productlist-->>",ramValues)
        Log.e("filternavedata from productlist-->>",filternavdata )*/

        val prFrag: DialogFragment = FilterFragment()
        val bundle = Bundle()
        bundle.putInt("categoryId", productId)
        bundle.putStringArrayList("storageList", storageList)
        bundle.putStringArrayList("ramList", ramList)
        bundle.putStringArrayList("colorList", colorList)
        bundle.putStringArrayList("sizeList", sizeList)
        bundle.putSerializable("filterNavigationData", filterNavigationData)

        prFrag.arguments = bundle
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        val prev = requireActivity().supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        prFrag.setTargetFragment(this, OPEN_FILTER_FRAGMENT_REQUEST_CODE)
        prFrag.show(ft, "dialog")
    }

    private fun getProductList() {
        activity?.let { ViewUtils.showProgressDialog(it) }
        sortOrder =
            when (sortingflag) {
                0 -> "asc"
                1 -> "desc"
                2 -> "asc"
                else -> "desc"
            }
        sortBy =
            when (sortingflag) {
                0 -> "position"
                1 -> "created_at"
                2 -> "price"
                else -> "price"
            }

        if (PreferenceHelper.readBoolean(IS_FROM_HOME_FRAGMENT)!!) {
            val params1 = JsonObject()
            val params = JsonObject()
            params.addProperty("attrib", productType)
            params.addProperty("type", Utility.getDeviceType())
            params1.add("param",params)
            viewModel.viewAllProducts(params1, Utility.getLanguage()).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            ViewUtils.hideProgressDialog()
                            resource.data?.let {
                                configurableData = it.data
                                initRecycler(view, configurableData)
                                itemOldAmount.text =
                                    Utility.changedCurrencyPrice(configurableData[0].old_price)
                                itemAmount.text =
                                    Utility.changedCurrencyPrice(configurableData[0].price)
                                for (item in specificationData) {
                                    when (item.itemTitle) {

                                    }
                                }
                                additionalDataAdapter.notifyDataSetChanged()
                                colorItems.clear()
                            }
                        }
                        Status.ERROR -> {
                            ViewUtils.hideProgressDialog()
                            it.message?.let { it1 -> Log.e("error", it1) }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
        } else {
            viewModel.getProductListData(
                Utility.getLanguage(),
                productId,
                Utility.getDeviceType(),
                currentpage,
                sortBy,
                sortOrder
            )
                .observe(
                    viewLifecycleOwner,
                    { it ->
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    ViewUtils.hideProgressDialog()
                                    resource.data?.let {
                                        configurableData = it.data
                                        initRecycler(view, configurableData)
                                        itemOldAmount.text =
                                            Utility.changedCurrencyPrice(configurableData[0].old_price)
                                        itemAmount.text =
                                            Utility.changedCurrencyPrice(configurableData[0].price)
                                        for (item in specificationData) {
                                            when (item.itemTitle) {

                                            }
                                        }
                                        additionalDataAdapter.notifyDataSetChanged()
                                        colorItems.clear()
                                    }
                                }
                                Status.ERROR -> {
                                    ViewUtils.hideProgressDialog()
                                    it.message?.let { it1 -> Log.e("error", it1) }
                                }
                                Status.LOADING -> {
                                }
                            }
                        }
                    })
        }
    }


    override fun onItemClick(position: Int, type: String) {
        if (type.equals("productDetail", true)) {
            val bundle = Bundle()
            bundle.putInt("productid", configurableData[position].id)
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
                configurableData[position].id.toString()
            )
        )
        viewModel.unTickWishlist(addAndUntickWishListRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(
                                requireContext(),
                                R.string.product_removed_from_wishlist,
                                Toast.LENGTH_SHORT
                            ).show()
                            mAdapter.notifyDataSetChanged()
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
                configurableData[position].id.toString()
            )
        )
        viewModel.addToWishList(addAndUntickWishListRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Toast.makeText(
                                requireContext(),
                                R.string.product_added_to_wishlist,
                                Toast.LENGTH_SHORT
                            ).show()
                            mAdapter.notifyDataSetChanged()
                            addToWishListItemArray(position)
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            if (it.message!!.contains("400")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Not able to add to WishList ${configurableData[position].id}",
                                    Toast.LENGTH_SHORT
                                )
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

    fun initRecycler(v: View?, catagoryList: List<ProductListData>) {
        product_list_recycler?.setHasFixedSize(true)


        // This code used to start  grid layout from right side of the screen as comfortable for arabic Views
        product_list_recycler?.layoutManager = lm

        // specify an adapter (see also next example)
        mAdapter = ProductListAdapter(configurableData, this, requireActivity())
        product_list_recycler!!.adapter = mAdapter
//        mAdapter.notifyDataSetChanged()
    }

    private fun initializeSortDialogue() {

        dialog = activity?.let { Dialog(it, R.style.CustomDialogAnimation) }

        dialog?.setContentView(R.layout.sort_design_english)

        if (dialog != null) {
            val window: Window? = dialog!!.window

            //  dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
            if (window != null) {

                // dialog.getWindow().setDimAmount(0.5f);
                val params = window.attributes
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                params.horizontalMargin = 10f
                dialog!!.setCanceledOnTouchOutside(true)
                dialog!!.setCancelable(true)
                params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                params.y = 380
                params.dimAmount = 0f
                params.dimAmount = 0.2f
                dialog!!.window?.setBackgroundDrawableResource(R.drawable.solid_white_round_background)
                // params.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                window.attributes = params
            }
        }
        recentlyArrived = dialog!!.findViewById(R.id.recentlyarrived)
        highToLow = dialog!!.findViewById(R.id.hightolow)
        lowToHigh = dialog!!.findViewById(R.id.lowtohigh)
        defaultList = dialog!!.findViewById(R.id.defaultlist)
        tickImage = dialog!!.findViewById(R.id.tick_image)
        tickImage2 = dialog!!.findViewById(R.id.tick_image2)
        tickImage3 = dialog!!.findViewById(R.id.tick_image3)
        tickImage4 = dialog!!.findViewById(R.id.tick_image4)

        if (sortingflag == 0) {
            tickImage?.makeVisible()
            tickImage2?.makeGone()
            tickImage3?.makeGone()
            tickImage4?.makeGone()
        } else if (sortingflag == 1) {
            tickImage?.makeGone()
            tickImage2?.makeVisible()
            tickImage3?.makeGone()
            tickImage4?.makeGone()
        }

        defaultList?.setOnClickListener {

            tickImage?.makeVisible()
            tickImage2?.makeGone()
            tickImage3?.makeGone()
            tickImage4?.makeGone()
            sortingflag = 0
            currentpage = 1

            getProductList()

        }


        recentlyArrived?.setOnClickListener {
            tickImage?.visibility = View.GONE
            tickImage2?.visibility = View.VISIBLE
            tickImage3?.visibility = View.GONE
            tickImage4?.visibility = View.GONE
            sortingflag = 1
            currentpage = 1

            getProductList()

        }

        highToLow?.setOnClickListener {

            tickImage?.visibility = View.GONE
            tickImage2?.visibility = View.GONE
            tickImage3?.visibility = View.GONE
            tickImage4?.visibility = View.VISIBLE

            sortingflag = 3
            currentpage = 1
            getProductList()
        }
        lowToHigh?.setOnClickListener {
            tickImage?.visibility = View.GONE
            tickImage2?.visibility = View.GONE
            tickImage3?.visibility = View.VISIBLE
            tickImage4?.visibility = View.GONE
            sortingflag = 2
            currentpage = 1
            getProductList()

        }

        dialog?.show()

    }

    private fun addToWishListItemArray(position: Int) {
        getWishListItemsCount(PreferenceHelper.readInt(Constants.TOTAL_WISHLIST_ITEMS)!! + 1)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.add(
                configurableData[position].id.toString()
            )
            else ->
                (activity as BuyerActivity).wishListItemsId.add(
                    configurableData[position].id.toString()
                )
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun removeFromWishListItemArray(position: Int) {
        getWishListItemsCount(PreferenceHelper.readInt(Constants.TOTAL_WISHLIST_ITEMS)!! - 1)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemsId.remove(
                configurableData[position].id.toString()
            )
            else ->
                (activity as BuyerActivity).wishListItemsId.remove(
                    configurableData[position].id.toString()
                )
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun getWishListItemsCount(count: Int) {
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).wishListItemCount(count)
            else -> (activity as BuyerActivity).wishListItemCount(count)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            OPEN_FILTER_FRAGMENT_REQUEST_CODE -> {
                when (resultCode) {
                    OPEN_FILTER_FRAGMENT_RESULT_CODE -> {
                        val args = data!!.getBundleExtra("BUNDLE")
                        storageList = args?.getStringArrayList("Storage") as ArrayList<String>;
                         ramList = args?.getStringArrayList("RamSize") as ArrayList<String>;
                       sizeList = args?.getStringArrayList("size") as ArrayList<String>;
                        colorList = args?.getStringArrayList("Colour") as ArrayList<String>;

                        storageList.forEach{
                            Log.e("storagevalue",it)
                        }
                        ramList.forEach{
                            Log.e(" ramList",it)
                        }

                        sizeList.forEach{
                            Log.e("sizeList",it)
                        }

                        colorList.forEach{
                            Log.e("colorList",it)
                        }

                        filterNavigationData.clear()
                        filterNavigationDataList.clear()
                        filterNavigationDataList2.clear()
                        filterNavigationDataList3.clear()
                        filterNavigationDataList4.clear()
                        filterData = data.getSerializableExtra("AllFilterData") as ArrayList<GetFilterDataResponseList>

                        var  storagedata    = GetFilterNavigationResponseData("storage",filterNavigationDataList,"storage")
                        for (i in 0..storageList.size-1){
                            for(j in 0..filterData.get(0).data.size-1) {
                                if (storageList.get(i).equals(filterData.get(0).data.get(j).value)){

                                    val data1 = GetFilterNavigationResponseDataList(filterData.get(0).data.get(j).count,
                                        filterData.get(0).data.get(j).display,
                                                filterData.get(0).data.get(j).value)

                                      filterNavigationDataList.add(data1)

                                    Log.e("fromstorage",filterData.get(0).data.get(j).display+" "+filterData.get(0).data.get(j).value+" "+filterData.get(0).data.get(j).count)

                                }

                            }

                          // storagedata    = GetFilterNavigationResponseData("storage",filterNavigationDataList,"storage")
                        }


                        storagedata    = GetFilterNavigationResponseData("storage",filterNavigationDataList,"storage")
                        filterNavigationData.add(storagedata)


                        var  ramdata    = GetFilterNavigationResponseData("storage",filterNavigationDataList,"storage")
                        for (i in 0..ramList.size-1){
                            for(j in 0..filterData.get(1).data.size-1) {
                                if (ramList.get(i).equals(filterData.get(1).data.get(j).value)){

                                    val data = GetFilterNavigationResponseDataList(filterData.get(1).data.get(j).count,
                                        filterData.get(1).data.get(j).display,
                                        filterData.get(1).data.get(j).value)

                                    filterNavigationDataList2.add(data)

                                    for(k in 0..filterNavigationDataList2.size-1) {
                                        Log.e(
                                            "fromRam",
                                            filterNavigationDataList2.get(k).display + " " + filterNavigationDataList2.get(
                                                k).value + " " + filterNavigationDataList2.get(k).count
                                        )
                                    }

                                }

                            }

                        }
                        ramdata = GetFilterNavigationResponseData("ram",filterNavigationDataList2,"Memory RAM")

                        filterNavigationData.add(ramdata)
                       var colordata = GetFilterNavigationResponseData("color",filterNavigationDataList3,"Color")
                        for (i in 0..colorList.size-1){
                            for(j in 0..filterData.get(2).data.size-1) {
                                if (colorList.get(i).equals(filterData.get(2).data.get(j).value)){

                                    val data3 = GetFilterNavigationResponseDataList(filterData.get(2).data.get(j).count,
                                        filterData.get(2).data.get(j).display,
                                        filterData.get(2).data.get(j).value)

                                    filterNavigationDataList3.add(data3)
                                    Log.e("fromcolor",filterData.get(2).data.get(j).display+" "+filterData.get(2).data.get(j).value+" "+filterData.get(2).data.get(j).count)

                                    for(k in 0..filterNavigationDataList3.size-1) {
                                        Log.e(
                                            "fromcolor",
                                            filterNavigationDataList3.get(k).display + " " + filterNavigationDataList3.get(
                                                k).value + " " + filterNavigationDataList3.get(k).count
                                        )
                                    }

                                }

                            }


                        }
                        colordata = GetFilterNavigationResponseData("color",filterNavigationDataList3,"Color")
                        filterNavigationData.add(colordata)
                        var sizedata = GetFilterNavigationResponseData("size",filterNavigationDataList4,"Size")
                        for (i in 0..sizeList.size-1){
                            for(j in 0..filterData.get(3).data.size-1) {
                                if (sizeList.get(i).equals(filterData.get(3).data.get(j).value)){

                                    val data4 = GetFilterNavigationResponseDataList(filterData.get(3).data.get(j).count,
                                        filterData.get(3).data.get(j).display,
                                        filterData.get(3).data.get(j).value)

                                    filterNavigationDataList4.add(data4)
                                    Log.e("fromsize",filterData.get(3).data.get(j).display+" "+filterData.get(3).data.get(j).value+" "+filterData.get(3).data.get(j).count)

                                /*    for(k in 0..filterNavigationDataList4.size-1) {
                                        Log.e(
                                            "fromsize",
                                            filterNavigationDataList3.get(k).display + " " + filterNavigationDataList3.get(
                                                k).value + " " + filterNavigationDataList3.get(k).count
                                        )
                                    }*/

                                }

                            }


                        }
                        sizedata = GetFilterNavigationResponseData("size",filterNavigationDataList4,"Size")
                        filterNavigationData.add(sizedata)
                        getFilterNavigationList()
                    }
                }
            }
        }
    }

    private fun getFilterNavigationList() {


        /*{
	"param": {
		"cat_id": 58,
		"data": [{
			"code": "storage",
			"data": [{
				"count": "63",
				"display": "4 GB",
				"value": "5835"
			}, {
				"count": "70",
				"display": "64 GB",
				"value": "5840"
			}],
			"name": "Storage"
		}, {
			"code": "ram",
			"data": [{
				"count": "173",
				"display": "4 GB",
				"value": "5842"
			}, {
				"count": "172",
				"display": "6 GB",
				"value": "5843"
			}],
			"name": "Memory RAM"
		}, {
			"code": "color",
			"data": [{
				 {
				"count": "56",
				"display": "Gray",
				"value": "5479"
			}, {
				"count": "37",
				"display": "Green",
				"value": "5480"
			}],
			"name": "Color"
		}, {
			"code": "size",
			"data": [{
				"count": "895",
				"display": "55 cm",
				"value": "5518"
			},  {
				"count": "10",
				"display": "6 inch",
				"value": "5833"
			}],
			"name": "Size"
		}],
		"order_by": "position",
		"page": 1,
		"product_count": 10,
		"sort_order": "asc",
		"type": "new"
	}
}*/
      //  filterNavigationData.clear()
        currentpage = 1
        val getFilterNavigationListRequest = GetFilterNavigationListRequest(
            param = GetFilterNavigationListRequestParam(
                productId, Utility.getDeviceType(), sortBy, sortOrder, currentpage, 10,
                filterNavigationData
            )
        )
        viewModel.getFilterNavigationList(getFilterNavigationListRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                setFilterCounter()
                                configurableData.clear()
                                for (item in it.data) {
                                    configurableData.add(ProductListData(
                                        item.name,
                                        item.id.toInt(),
                                        item.sku,
                                        item.price.toDouble(),
                                        item.old_price.toDouble(),
                                        item.img,
                                        item.created,
                                        "",
                                        true
                                    ))
                                }
                                mAdapter.notifyDataSetChanged()
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

    private fun setFilterCounter() {
        count = storageList.size + ramList.size + colorList.size + sizeList.size
        if (count > 0) {
            filtercounter.makeVisible()
            filtercounter.text = "($count)"
        } else {
            filtercounter.makeGone()
        }
    }

}