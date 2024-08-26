package com.emall.net.fragment.payment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import kotlinx.android.synthetic.main.fragment_payment_slip.*

class PaymentSlipFragment: Fragment() {

    private val removePdfTopIcon =
        "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()"
    private val googleUrl = "http://docs.google.com/gview?embedded=true&url="

    private var paymentSlip: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_slip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle("Payment Slip")

        when{
            arguments != null -> paymentSlip = arguments?.getString("paymentSlip")
        }
        showPdfFile(paymentSlip!!)
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