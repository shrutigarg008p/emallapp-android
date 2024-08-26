package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.utils.Constants.ECOMMERCE_PRODUCTION_URL
import com.emall.net.utils.Constants.ECOMMERCE_STAGING_URL
import com.emall.net.utils.Constants.SELECTED_COUNTRY
import com.emall.net.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import java.util.*

class BottomSheetCountryAdapter(
    private var countryDetail: ArrayList<GetCountryDetailData>,
    val itemClick: ItemClick,

    ) :
    RecyclerView.Adapter<BottomSheetCountryAdapter.MyViewHolder>() {
    private var itemView: View? = null
    private var selectedItem: Int = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_country_item, parent, false)
        return MyViewHolder(itemView!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = countryDetail[position]
        holder.countryName.text = item.name
        if (PreferenceHelper.readString(SELECTED_COUNTRY)!!.equals(item.code, true)) {
            selectedItem = position
            holder.countryRadioImage.setImageResource(R.drawable.radio_button_checked)
        } else {
            holder.countryRadioImage.setImageResource(R.drawable.radio_button_unchecked)
        }
        Picasso.get().load(ECOMMERCE_STAGING_URL + item.flag)
            .placeholder(R.drawable.uae_flag).into(holder.countryFlag)

        holder.countrySelector.setOnClickListener {
            val previousItem = selectedItem
            selectedItem = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return countryDetail.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var countrySelector: ConstraintLayout = view.findViewById(R.id.country_selector)
        var countryRadioImage: ImageView = view.findViewById(R.id.country_radio_image)
        var countryName: TextView = view.findViewById(R.id.country_name)
        var countryFlag: ImageView = view.findViewById(R.id.country_flag)
    }
}