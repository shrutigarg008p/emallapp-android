package com.emall.net.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.dashboard.DataHotSale
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

class HotSaleAdapter(private val data: ArrayList<DataHotSale>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<HotSaleAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.child_item_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        if (item.old_price > 0) {
            holder.discountedPrice.makeVisible()
        } else {
            holder.discountedPrice.makeGone()
        }
        holder.discountedPrice.text = Utility.changedCurrencyPrice(item.old_price)
        holder.discountedPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.price.text = Utility.changedCurrencyPrice(item.price)
        holder.itemType.text = item.category_name
        holder.itemName.text = item.name
        Picasso.get().load(item.image).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position, "hot sale")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemType: TextView = view.findViewById(R.id.type)
        var itemName: TextView = view.findViewById(R.id.name)
        var discountedPrice: com.emall.net.customview.MulishRegular =
            view.findViewById(R.id.discounted_price)
        var price: com.emall.net.customview.MulishRegular = view.findViewById(R.id.price)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)

    }

}