package com.emall.net.fragment.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.fragment.HomeFragment
import com.emall.net.utils.Utility.replaceFragment
import kotlinx.android.synthetic.main.fragment_order_place.*


class OrderPlaceFragment : Fragment() {
    private var orderId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) orderId = arguments?.getString("orderId")!!
        val newString: String = getString(R.string.order_details).replace("#345678", "#$orderId")

        order_description.text = newString

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.thank_you))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.thank_you))
            }
        }

        continue_shopping.setOnClickListener {
            when (activity) {
                is SellerActivity -> {
                    (activity as SellerActivity).replaceFragment(HomeFragment(),
                        R.id.container)
                    (activity as SellerActivity).selectBottomNavigationItem(
                        0)
                }
                else -> {
                    (activity as BuyerActivity).replaceFragment(HomeFragment(),
                        R.id.container)
                    (activity as BuyerActivity).selectBottomNavigationItem(
                        0)
                }
            }
        }
    }
}