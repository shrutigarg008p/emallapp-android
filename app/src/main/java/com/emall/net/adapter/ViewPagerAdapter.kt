package com.emall.net.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.squareup.picasso.Picasso
import java.util.*

class ViewPagerAdapter(private var mainBannerList: ArrayList<String>, val itemClick: OnItemClick) :
    PagerAdapter() {

    override fun getCount(): Int = mainBannerList.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_pager_image, null)
        val imageView = view.findViewById<View>(R.id.image) as AppCompatImageView
        Picasso.get().load(mainBannerList[position]).into(imageView)
        imageView.setOnClickListener {
            itemClick.onItemClick(position, "topBanner")
        }
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val vp = container as ViewPager
        val view = obj as View
        vp.removeView(view)
    }
}