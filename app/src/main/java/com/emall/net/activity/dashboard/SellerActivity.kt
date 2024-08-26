package com.emall.net.activity.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.loginSignUp.LoginActivity
import com.emall.net.adapter.*
import com.emall.net.fragment.*
import com.emall.net.fragment.address.ReCommerceAddressFragment
import com.emall.net.fragment.cart.CartFragment
import com.emall.net.fragment.category.CategoryFragment
import com.emall.net.fragment.createProduct.*
import com.emall.net.fragment.ecommerceAddress.AddressList
import com.emall.net.fragment.orders.OrderListFragment
import com.emall.net.fragment.profile.ProfileFragment
import com.emall.net.fragment.resolutionCenter.IssueListFragment
import com.emall.net.fragment.search.PaginationListener.Companion.TAG
import com.emall.net.fragment.search.SearchFragment
import com.emall.net.fragment.settings.SettingsFragment
import com.emall.net.fragment.wishList.WishListFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.DrawerItemList
import com.emall.net.network.api.*
import com.emall.net.network.model.wishlistResponse.Data
import com.emall.net.utils.*
import com.emall.net.utils.Constants.APP_LOCALE
import com.emall.net.utils.Constants.ARAB_LOCALE
import com.emall.net.utils.Constants.CURRENCY_RATE
import com.emall.net.utils.Constants.CUSTOMER_ID
import com.emall.net.utils.Constants.ENGLISH_LOCALE
import com.emall.net.utils.Constants.FROM_CART
import com.emall.net.utils.Constants.GUEST_QUOTE_ID
import com.emall.net.utils.Constants.HOME_FRAGMENT
import com.emall.net.utils.Constants.IS_NEW_DEVICE
import com.emall.net.utils.Constants.IS_SELLER
import com.emall.net.utils.Constants.SELECTED_COUNTRY
import com.emall.net.utils.Constants.SELECTED_COUNTRY_FLAG
import com.emall.net.utils.Constants.SELECTED_COUNTRY_NAME
import com.emall.net.utils.Constants.SELECTED_CURRENCY
import com.emall.net.utils.Constants.TOTAL_CART_ITEMS
import com.emall.net.utils.Constants.TOTAL_WISHLIST_ITEMS
import com.emall.net.utils.Constants.USER_NAME
import com.emall.net.utils.Utility.isCustomerLoggedIn
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.openActivity
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.showPopUp
import com.emall.net.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_seller.*
import kotlinx.android.synthetic.main.cart_notification_badge.*
import kotlinx.android.synthetic.main.cart_notification_badge.view.*
import kotlinx.android.synthetic.main.nav_footer_seller.*
import kotlinx.android.synthetic.main.nav_header_seller.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class SellerActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    OnItemClick, OnClickListener {

    private var totalCartItems: Int = 0
    private var wishListItems = ArrayList<Data>()
    private lateinit var drawerAdapter: DrawerAdapter
    private val drawerList = ArrayList<DrawerItemList>()
    private lateinit var eCommerceViewModel: MainViewModel
    var wishListItemsId = ArrayList<String>()
    var badge: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        PreferenceHelper.writeBoolean(Constants.FIRST_TIME_USER, true)

        val bottomNavigationMenuView: BottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val v: View = bottomNavigationMenuView.getChildAt(3)
        val itemView: BottomNavigationItemView = v as BottomNavigationItemView

        badge = LayoutInflater.from(this)
            .inflate(R.layout.cart_notification_badge, itemView, true)

        if (PreferenceHelper.readString(APP_LOCALE).equals(ENGLISH_LOCALE)) {
            english_text_view.text = getString(R.string.arab_language)
            PreferenceHelper.writeBoolean(HOME_FRAGMENT, true)
        } else {
            english_text_view.text = getString(R.string.english)
            PreferenceHelper.writeBoolean(HOME_FRAGMENT, true)
        }
        app_versions_seller.setTextColor(Color.BLACK)
        app_versions_seller.alpha = 0.44F
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> window.insetsController?.hide(
                WindowInsets.Type.statusBars()
            )
            else -> window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        }
    }


    override fun onStart() {
        super.onStart()
//
        setUpViewModel()
        updatePreferences()
        showHideLoginLogout()
        bottomNavigationView.setOnItemSelectedListener(this)
        logout_btn.setOnClickListener(this)
        fab.setOnClickListener(this)
        back_btn.setOnClickListener(this)
        hamburger_btn.setOnClickListener(this)
        login_register_btn.setOnClickListener(this)
        english_text_view.setOnClickListener(this)
        seller_name.setOnClickListener(this)

        when {
            PreferenceHelper.readBoolean(HOME_FRAGMENT)!! -> {
                if (PreferenceHelper.readBoolean(FROM_CART)!!) {
                    replaceFragment(CartFragment(), R.id.container)
                    PreferenceHelper.writeBoolean(HOME_FRAGMENT, false)
                    PreferenceHelper.writeBoolean(FROM_CART, false)
                    bottomNavigationView.menu.getItem(1).isChecked = true
                } else {
                    replaceFragment(HomeFragment(), R.id.container)
                    PreferenceHelper.writeBoolean(HOME_FRAGMENT, false)
                    bottomNavigationView.menu.getItem(0).isChecked = true
                }
            }

        }

        wishlist_container.setOnClickListener {
            when {
                isCustomerLoggedIn() -> replaceFragment(WishListFragment(), R.id.container)
                else -> openLoginDialog()
            }
        }
        search_container.setOnClickListener {
            val prFrag: Fragment = SearchFragment()
            supportFragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.container, prFrag).commit()

        }

        seller_name.setOnClickListener {
            drawerLayout.closeDrawers()
            replaceFragment(ProfileFragment(), R.id.container)
        }
        setupDrawerList()
    }

    fun updatePreferences() {
        when {
            isCustomerLoggedIn() -> {
                if (PreferenceHelper.readString(USER_NAME)!!.isNotEmpty()) {
                    seller_name.text =
                        PreferenceHelper.readString(USER_NAME)
                }
                if (PreferenceHelper.readString(SELECTED_COUNTRY_NAME)!!.isNotEmpty()) {
                    country_name_seller_login.text =
                        PreferenceHelper.readString(SELECTED_COUNTRY_NAME)!!
                }
                if (PreferenceHelper.readString(SELECTED_COUNTRY_FLAG)!!.isNotEmpty()) {
                    Picasso.get()
                        .load(PreferenceHelper.readString(
                            SELECTED_COUNTRY_FLAG))
                        .into(country_flag_seller_login)
                }
                logout_btn.makeVisible()
                login_layout.makeVisible()
                logout_layout.makeGone()
            }
            else -> {
                if (PreferenceHelper.readString(SELECTED_COUNTRY_NAME)!!
                        .isNotEmpty()
                ) {
                    country_name_seller.text =
                        PreferenceHelper.readString(SELECTED_COUNTRY_NAME)!!
                }

                if (PreferenceHelper.readString(SELECTED_COUNTRY_FLAG)!!
                        .isNotEmpty()
                ) {
                    Log.e("SELECTED_COUNTRY_FLAG", PreferenceHelper.readString(
                        SELECTED_COUNTRY_FLAG).toString())
                    Picasso.get()
                        .load(PreferenceHelper.readString(SELECTED_COUNTRY_FLAG))
                        .into(country_flag_seller)
                } else {
                    Log.e("SELECTED_COUNTRY_FLAG", "Null")
                }

                logout_btn.makeGone()
                login_layout.makeGone()
                logout_layout.makeVisible()
            }
        }
    }

    private fun showHideLoginLogout() {
        when {
            isCustomerLoggedIn() -> {
                logout_btn.makeVisible()
                login_layout.makeVisible()
                logout_layout.makeGone()
            }
            else -> {
                logout_btn.makeGone()
                login_layout.makeGone()
                logout_layout.makeVisible()
            }
        }
    }

    private fun setUpViewModel() {
        viewModelStore.clear()
        scopeMainThread.launch {
            eCommerceViewModel = getECommerceViewModel()
            getCartItemCount()
            when {
                isCustomerLoggedIn() -> getWishlistItemsCount()
                else -> {
                    if (PreferenceHelper.readInt(GUEST_QUOTE_ID) == 0) {
                        getGuestToken()
                    }
                }
            }

        }
    }

    private fun getGuestToken() {
        eCommerceViewModel.getGuestToken().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        PreferenceHelper.writeString(
                            Constants.GUEST_MASK_KEY,
                            resource.data?.mask_key.toString()
                        )
                        PreferenceHelper.writeInt(
                            GUEST_QUOTE_ID,
                            resource.data?.quote_id!!
                        )
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "error: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d(TAG, "loading: ${it.message} , ${it.status}")
                    }
                }
            }
        })
    }

    private fun openLoginDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@SellerActivity)
        alertDialog.setMessage(R.string.must_be_logged_in)
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            PreferenceHelper.writeBoolean(Constants.FROM_CART, false)
            openActivity(LoginActivity::class.java)
            finish()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun getWishlistItemsCount() {
        eCommerceViewModel.getWishlistItems(
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage()
        )
            .observe(this, androidx.lifecycle.Observer {
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
        var drawerItem = DrawerItemList(getString(R.string.sell_my_device), R.drawable.offers)
        drawerList.add(drawerItem)
        drawerItem = DrawerItemList(getString(R.string.track_devices), R.drawable.track_devices)
        drawerList.add(drawerItem)
        //drawerItem = DrawerItemList("Evaluation", R.drawable.track_devices)
        drawerItem = DrawerItemList(getString(R.string.evaluation), R.drawable.track_devices)
        drawerList.add(drawerItem)
        drawerItem =
            DrawerItemList(getString(R.string.resolution_center), R.drawable.resolution_center)
        drawerList.add(drawerItem)
        drawerItem =
            DrawerItemList(getString(R.string.auction_payments), R.drawable.auction_payments)
        drawerList.add(drawerItem)
        drawerItem = DrawerItemList(getString(R.string.my_messages), R.drawable.messages)
        drawerList.add(drawerItem)
        drawerItem = DrawerItemList(
            getString(R.string.addresses),
            R.drawable.evaluation_notification
        )
        drawerList.add(drawerItem)

        drawerItem = DrawerItemList(
            getString(R.string.settings),
            R.drawable.settings
        )
        drawerList.add(drawerItem)
        drawerItem = DrawerItemList(
            getString(R.string.my_orders),
            R.drawable.buying_orders
        )
        drawerList.add(drawerItem)


        drawerAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        drawerSlide()
    }

    fun moveToProductFragment(){
        for(i in 0 until supportFragmentManager.backStackEntryCount){
            supportFragmentManager.popBackStack()
        }
        when {
            PreferenceHelper.readString("product_activity").equals("auction", true) -> {
                replaceFragment(ProductFragment(), R.id.container)
                bottomNavigationView.menu.getItem(4).isChecked = true
            }
            PreferenceHelper.readString("product_activity").equals("evaluation", true) -> {
                replaceFragment(ProductFragment(), R.id.container)
                bottomNavigationView.menu.getItem(4).isChecked = true
            }
        }

    }

    // back button handled
    override fun onBackPressed() {
        PreferenceHelper.writeString("product_activity", "")
        PreferenceHelper.writeBoolean(HOME_FRAGMENT, false)
        when {
            supportFragmentManager.backStackEntryCount != 0 ->

                when (supportFragmentManager.backStackEntryCount) {
                    1 -> finish()
                    else -> supportFragmentManager.popBackStack()
                }
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(
                GravityCompat.END
            )
            else -> super.onBackPressed()
        }
    }

    // drawer item click listener
    override fun onItemClick(position: Int, type: String) {
        when {
            isCustomerLoggedIn() -> when (position) {
                0 -> replaceFragment(SellFragment(), R.id.container)
                2 -> {
                    PreferenceHelper.writeBoolean(IS_SELLER, false)
                    replaceFragment(ProductFragment(), R.id.container)
                }
                3 -> {
                    showHideToolbar("")
                    replaceFragment(IssueListFragment(), R.id.container)
                }
                6 -> replaceFragment(AddressList(), R.id.container)
                7 -> replaceFragment(SettingsFragment(), R.id.container)
                8 -> replaceFragment(OrderListFragment(), R.id.container)
            }
            else -> when (position) {
                7 -> replaceFragment(SettingsFragment(), R.id.container)

                else -> when {
                    drawerLayout.showPopUp(getString(R.string.please_login_to_application)) -> {
                        openActivity(LoginActivity::class.java)
                        finish()
                    }
                }
            }
        }
        drawerLayout.closeDrawers()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.logout_btn -> openSellerActivity()
            R.id.back_btn -> onBackPressed()
            R.id.fab -> when {
                isCustomerLoggedIn() -> {
                    replaceFragment(SellFragment(), R.id.container)
                    bottomNavigationView.menu.getItem(2).isChecked = true
                }
                else -> drawerLayout.showPopUp(getString(R.string.please_login_to_application))
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
                        PreferenceHelper.writeString(APP_LOCALE, ENGLISH_LOCALE)
                        PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, true)
                        Log.d("Seller Activity", Locale.getDefault().language)
                        english_text_view.text = getString(R.string.arab_language)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    else -> {
                        PreferenceHelper.writeString(APP_LOCALE, ARAB_LOCALE)
                        PreferenceHelper.writeBoolean(Constants.HOME_FRAGMENT, true)
                        Log.d("Seller Activity", Locale.getDefault().language)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_bar, menu)
        return true
    }

    // bottom navigation item selected listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.auction -> {
                if (isCustomerLoggedIn()) {
                    replaceFragment(ProductFragment(), R.id.container)
                    bottomNavigationView.menu.getItem(4).isChecked = true
                    PreferenceHelper.writeBoolean(IS_SELLER, true)
                } else
                    drawerLayout.showPopUp(getString(R.string.please_login_to_application))
            }
            R.id.cart -> {
                replaceFragment(CartFragment(), R.id.container)
                bottomNavigationView.menu.getItem(1).isChecked = true
            }
            R.id.sell -> {
                if (isCustomerLoggedIn()) {
                    replaceFragment(SellFragment(), R.id.container)
                    bottomNavigationView.menu.getItem(2).isChecked = true
                } else
                    drawerLayout.showPopUp(getString(R.string.please_login_to_application))
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

    // drawer slide
    private fun drawerSlide() {
        val actionBarDrawerToggle: ActionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                val scaleFactor = 6f
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)

                    if (Locale.getDefault().language.equals(ENGLISH_LOCALE)) {
                        // LTR
                        val slideX = drawerView.width * slideOffset
                        content.translationX = slideX
                        content.scaleX = 1 - slideOffset / scaleFactor
                        content.scaleY = 1 - slideOffset / scaleFactor
                    } else if (Locale.getDefault().language.equals(ARAB_LOCALE, true)) {
                        // RTL
                        val moveFactor: Float = drawerView.width * slideOffset * -1
                        content.translationX = moveFactor
                        content.scaleX = 1 - (slideOffset) / scaleFactor
                        content.scaleY = 1 - (slideOffset) / scaleFactor
                    }
                    /* when {
                         drawerLayout.isDrawerVisible(drawerView) -> settings_layout.visibility =
                             VISIBLE
                         else -> settings_layout.visibility = GONE
                     }*/
                }
            }
