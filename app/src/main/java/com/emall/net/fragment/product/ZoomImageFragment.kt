package com.emall.net.fragment.product

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.emall.net.R
import com.emall.net.adapter.ZoomSliderAdapter
import com.google.android.material.tabs.TabLayout

class ZoomImageFragment : DialogFragment() {

    private var dialog: AlertDialog? = null
    private var position = 0
    private var imageList: ArrayList<String>? = null
    private var iv_close: ImageView? = null
    private var zoomviewPager: ViewPager? = null
    private var indicatorzoom: TabLayout? = null
    private var currentpage: TextView? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_zoom_image, null)
        iv_close = view.findViewById(R.id.imageView_close)
        zoomviewPager = view.findViewById(R.id.zoomviewPager)
        indicatorzoom = view.findViewById(R.id.indicatorzoom)
        currentpage = view.findViewById(R.id.currentpage)
        iv_close!!.setOnClickListener(View.OnClickListener { dialog!!.cancel() })
        imageList = requireArguments().getStringArrayList("zoomimagelist")
        position = requireArguments().getInt("position")
        intiViewPager(imageList!!, view)
        indicatorzoom!!.setupWithViewPager(zoomviewPager!!, true)
        builder.setView(view)
        dialog = builder.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return dialog!!
    }

    override fun onStart() {
        super.onStart()
        val dialog = getDialog()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }


    private fun intiViewPager(productDetail: List<String>, view: View) {
        zoomviewPager!!.offscreenPageLimit = productDetail.size
        zoomviewPager!!.adapter = ZoomSliderAdapter(view.context, productDetail)
        zoomviewPager!!.post(Runnable { zoomviewPager!!.currentItem = position })
        zoomviewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                currentpage!!.text = (position + 1).toString() + "/" + productDetail.size
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })
    }
}

