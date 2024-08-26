package com.emall.net.fragment.payment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.*
import com.emall.net.utils.Constants.PRODUCT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_shipment.*


class ShipmentFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var addressId: Int? = 0
    private var shipmentId: Int? = 0
    private var waybill: Boolean? = false
    private val removePdfTopIcon =
        "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()"
    private val googleUrl = "http://docs.google.com/gview?embedded=true&url="

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_shipment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)
        when {
            arguments != null -> {
                addressId = arguments?.getInt("addressId")
                shipmentId = arguments?.getInt("shipmentId")
                waybill = arguments?.getBoolean("waybill")
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        if (waybill!!) {
            viewModel.getShipmentDetail(shipmentId!!, "Bearer $token",Utility.getLanguage())
                .observe(viewLifecycleOwner,
                    {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.DATA?.let { it ->
                                        progress_bar.makeGone()
                                        seller_address.text = it.ship_from_str
                                        evaluator_address.text = it.ship_to_str
                                        showPdfFile(it.bill_sticker_pdf)
                                    }
                                }
                                Status.LOADING -> progress_bar.makeVisible()
                                Status.ERROR -> {
                                    progress_bar.makeGone()
                                    constraintLayout.snack(it.message!!)
                                }
                            }
                        }
                    })
        } else {
            viewModel.generateShipment(PreferenceHelper.readInt(PRODUCT_ID)!!,
                addressId!!,
                "Bearer $token",Utility.getLanguage())
                .observe(viewLifecycleOwner,
                    {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.DATA?.let { it ->
                                        progress_bar.makeGone()
                                        seller_address.text = it.ship_from_str
                                        evaluator_address.text = it.ship_to_str
                                        showPdfFile(it.bill_sticker_pdf)
                                    }
                                }
                                Status.LOADING -> progress_bar.makeVisible()
                                Status.ERROR -> {
                                    progress_bar.makeGone()
                                    constraintLayout.snack(it.message!!)
                                }
                            }
                        }
                    })
        }
    }

    private fun showPdfFile(billStickerPdf: String) {
        waybill_web_view.invalidate()
        waybill_web_view.settings.setSupportZoom(true)
        waybill_web_view.settings.javaScriptEnabled = true
        waybill_web_view.loadUrl(googleUrl + billStickerPdf)

        waybill_web_view.webViewClient = object : WebViewClient() {
            var checkOnPageStartedCalled = false
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?,
            ) {
                checkOnPageStartedCalled = true
            }

            override fun onPageFinished(
                view: WebView,
                url: String,
            ) {
                if (checkOnPageStartedCalled) {
                    waybill_web_view.loadUrl(removePdfTopIcon)
                } else {
                    showPdfFile(billStickerPdf)
                }
            }
        }
    }
}