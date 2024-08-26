package com.emall.net.activity.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.*
import androidx.appcompat.app.*
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.*
import com.emall.net.fragment.*
import com.emall.net.fragment.auctionDevices.AuctionDevicesFragment
import com.emall.net.fragment.auctionDevicesWon.AuctionDevicesWonFragment
import com.emall.net.fragment.cart.CartFragment
import com.emall.net.fragment.category.CategoryFragment
import com.emall.net.fragment.chat.ChatMessagesFragment
import com.emall.net.fragment.evaluationDevices.EvaluationDevicesFragment
import com.emall.net.fragment.evaluationDevicesWon.EvaluationDevicesWonFragment
import com.emall.net.fragment.evaluationReports.EvaluationReportsFragment
import com.emall.net.fragment.faq.FAQFragment
import com.emall.net.fragment.payment.PaymentListFragment
import com.emall.net.fragment.reCommerceOrders.EvaluationOrderFragment
import com.emall.net.fragment.resolutionCenter.IssueListFragment
import com.emall.net.fragment.search.SearchFragment
import com.emall.net.fragment.settings.SettingsFragment
import com.emall.net.fragment.wishList.WishListFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.DrawerItemList
import com.emall.net.network.api.*
import com.emall.net.network.model.wishlistResponse.Data
import com.emall.net.utils.*
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.Constants.CURRENCY_RATE
import com.emall.net.utils.Constants.IS_NEW_DEVICE
import com.emall.net.utils.Constants.SELECTED_COUNTRY
import com.emall.net.utils.Constants.SELECTED_CURRENCY
import com.emall.net.utils.Constants.TOTAL_CART_ITEMS
import com.emall.net.utils.Utility.isCustomerLoggedIn
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_buyer.*
import kotlinx.android.synthetic.main.activity_buyer.back_btn
import kotlinx.android.synthetic.main.activity_buyer.bottomNavigationView
import kotlinx.android.synthetic.main.activity_buyer.categories_heading
import kotlinx.android.synthetic.main.activity_buyer.content
import kotlinx.android.synthetic.main.activity_buyer.drawerLayout
import kotlinx.android.synthetic.main.activity_buyer.drawer_recycler_view
import kotlinx.android.synthetic.main.activity_buyer.fab
import kotlinx.android.synthetic.main.activity_buyer.hamburger_btn
import kotlinx.android.synthetic.main.activity_buyer.search_container
import kotlinx.android.synthetic.main.activity_buyer.wishlist_container
import kotlinx.android.synthetic.main.activity_buyer.wishlist_counter
import kotlinx.android.synthetic.main.activity_seller.*
import kotlinx.android.synthetic.main.cart_notification_badge.view.*
import kotlinx.android.synthetic.main.nav_header_buyer.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class BuyerActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    OnItemClick, View.OnClickListener {

    private lateinit var drawerAdapter: DrawerAdapter
    private val drawerList = ArrayList<DrawerItemList>()
    private lateinit var eCommerceViewModel: MainViewModel
    private var totalCartItems: Int = 0
    private var wishListItems = ArrayList<Data>()
    var wishListItemsId = ArrayList<String>()
    var badge: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)
        PreferenceHelper.writeBoolean(Constants.FIRST_TIME_USER, true)

        val bottomNavigationMenuView: BottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val v: View = bottomNavigationMenuView.getChildAt(1)
        val itemView: BottomNavigationItemView = v as BottomNavigationItemView

        badge = LayoutInflater.from(this)
            .inflate(R.layout.cart_notification_badge, itemView, true)

        if (PreferenceHelper.readString(Constants.APP_LOCALE).equals(Constants.ENGLISH_LOCALE)) {
            english_text_view.text = getString(R.string.arab_language)
        } else
            english_text_view.text = getString(R.string.english)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> window.insetsController?.hide(
                WindowInsets.Type.statusBars()
            )
            else -> window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    }

    override fun onStart() {
        super.onStart()
        updatePreferences()
        setUpViewModel()
        back_btn.setOnClickListener(this)
        hamburger_btn.setOnClickListener(this)
        fab.setOnClickListener(this)
        bottomNavigationView.setOnItemSelectedListener(this)
        wishlist_container.setOnClickListener {
            when {
                isCustomerLoggedIn() -> {
                    replaceFragment(WishListFragment(), R.id.container)
                }
                else -> {
                    openLoginDialog()
                }
            }
        }

        search_container.setOnClickListener {
            replaceFragment(SearchFragment(), R.id.container)
        }
        english_text_view.setOnClickListener(this)
        if (PreferenceHelper.readBoolean(Constants.FROM_CART) == true) {
            replaceFragment(CartFragment(), R.id.container)
            PreferenceHelper.writeBoolean(Constants.FROM_CART, false)
            bottomNavigationView.menu.getItem(1).isChecked = true
        } else {
            replaceFragment(HomeFragment(), R.id.container)
            bottomNavigationView.menu.getItem(0).isChecked = true
        }

        setupDrawerList()
    }

    fun updatePreferences() {
        if (isCustomerLoggedIn()) {
            if (PreferenceHelper.readString(Constants.USER_NAME)!!.isNotEmpty()) buyer_name.text =
                PreferenceHelper.readString(
                    Constants.USER_NAME
                )
            if (PreferenceHelper.readString(Constants.SELECTED_COUNTRY_NAME)!!
                    .isNotEmpty()
            ) country_name_buyer_login.text = PreferenceHelper.readString(
                Constants.SELECTED_COUNTRY_NAME
            )!!
            if (PreferenceHelper.readString(Constants.SELECTED_COUNTRY_FLAG)!!
                    .isNotEmpty()
            ) Picasso.get().load(
                PreferenceHelper.readString(
                    Constants.SELECTED_COUNTRY_FLAG
                )
            ).into(country_flag_buyer_login)
            login_layout.makeVisible()
            logout_layout.makeGone()
        } else {
            if (PreferenceHelper.readString(Constants.SELECTED_COUNTRY_NAME)!!
                    .isNotEmpty()
            ) country_name_buyer.text = PreferenceHelper.readString(
                Constants.SELECTED_COUNTRY_NAME
            )!!
            if (PreferenceHelper.readString(Constants.SELECTED_COUNTRY_FLAG)!!
                    .isNotEmpty()
            ) Picasso.get().load(
                PreferenceHelper.readString(
                    Constants.SELECTED_COUNTRY_FLAG
                )
            ).into(country_flag_buyer)
            login_layout.makeGone()
            logout_layout.makeVisible()
        }
    }

    override fun onResume() {
        super.onResume()
        drawerSlide()
    }

    private fun openLoginDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@BuyerActivity)
        alertDialog.setMessage(R.string.must_be_logged_in)
        alertDialog.setNegativeButton(
            resources.getString(R.string.no)
        ) { _, _ -> }
        alertDialog.setPositiveButton(
            resources.getString(R.string.Yes)
        ) { _, _ ->
            PreferenceHelper.writeBoolean(Constants.FROM_CART, false)
            openActivity(LoginActivity::class.java)
            finish()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


    private fun setUpViewModel() {
        viewModelStore.clear()
        scopeMainThread.launch {
            eCommerceViewModel = getECommerceViewModel()

            getCartItemCount()
            when {
                isCustomerLoggedIn() -> getWishlistItemsCount()
            }
        }

    }

    private fun getWishlistItemsCount() {
        eCommerceViewModel.getWishlistItems(
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage()
        )
            .observe(this, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { it ->
                                wishListItems.clear()
                                wishListItemsId.clear()
                                wishListItems = it.data
                                wishListItemCount(wishListItems.size)
                                for (items in wishListItems) {
                                    wishListItemsId.add(items.product_id)
                                }
                            }
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun setupDrawerList() {
        drawerAdapter = DrawerAdapter(drawerList, this)
        drawer_recycler_view.setHasFixedSize(true)
        drawer_recycler_view.layoutManager = LinearLayoutManager(this)
        drawer_recycler_view.adapter = drawerAdapter
        drawerList.clear()
        var drawerItem =
            DrawerItemList(getString(R.string.track_devices), R.drawable.track_devices_seller)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(getString(R.string.evaluation), R.drawable.offers)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(getString(R.string.auction), R.drawable.sell_my_devices)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList("Report", R.drawable.sell_my_devices)
        drawerList.add(drawerItem)

        drawerItem =
            DrawerItemList(getString(R.string.resolution_center), R.drawable.resolution_center)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(getString(R.string.my_messages), R.drawable.messages)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(getString(R.string.my_buying_orders), R.drawable.buying_orders)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList("My Payments", R.drawable.auction_payments)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList("Help & FAQ", R.drawable.help_support)
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(getString(R.string.settings), R.drawable.settings)
        drawerList.add(drawerItem)

        when {
            isCustomerLoggedIn() -> {
                drawerItem = DrawerItemList(getString(R.string.logout), R.drawable.logout)
                drawerList.add(drawerItem)
            }
        }
        drawerAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount != 0 -> {
                supportFragmentManager.popBackStack()
            }
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.END)
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onItemClick(position: Int, type: String) {
        when (position) {
            // track devices for estimation{
            0 -> {
            }
            1 -> replaceFragment(EvaluationDevicesWonFragment(), R.id.container)
            2 -> replaceFragment(AuctionDevicesWonFragment(), R.id.container)
            // reports
            3 -> replaceFragment(EvaluationReportsFragment(), R.id.container)
            4 -> replaceFragment(IssueListFragment(), R.id.container)
            // my messages
            5 -> replaceFragment(ChatMessagesFragment(), R.id.container)
            // my buying orders
            6 -> replaceFragment(EvaluationOrderFragment(), R.id.container)
            // my payments
            7 -> replaceFragment(PaymentListFragment(), R.id.container)
            // help & FAQ
            8 -> replaceFragment(FAQFragment(), R.id.container)
            9 -> replaceFragment(SettingsFragment(), R.id.container)
            10 -> openSellerActivity()
        }
        drawerLayout.closeDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_bar_evaluator, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.auction -> {
                replaceFragment(AuctionDevicesFragment(), R.id.container)
                bottomNavigationView.menu.getItem(4).isChecked = true
                PreferenceHelper.writeBoolean(Constants.IS_SELLER, true)
            }
            R.id.cart -> {
                replaceFragment(CartFragment(), R.id.container)
                bottomNavigationView.menu.getItem(1).isChecked = true
            }
            R.id.evaluation -> {
                replaceFragment(EvaluationDevicesFragment(), R.id.container)
                bottomNavigationView.menu.getItem(2).isChecked = true
            }
            R.id.categories -> {
                replaceFragment(CategoryFragment(), R.id.container)
                bottomNavigationView.menu.getItem(3).isChecked = true
            }
            R.id.home -> {
                replaceFragment(HomeFragment(), R.id.container)
                bottomNavigationView.menu.getItem(0).isChecked = true
            }
        }
        return true
    }

    fun moveToBuyerScreen(){
        for(i in 0 until supportFragmentManager.backStackEntryCount){
            supportFragmentManager.popBackStack()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                replaceFragment(EvaluationDevicesFragment(), R.id.container)
                bottomNavigationView.menu.getItem(2).isChecked = true
            }
            R.id.back_btn -> {
                if (bottomNavigationView.menu.getItem(0).isChecked) {
                    drawerLayout.openDrawer(GravityCompat.START)
                } else
                    onBackPressed()
            }
            R.id.login_register_btn -> {
                openActivity(LoginActivity::class.java)
                finish()
            }
            R.id.hamburger_btn -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.english_text_view -> {
                Log.d("Seller Activity", Locale.getDefault().language)
                when {
                    english_text_view.text.toString().equals("english", true) -> {
                        PreferenceHelper.writeString(Constants.APP_LOCALE, Constants.ENGLISH_LOCALE)
                        Log.d("Buyer Activity", Locale.getDefault().language)
                        english_text_view.text = getString(R.string.arab_language)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    else -> {
                        PreferenceHelper.writeString(Constants.APP_LOCALE, Constants.ARAB_LOCALE)
                        Log.d("Buyer Activity", Locale.getDefault().language)
                        english_text_view.text = getString(R.string.english)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
            }

        }
    }

    private fun drawerSlide() {
        val actionBarDrawerToggle: ActionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                val scaleFactor = 6f
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)

                    if (Locale.getDefault().language.equals(Locale.ENGLISH.language, false)) {
                        // LTR
                        val slideX = drawerView.width * slideOffset
                        content.translationX = slideX
                        content.scaleX = 1 - slideOffset / scaleFactor
                        content.scaleY = 1 - slideOffset / scaleFactor
                    } else {
                        // RTL
                        val moveFactor: Float = drawerView.width * slideOffset * -1
                        content.translationX = moveFactor
                        content.scaleX = 1 - (slideOffset) / scaleFactor
                        content.scaleY = 1 - (slideOffset) / scaleFactor
                    }
                    /* when {
                         drawerLayout.isDrawerVisible(drawerView) -> {
                             settings_layout.makeVisible()
                         }
                         else -> {
                             settings_layout.makeGone()
                         }
                     }*/
                }
            }
//        settings_layout.makeGone()
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 0f
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    // set toolbar title
    fun setToolbarTitle(title: String) {
        categories_heading.text = title
    }

    // show hide toolbar
    fun showHideToolbar(fragmentType: String) {
        when (fragmentType) {
            "home" -> {
                evaluator_toolbar.makeVisible()
                back_btn.makeGone()
                hamburger_btn.makeVisible()
                wishlist_container.makeVisible()
                search_container.makeVisible()
                categories_heading.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_emall_logo_white)
                categories_heading.text = ""
            }
            "wishlist" -> {
                evaluator_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeGone()
                search_container.makeVisible()
                categories_heading.background = null
            }
            "hide" -> {
                evaluator_toolbar.makeGone()
            }
            "staticPages" -> {
                evaluator_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeGone()
                search_container.makeGone()
                categories_heading.background = null
            }
            else -> {
                evaluator_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeVisible()
                search_container.makeVisible()
                categories_heading.background = null
            }
        }
    }

    // logout click listener
    private fun openSellerActivity() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@BuyerActivity)
        alertDialog.setTitle(getString(R.string.logout))
        alertDialog.setMessage(getString(R.string.logout_message))
        alertDialog.setPositiveButton(
            getString(R.string.logout)
        ) { _, _ ->
            val language = PreferenceHelper.readString(APP_LOCALE)
            val selectedCountry = PreferenceHelper.readString(SELECTED_COUNTRY)
            val selectedCurrency = PreferenceHelper.readString(SELECTED_CURRENCY)
            val selectedCountryName = PreferenceHelper.readString(Constants.SELECTED_COUNTRY_NAME)
            val selectedCountryFlag = PreferenceHelper.readString(Constants.SELECTED_COUNTRY_FLAG)
            val currencyRate = PreferenceHelper.readFloat(CURRENCY_RATE)
            cartItemCount(0)
            PreferenceHelper.clear()
            PreferenceHelper.writeString(APP_LOCALE, language!!)
            PreferenceHelper.writeString(SELECTED_COUNTRY, selectedCountry!!)
            PreferenceHelper.writeString(Constants.SELECTED_COUNTRY_NAME, selectedCountryName!!)
            PreferenceHelper.writeString(Constants.SELECTED_COUNTRY_FLAG, selectedCountryFlag!!)
            PreferenceHelper.writeString(SELECTED_CURRENCY, selectedCurrency!!)
            PreferenceHelper.writeFloat(CURRENCY_RATE, currencyRate!!)
            PreferenceHelper.writeBoolean(IS_NEW_DEVICE, true)
            finish()
            overridePendingTransition(0, 0)
            openActivity(SellerActivity::class.java)
            overridePendingTransition(0, 0)
        }
        alertDialog.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    // highlight bottom navigation item
    fun selectBottomNavigationItem(index: Int) {
        bottomNavigationView.menu.getItem(index).isChecked = true
    }

    // add wishList count
    fun wishListItemCount(count: Int) {
        PreferenceHelper.writeInt(Constants.TOTAL_WISHLIST_ITEMS, count)
        if (count > 0) {
            wishlist_counter.makeVisible()
            wishlist_counter.text = count.toString()
        }
    }


    // set cart item count
    fun cartItemCount(count: Int) {
//        val badge = bottomNavigationView.getOrCreateBadge(R.id.cart)
//        badge.isVisible = count != 0
//        badge.backgroundColor = getColor(R.color.selected_color)
//        badge.number = count
        if (count == 0) {
            badge?.cart_counter!!.makeGone()
        } else {
            badge?.cart_counter!!.makeVisible()
            badge?.cart_counter!!.text = count.toString()
        }
    }

    // get cart item count
    private fun getCartItemCount() {
        when {
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!! != 0 -> getCartItemList()
            else -> getCartItemListGuest()
        }
    }

    // get cart item list
    private fun getCartItemList() {
        eCommerceViewModel.getCartItems(
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage()
        )
            .observe(this, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { it ->
                                totalCartItems = 0
                                for (item in it.data.items) {
                                    totalCartItems += item.qty
                                }
                                PreferenceHelper.writeInt(TOTAL_CART_ITEMS, totalCartItems)
                                cartItemCount(totalCartItems)
                            }
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    // get cart item list for guest
    private fun getCartItemListGuest() {
        eCommerceViewModel.getCartItemsGuest(
            PreferenceHelper.readInt(Constants.GUEST_QUOTE_ID)!!,
            Utility.getLanguage()
        )
            .observe(this, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
                                totalCartItems = 0
                                for (item in it.data.items) {
                                    totalCartItems += item.qty
                                }
                                PreferenceHelper.writeInt(TOTAL_CART_ITEMS, totalCartItems)
                                cartItemCount(totalCartItems)
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

    // Request Permission required in address at e-commerce end
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}