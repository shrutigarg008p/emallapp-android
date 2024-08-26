package com.emall.net.fragment.createProduct

import android.os.Bundle
import android.view.*
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.*
import com.emall.net.fragment.staticPages.MyBrowser
import com.emall.net.utils.Constants.SERIAL_NUMBER_DATA
import com.emall.net.utils.Constants.TRACK_MY_SHIPMENT
import kotlinx.android.synthetic.main.serial_number_web_view.*

class SerialNumberWebViewFragment : BaseFragment() {

    private var serialNumberData: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.serial_number_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle("Serial Number")
                when {
                    arguments != null -> serialNumberData = arguments?.getString(SERIAL_NUMBER_DATA)
                }
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle("Track My Shipment")
                when{
                    arguments != null -> serialNumberData = arguments?.getString(TRACK_MY_SHIPMENT)
                }
            }
        }

        serial_number_web_view.webViewClient = MyBrowser(requireContext())
        serial_number_web_view.settings.setSupportZoom(true)
        serial_number_web_view.settings.javaScriptEnabled = true
        serial_number_web_view.loadDataWithBaseURL(
            null,
            serialNumberData!!,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )
    }
}