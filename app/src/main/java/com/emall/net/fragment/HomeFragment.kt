package com.emall.net.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.*
import com.emall.net.fragment.product.ProductDetailFragment
import com.emall.net.fragment.product.ProductListFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.BottomItemList
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.dashboard.*
import com.emall.net.network.model.dashboardUsed.DashboardResponseDataForUsed
import com.emall.net.utils.*
import com.emall.net.utils.Utility.hasInternet
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : BaseFragment(), View.OnClickListener, OnItemClick {

/*
    private var countDownTimer : CountDownTimer ? = null
*/


    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPagerAdapter2: ViewPagerAdapterNew
    private lateinit var topOffersAdapter: TopOffersAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var topSelectedProductsAdapter: TopSelectedProductsAdapter
    private lateinit var popularSearchAdapter: PopularSearchAdapter
    private lateinit var popularSearchVerticalAdapter: PopularSearchVerticalAdapter
    private lateinit var flashSaleAdapter: FlashSaleAdapter
    private lateinit var hotSaleAdapter: HotSaleAdapter
    private lateinit var bottomItemAdapter: BottomItemAdapter
    private lateinit var viewModel: MainViewModel

    private var dashboardResponseDataList = DashboardResponseData()
    private var dashboardResponseDataListForUsed = DashboardResponseDataForUsed()

    private var topOffersList = ArrayList<DataTopOffer>()
    private var bestDealsList = ArrayList<DataBestDeal>()
    private var topSelectedProductsList = ArrayList<DataTopTenSelected>()
    private var popularSearchList = ArrayList<DataPopularSearch>()

    private var flashSaleList = ArrayList<ProductX>()
    private var hotSaleList = ArrayList<DataHotSale>()
    private var bottomItemList = ArrayList<BottomItemList>()
    private val imageList = ArrayList<String>()
    private val brandList = ArrayList<String>()

    private var popularSearchVerticalList = ArrayList<DataMidBannerFour>()
    private var popularSearchVerticalListForUsed =
        ArrayList<com.emall.net.network.model.dashboardUsed.DataMidBannerFour>()
    private val TAG = "Home fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).showHideToolbar("home")
            else -> (activity as BuyerActivity).showHideToolbar("home")
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().storeUrlApiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        new_click.setOnClickListener(this)
        used_click.setOnClickListener(this)
        top_offers_view_all.setOnClickListener(this)
        best_deals_view_all.setOnClickListener(this)
        hot_sales_view_all.setOnClickListener(this)
        popular_search_view_all.setOnClickListener(this)
        after_best_deals_banner.setOnClickListener(this)
        after_selected_products_banner.setOnClickListener(this)
        after_selected_products_banner_2.setOnClickListener(this)
        deals_of_the_month_banner.setOnClickListener(this)
        shop_now_btn.setOnClickListener(this)
        after_deal_of_the_month_banner.setOnClickListener(this)
        after_popular_search_recycler_view_vertically_banner.setOnClickListener(this)
        after_flash_sale_banner_1.setOnClickListener(this)
        after_flash_sale_banner_2.setOnClickListener(this)
        after_hot_sales_banner.setOnClickListener(this)
        after_hot_sales_banner_2.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).updatePreferences()
            else -> (activity as BuyerActivity).updatePreferences()
        }
        setupItemList()
    }

    private fun setupItemList() {

        // top sales recycler view
        top_offers_recycler_view.setHasFixedSize(true)
        top_offers_recycler_view.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        topOffersAdapter = TopOffersAdapter(topOffersList, this)
        top_offers_recycler_view.adapter = topOffersAdapter

        // best deals recycler view
        best_deals_recycler_view.setHasFixedSize(true)
        best_deals_recycler_view.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bestDealsAdapter = BestDealsAdapter(bestDealsList, this)
        best_deals_recycler_view.adapter = bestDealsAdapter

        // top selected products recycler view
        top_ten_selected_products_recycler_view.setHasFixedSize(true)
        top_ten_selected_products_recycler_view.layoutManager = GridLayoutManager(activity, 2)
        topSelectedProductsAdapter = TopSelectedProductsAdapter(topSelectedProductsList, this)
        top_ten_selected_products_recycler_view.adapter = topSelectedProductsAdapter

        // popular search recycler view
        popular_search_recycler_view.setHasFixedSize(true)
        popular_search_recycler_view.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularSearchAdapter = PopularSearchAdapter(popularSearchList, this)
        popular_search_recycler_view.adapter = popularSearchAdapter

        // popular search vertically recycler view
        popular_search_recycler_view_vertically.setHasFixedSize(true)
        popular_search_recycler_view_vertically.layoutManager = LinearLayoutManager(activity)
        popularSearchVerticalAdapter = PopularSearchVerticalAdapter(popularSearchVerticalList, this)
        popular_search_recycler_view_vertically.adapter = popularSearchVerticalAdapter

        // flash sale recycler view
        flash_sale_recycler_view.setHasFixedSize(true)
        flash_sale_recycler_view.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        flashSaleAdapter = FlashSaleAdapter(flashSaleList, this)
        flash_sale_recycler_view.adapter = flashSaleAdapter

        // hot sale recycler view
        hot_sales_recycler_view.setHasFixedSize(true)
        hot_sales_recycler_view.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        hotSaleAdapter = HotSaleAdapter(hotSaleList, this)
        hot_sales_recycler_view.adapter = hotSaleAdapter

        // bottom items recycler view
        most_bottom_recycler_view.setHasFixedSize(true)
        most_bottom_recycler_view.layoutManager = GridLayoutManager(activity, 3)
        bottomItemAdapter = BottomItemAdapter(bottomItemList, this)
        most_bottom_recycler_view.adapter = bottomItemAdapter

        callNewApi()
    }

    override fun onClick(view: View?) {
        if (hasInternet(requireContext())) {
            when (view?.id) {
                R.id.new_click -> {
                    imageList.clear()
                    viewPagerAdapter.notifyDataSetChanged()
                    brandList.clear()
                    viewPagerAdapter2.notifyDataSetChanged()
                    slider_dots.removeAllViews()
                    hot_sales_slider_dots.removeAllViews()
                    PreferenceHelper.writeBoolean(Constants.IS_NEW_DEVICE, true)
                    changeUI()
                    callNewApi()
                }
                R.id.used_click -> {
                    imageList.clear()
                    viewPagerAdapter.notifyDataSetChanged()
                    brandList.clear()
                    viewPagerAdapter2.notifyDataSetChanged()
                    slider_dots.removeAllViews()
                    hot_sales_slider_dots.removeAllViews()
                    PreferenceHelper.writeBoolean(Constants.IS_NEW_DEVICE, false)
                    changeOtherUI()
                    callUsedApi()
                }
                R.id.top_offers_view_all -> when {
                    Utility.getDeviceType().equals("new", true) -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataList[0].view_all.data_top_offers
                    )
                    else -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataListForUsed[0].view_all.data_top_offers
                    )
                }
                R.id.best_deals_view_all -> when {
                    Utility.getDeviceType().equals("new", true) -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataList[0].view_all.data_best_deals
                    )
                    else -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataListForUsed[0].view_all.data_best_deals
                    )

                }
                R.id.hot_sales_view_all -> when {
                    Utility.getDeviceType().equals("new", true) -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataList[0].view_all.data_hot_sale
                    )
                    else -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataListForUsed[0].view_all.data_hot_sale
                    )
                }
                R.id.popular_search_view_all -> when {
                    Utility.getDeviceType().equals("new", true) -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataList[0].view_all.data_popular_search
                    )
                    else -> openProductListViewAll(
                        ProductListFragment(),
                        dashboardResponseDataListForUsed[0].view_all.data_popular_search
                    )
                }
                R.id.after_best_deals_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_one[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_one[0].id.toInt())
                    }
                }
                R.id.after_selected_products_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_two[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_two[0].id.toInt())
                    }
                }
                R.id.after_selected_products_banner_2 -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_two[1].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_two[1].id.toInt())
                    }
                }
                R.id.deals_of_the_month_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) ->
                            openProductDetailPage(dashboardResponseDataList[0].data_best_deal_month[0].products[0].id.toInt())
                        else -> openProductDetailPage(dashboardResponseDataListForUsed[0].data_best_deal_month[0].products[0].id.toInt())
                    }
                }
                R.id.shop_now_btn -> {
                    when {
                        Utility.getDeviceType().equals("new", true) ->
                            openProductDetailPage(dashboardResponseDataList[0].data_best_deal_month[0].products[0].id.toInt())
                        else -> openProductDetailPage(dashboardResponseDataListForUsed[0].data_best_deal_month[0].products[0].id.toInt())
                    }
                }
                R.id.after_deal_of_the_month_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_three[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_three[0].id.toInt())
                    }
                }
                R.id.after_popular_search_recycler_view_vertically_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_five[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_five[0].id.toInt())
                    }
                }
                R.id.after_flash_sale_banner_1 -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_six[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_six[0].id.toInt())
                    }
                }
                R.id.after_flash_sale_banner_2 -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_six[1].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_six[1].id.toInt())
                    }
                }
                R.id.after_hot_sales_banner -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_seven[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_seven[0].id.toInt())
                    }
                }
                R.id.after_hot_sales_banner_2 -> {
                    when {
                        Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                            dashboardResponseDataList[0].data_mid_banner_eight[0].id.toInt()
                        )
                        else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_eight[0].id.toInt())
                    }
                }
            }
        } else {
            scrollView.snack(getString(R.string.no_internet))
        }
    }

    private fun openProductListViewAll(fragment: Fragment, productType: String) {
        val bundle = Bundle()
        bundle.putString("productType", productType)
        fragment.arguments = bundle
        PreferenceHelper.writeBoolean(Constants.IS_FROM_HOME_FRAGMENT, true)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).replaceFragment(
                fragment,
                R.id.container
            )
            else -> (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
        }
    }

    private fun callNewApi() {
        showProgressDialog()
        /*when(activity){
        is SellerActivity -> ViewUtils.showProgressDialog(requireActivity())
        else -> ViewUtils.showProgressDialog(requireActivity())
        }*/
        imageList.clear()
        topOffersList.clear()
        bestDealsList.clear()
        topSelectedProductsList.clear()
        popularSearchList.clear()
        popularSearchVerticalList.clear()
        popularSearchVerticalListForUsed.clear()
        flashSaleList.clear()
        hotSaleList.clear()
        brandList.clear()
        bottomItemList.clear()

        if (hasInternet(requireContext())) {
            viewModel.getDashboardDetails(Utility.getStoreCode(), Utility.getDeviceType()).observe(
                viewLifecycleOwner,
                {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { it ->
                                    dashboardResponseDataList = it
                                    setData(it)
                                    hideProgressDialog()
                                }
                            }
                            Status.LOADING -> {

                                Log.d(TAG, "onResume: ")
                            }
                            Status.ERROR -> {
                                Log.d(TAG, "on error: " + it.message)
                                hideProgressDialog()
                            }
                        }
                    }
                })
        } else {
            scrollView.snack(getString(R.string.no_internet))
        }
    }

    private fun setData(data: DashboardResponseData) {
        for (dataItem in data) {

            for (item in dataItem.main_banner) {
                imageList.add(item.image)
            }
            topOffersList.addAll(dataItem.data_top_offers)
            bestDealsList.addAll(dataItem.data_best_deals)
            Picasso.get().load(dataItem.data_mid_banner_one[0].image)
                .into(after_best_deals_banner)
            topSelectedProductsList.addAll(dataItem.data_top_ten_selected)
            Picasso.get().load(dataItem.data_mid_banner_two[0].image)
                .into(after_selected_products_banner)
            Picasso.get().load(dataItem.data_mid_banner_two[1].image)
                .into(after_selected_products_banner_2)
            Picasso.get()
                .load(dataItem.data_best_deal_month[0].products[0].image)
                .into(deal_of_the_month_image)
            deal_of_the_month_item_price.text =
                dataItem.data_best_deal_month[0].products[0].price.toString()
            deal_of_the_month_text_view.text =
                dataItem.data_best_deal_month[0].products[0].name
            Picasso.get().load(dataItem.data_mid_banner_three[0].image)
                .into(after_deal_of_the_month_banner)
            popularSearchList.addAll(dataItem.data_popular_search)
            popularSearchVerticalList.addAll(dataItem.data_mid_banner_four)
            Picasso.get().load(dataItem.data_mid_banner_five[0].image)
                .into(after_popular_search_recycler_view_vertically_banner)
            flash_sale_heading.text = dataItem.data_flash_sale[0].text_one
            flash_sale_sub_heading.text =
                dataItem.data_flash_sale[0].text_two
            flashSaleList.addAll(dataItem.data_flash_sale[0].products)
            Picasso.get().load(dataItem.data_mid_banner_six[0].image)
                .into(after_flash_sale_banner_1)
            Picasso.get().load(dataItem.data_mid_banner_six[1].image)
                .into(after_flash_sale_banner_2)
            hotSaleList.addAll(dataItem.data_hot_sale)
            Picasso.get().load(dataItem.data_mid_banner_seven[0].image)
                .into(after_hot_sales_banner)
            for (data in dataItem.data_brand_logo_slider.items) {
                brandList.add("https://emall.net/pub/media/" + data.image)
            }
            Picasso.get().load(dataItem.data_mid_banner_eight[0].image)
                .into(after_hot_sales_banner_2)
        }

        setImageToViewPager(imageList)
        setSliderBelowHotSalesBanner(brandList)

        topOffersAdapter.notifyDataSetChanged()
        bestDealsAdapter.notifyDataSetChanged()
        topSelectedProductsAdapter.notifyDataSetChanged()
        popularSearchAdapter.notifyDataSetChanged()
        popularSearchVerticalAdapter.notifyDataSetChanged()
        flashSaleAdapter.notifyDataSetChanged()
        hotSaleAdapter.notifyDataSetChanged()

        addSimilarProducts()
    }

    private fun callUsedApi() {
        /* when(activity){
             is SellerActivity -> ViewUtils.showProgressDialog(requireActivity())
             else -> ViewUtils.showProgressDialog(requireActivity())
         }*/
        showProgressDialog()
        imageList.clear()
        topOffersList.clear()
        bestDealsList.clear()
        topSelectedProductsList.clear()
        popularSearchVerticalList.clear()
        popularSearchVerticalListForUsed.clear()
        flashSaleList.clear()
        hotSaleList.clear()
        brandList.clear()
        bottomItemList.clear()

        if (hasInternet(requireContext())) {
            viewModel.getDashBoardDetailsForUsed(Utility.getStoreCode(), "used").observe(
                viewLifecycleOwner,
                {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { it ->
                                    dashboardResponseDataListForUsed = it
                                    setDataForUsed(it)
                                    hideProgressDialog()
                                }
                            }
                            Status.LOADING -> {
                                Log.d(TAG, "onResume: ")
                            }
                            Status.ERROR -> {
                                Log.d(TAG, "on error: " + it.message)
                                hideProgressDialog()
                            }
                        }
                    }
                })
        } else {
            scrollView.snack(getString(R.string.no_internet))
        }
    }

    private fun setDataForUsed(data: DashboardResponseDataForUsed) {
        for (dataItem in data) {

            for (item in dataItem.main_banner) {
                imageList.add(item.image)
            }
            topOffersList.addAll(dataItem.data_top_offers)
            bestDealsList.addAll(dataItem.data_best_deals)
            Picasso.get().load(dataItem.data_mid_banner_one[0].image)
                .into(after_best_deals_banner)
            topSelectedProductsList.addAll(dataItem.data_top_ten_selected)
            Picasso.get().load(dataItem.data_mid_banner_two[0].image)
                .into(after_selected_products_banner)
            Picasso.get().load(dataItem.data_mid_banner_two[1].image)
                .into(after_selected_products_banner_2)
            Picasso.get()
                .load(dataItem.data_best_deal_month[0].products[0].image)
                .into(deal_of_the_month_image)
            deal_of_the_month_item_price.text =
                dataItem.data_best_deal_month[0].products[0].price.toString()
            deal_of_the_month_text_view.text =
                dataItem.data_best_deal_month[0].products[0].name
            Picasso.get().load(dataItem.data_mid_banner_three[0].image)
                .into(after_deal_of_the_month_banner)
            popularSearchList.addAll(dataItem.data_popular_search)
            popularSearchVerticalListForUsed.addAll(dataItem.data_mid_banner_four)
            Picasso.get().load(dataItem.data_mid_banner_five[0].image)
                .into(after_popular_search_recycler_view_vertically_banner)
            flash_sale_heading.text = dataItem.data_flash_sale[0].text_one
            flash_sale_sub_heading.text =
                dataItem.data_flash_sale[0].text_two
            if (dataItem.data_flash_sale[0].products != null) {
                flashSaleList.addAll(dataItem.data_flash_sale[0].products)
            }
            Picasso.get().load(dataItem.data_mid_banner_six[0].image)
                .into(after_flash_sale_banner_1)
            Picasso.get().load(dataItem.data_mid_banner_six[1].image)
                .into(after_flash_sale_banner_2)
            hotSaleList.addAll(dataItem.data_hot_sale)
            Picasso.get().load(dataItem.data_mid_banner_seven[0].image)
                .into(after_hot_sales_banner)
            for (item in dataItem.data_brand_logo_slider.items) {
                brandList.add("https://emall.net/pub/media/" + item.image)
            }
            Picasso.get().load(dataItem.data_mid_banner_eight[0].image)
                .into(after_hot_sales_banner_2)
        }

        setImageToViewPager(imageList)
        setSliderBelowHotSalesBanner(brandList)

        topOffersAdapter.notifyDataSetChanged()
        bestDealsAdapter.notifyDataSetChanged()
        topSelectedProductsAdapter.notifyDataSetChanged()
        popularSearchAdapter.notifyDataSetChanged()
        popularSearchVerticalAdapter.notifyDataSetChanged()
        flashSaleAdapter.notifyDataSetChanged()
        hotSaleAdapter.notifyDataSetChanged()

        addSimilarProducts()
    }

    private fun setImageToViewPager(imageList: ArrayList<String>) {

        viewPagerAdapter = ViewPagerAdapter(imageList, this)
        viewPager.adapter = viewPagerAdapter
        viewPagerAdapter.notifyDataSetChanged()
        val dotsCount = viewPagerAdapter.count

        if (dotsCount > 0) {
            val dots = arrayOfNulls<ImageView>(dotsCount)

            for (i in 0 until dotsCount) {
                Log.d(TAG, "setDots value: $i")
                dots[i] = ImageView(requireContext())
                dots[i]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.unselected_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                slider_dots.addView(dots[i], params)
            }

            dots[0]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.default_selected_dot
                )
            )

            viewPager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0 until dotsCount) {
                        dots[i]!!.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.unselected_dot)
                        )
                    }
                    dots[position]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.default_selected_dot
                        )
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun setSliderBelowHotSalesBanner(brandList: ArrayList<String>) {

        viewPagerAdapter2 = ViewPagerAdapterNew(brandList, this)
        hot_sales_view_pager.adapter = viewPagerAdapter2
        viewPagerAdapter2.notifyDataSetChanged()
        val dotsCount = viewPagerAdapter2.count

        if (dotsCount > 0) {
            val dots = arrayOfNulls<ImageView>(dotsCount)

            for (i in 0 until dotsCount) {
                Log.d(TAG, "setDots value: $i")
                dots[i] = ImageView(requireContext())
                dots[i]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.unselected_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                hot_sales_slider_dots.addView(dots[i], params)
            }

            dots[0]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.default_selected_dot
                )
            )

            hot_sales_view_pager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0 until dotsCount) {
                        dots[i]!!.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.unselected_dot)
                        )
                    }
                    dots[position]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.default_selected_dot
                        )
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }


    }

    private fun addSimilarProducts() {
        var item = BottomItemList(
            R.drawable.ic_free_delivery,
            R.drawable.free_delivery,
            getString(R.string.free_delivery),
            getString(R.string.fromdollor40)
        )
        bottomItemList.add(item)
        item = BottomItemList(
            R.drawable.ic_best_quality,
            R.drawable.best_quality,
            getString(R.string.bestquality),
            getString(R.string.Brand)
        )
        bottomItemList.add(item)
        item = BottomItemList(
            R.drawable.ic_year_return,
            R.drawable.free_return,
            getString(R.string.oneyear),
            getString(R.string.freeforreturn)
        )
        bottomItemList.add(item)
        item =
            BottomItemList(
                R.drawable.ic_feedback,
                R.drawable.feedback,
                getString(R.string.feedback),
                getString(R.string.realdata)
            )
        bottomItemList.add(item)
        item = BottomItemList(
            R.drawable.ic_payment,
            R.drawable.payment,
            getString(R.string.payment),
            getString(R.string.secure)
        )
        bottomItemList.add(item)
        item = BottomItemList(
            R.drawable.ic_latest_products,
            R.drawable.latest_products,
            getString(R.string.latestproduct),
            getString(R.string.newfeatures)
        )
        bottomItemList.add(item)
        bottomItemAdapter.notifyDataSetChanged()
    }

    private fun changeOtherUI() {
        used_click.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.selected_color
            )
        )
        new_click.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.used_btn_default_color
            )
        )

        used_click.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.white
            )
        )
        new_click.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.light_gray
            )
        )
    }

    private fun changeUI() {
        new_click.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.selected_color
            )
        )
        used_click.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.used_btn_default_color
            )
        )
        new_click.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.white
            )
        )
        used_click.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.light_gray
            )
        )
    }

    override fun onItemClick(position: Int, type: String) {

        if (type.equals("topBanner", true)) {
            when {
                Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                    dashboardResponseDataList[0].main_banner[position].id.toInt()
                )
                else -> openProductListFragment(dashboardResponseDataListForUsed[0].main_banner[position].id.toInt())
            }
        } else if (type.equals("top offers", true)) {
            openProductDetailPage(topOffersList[position].id.toInt())
        } else if (type.equals("best deals", true)) {
            openProductDetailPage(bestDealsList[position].id.toInt())
        } else if (type.equals("top selected", true)) {
            when {
                Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                    dashboardResponseDataList[0].data_top_ten_selected[position].id.toInt()
                )
                else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_top_ten_selected[position].id.toInt())
            }
        } else if (type.equals("popular search", true)) {
            openProductDetailPage(popularSearchList[position].id.toInt())
        } else if (type.equals("top sales", true)) {
            when {
                Utility.getDeviceType().equals("new", true) -> openProductListFragment(
                    dashboardResponseDataList[0].data_mid_banner_four[position].id.toInt()
                )
                else -> openProductListFragment(dashboardResponseDataListForUsed[0].data_mid_banner_four[position].id.toInt())
            }
        } else if (type.equals("flash sale", true)) {
            openProductDetailPage(flashSaleList[position].id.toInt())
        } else if (type.equals("hot sale", true)) {
            openProductDetailPage(hotSaleList[position].id.toInt())
        }
    }

    private fun openProductDetailPage(productId: Int) {
        val bundle = Bundle()
        bundle.putInt("productid", productId)
        bundle.putString("from", "")
        bundle.putString("itemId", "")
        val transaction = this.parentFragmentManager.beginTransaction()
        val frag2 = ProductDetailFragment()
        frag2.arguments = bundle
        transaction.add(R.id.container, frag2)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    private fun openProductListFragment(productId: Int) {
        PreferenceHelper.writeBoolean(Constants.IS_FROM_HOME_FRAGMENT, false)
        val bundle = Bundle()
        bundle.putInt("productid", productId)
        val transaction = this.parentFragmentManager.beginTransaction()
        val frag2 = ProductListFragment()
        frag2.arguments = bundle
        transaction.add(R.id.container, frag2)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    /*Countdown Timer*/

    /*private fun startTimer() {

        val currentTime = Calendar.getInstance().time
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 2)
        val endDate = cal.time

        //milliseconds
        var different = endDate.time - currentTime.time
        countDownTimer = object : CountDownTimer(different, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                days_text_view.setText(elapsedDays.toString())
                hours_text_view.setText(elapsedHours.toString())
                minutes_text_view.setText(elapsedMinutes.toString())
                seconds_text_view.setText(elapsedSeconds.toString())

            }

            override fun onFinish() {
                days_text_view.setText("00")
                hours_text_view.setText("00")
                minutes_text_view.setText("00")
                seconds_text_view.setText("00")
            }
        }.start()
    }*/

    /*private fun stopTimer() {
        countDownTimer?.cancel()
    }*/
}