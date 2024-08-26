package com.emall.net.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.emall.net.R
import com.squareup.picasso.Picasso

class ZoomSliderAdapter(
    private val context: Context,
    private val myData: List<String>
) :
    PagerAdapter() {
    var image: ImageView? = null
    override fun getCount(): Int {
        return myData.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.zoom_image_slider_layout, null)
        image = view.findViewById<View>(R.id.zoom_slider_item) as ImageView
        Picasso.get().load(myData[position]).placeholder(R.drawable.progress_animation).into(image)
        val viewPager = container as ViewPager
        viewPager.addView(view, position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val viewPager = container as ViewPager
        val view = obj as View
        viewPager.removeView(view)
    }
}