//        settings_layout.visibility = GONE
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 0f
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    // set toolbar title
    fun setToolbarTitle(title: String) {
        categories_heading.text = title
    }

    // show/hide toolbar
    fun showHideToolbar(fragmentType: String) {
        when (fragmentType) {
            "home" -> {
                seller_toolbar.makeVisible()
                back_btn.makeGone()
                hamburger_btn.makeVisible()
                wishlist_container.makeVisible()
                search_container.makeVisible()
                categories_heading.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_emall_logo_white)
                categories_heading.text = ""
            }
            "wishlist" -> {
                seller_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeGone()
                search_container.makeVisible()
                categories_heading.background = null
            }
            "hide" -> {
                seller_toolbar.makeGone()
            }
            "staticPages" -> {
                seller_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeGone()
                search_container.makeGone()
                categories_heading.background = null
            }
            else -> {
                seller_toolbar.makeVisible()
                back_btn.makeVisible()
                hamburger_btn.makeGone()
                wishlist_container.makeVisible()
                search_container.makeVisible()
                categories_heading.background = null
            }
        }
    }

    // highlight bottom navigation item
    fun selectBottomNavigationItem(index: Int) {
        bottomNavigationView.menu.getItem(index).isChecked = true
    }

    // logout click listener
    private fun openSellerActivity() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@SellerActivity)
        alertDialog.setTitle(getString(R.string.logout))
        alertDialog.setMessage(getString(R.string.logout_message))
        alertDialog.setPositiveButton(
            getString(R.string.logout)
        ) { _, _ ->
            val language = PreferenceHelper.readString(APP_LOCALE)
            val selectedCountry = PreferenceHelper.readString(SELECTED_COUNTRY)
            val selectedCountryName = PreferenceHelper.readString(SELECTED_COUNTRY_NAME)
            val selectedCountryFlag = PreferenceHelper.readString(SELECTED_COUNTRY_FLAG)
            val selectedCurrency = PreferenceHelper.readString(SELECTED_CURRENCY)
            val currencyRate = PreferenceHelper.readFloat(CURRENCY_RATE)
            cartItemCount(0)
            PreferenceHelper.clear()
            PreferenceHelper.writeString(APP_LOCALE, language!!)
            PreferenceHelper.writeString(SELECTED_COUNTRY, selectedCountry!!)
            PreferenceHelper.writeString(SELECTED_COUNTRY_NAME, selectedCountryName!!)
            PreferenceHelper.writeString(SELECTED_COUNTRY_FLAG, selectedCountryFlag!!)
            PreferenceHelper.writeString(SELECTED_CURRENCY, selectedCurrency!!)
            PreferenceHelper.writeFloat(CURRENCY_RATE, currencyRate!!)
            PreferenceHelper.writeBoolean(IS_NEW_DEVICE, true)
            PreferenceHelper.writeInt(CUSTOMER_ID, 0)
            updatePreferences()
            drawerLayout.closeDrawers()
            drawerLayout.invalidate()
            showHideLoginLogout()
        }
        alertDialog.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    // set cart count
    fun cartItemCount(count: Int) {
//        val badge = bottomNavigationView.getOrCreateBadge(R.id.cart)
//        badge.isVisible = count != 0
//        badge.backgroundColor = getColor(R.color.selected_color)
        if (count == 0) {
            badge?.cart_counter!!.makeGone()
        } else {
            badge?.cart_counter!!.makeVisible()
            badge?.cart_counter!!.text = count.toString()
        }
    }

    // get cart count
    private fun getCartItemCount() {
        when {
            isCustomerLoggedIn() -> getCartItemList()
            else -> getCartItemListGuest()
        }
    }

    // get cart list
    private fun getCartItemList() {
        eCommerceViewModel.getCartItems(
            PreferenceHelper.readInt(Constants.CUSTOMER_ID)!!,
            Utility.getLanguage()
        )
            .observe(this, androidx.lifecycle.Observer {
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
            .observe(this, androidx.lifecycle.Observer { it ->
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

    fun wishListItemCount(count: Int) {
        PreferenceHelper.writeInt(TOTAL_WISHLIST_ITEMS, count)
        if (count > 0) {
            wishlist_counter.makeVisible()
            wishlist_counter.text = count.toString()
        } else {
            wishlist_counter.makeGone()
        }
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
