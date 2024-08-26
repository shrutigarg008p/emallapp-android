package com.emall.net.fragment.trackShipments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.*
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.utils.Constants.EVALUATOR_ADDRESS
import com.emall.net.utils.Constants.SELLER_ADDRESS
import com.emall.net.utils.Constants.WAY_BILL
import kotlinx.android.synthetic.main.fragment_way_bill.*
import kotlinx.coroutines.launch

class WayBillFragment : BaseFragment(){

    private val removePdfTopIcon =
        "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()"
    private val googleUrl = "http://docs.google.com/gview?embedded=true&url="
    private var sellerAddress: String =""
    private var evaluatorAddress: String =""
    private var wayBill: String =""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_way_bill,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scopeMainThread.launch {
            arguments?.let {
                sellerAddress = arguments?.getString(SELLER_ADDRESS)!!
                evaluatorAddress = arguments?.getString(EVALUATOR_ADDRESS)!!
                wayBill = arguments?.getString(WAY_BILL)!!
            }
            seller_address.text = sellerAddress
            evaluator_address.text = evaluatorAddress
            showPdfFile(wayBill)
        }
    }

    private fun showPdfFile(wayBill: String) {
        waybill_web_view.invalidate()
        waybill_web_view.settings.setSupportZoom(true)
        waybill_web_view.settings.javaScriptEnabled = true
        waybill_web_view.loadUrl(googleUrl + wayBill)

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
                    showPdfFile(wayBill)
                }
            }
        }
    }
}