package com.emall.net.fragment.staticPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.utils.Utility.replaceFragment
import kotlinx.android.synthetic.main.fragment_help_and_support.*

class HelpAndSupportFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_and_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("staticPages")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.help_support))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("staticPages")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.help_support))
            }
        }
        setUp()
    }

    private fun setUp() {
        contact_us_container.setOnClickListener(this)
        shipping_and_delivery_policy_container.setOnClickListener(this)
        exchange_and_return_policy_container.setOnClickListener(this)
        faq_container.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.contact_us_container -> openContactUsFragment()
            R.id.shipping_and_delivery_policy_container -> openShippingAndDeliveryPolicyFragment()
            R.id.exchange_and_return_policy_container -> openExchangeAndReturnPolicyFragment()
            R.id.faq_container -> openFaqEcomFragment()
        }
    }

    private fun openContactUsFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(ContactUsFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(ContactUsFragment(),
                R.id.container)
        }
    }

    private fun openShippingAndDeliveryPolicyFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(ShippingAndDeliveryPolicyFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(ShippingAndDeliveryPolicyFragment(),
                R.id.container)
        }
    }

    private fun openExchangeAndReturnPolicyFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(ExchangeAndReturnPolicyFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(ExchangeAndReturnPolicyFragment(),
                R.id.container)
        }
    }

    private fun openFaqEcomFragment() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).replaceFragment(FaqEcomFragment(),
                    R.id.container)
            }
            else -> (activity as BuyerActivity).replaceFragment(FaqEcomFragment(),
                R.id.container)
        }
    }
}