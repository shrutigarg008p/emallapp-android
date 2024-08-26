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
import com.emall.net.model.ItemData
import com.squareup.picasso.Picasso

class ChildAdapter(private val data: List<ItemData>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<ChildAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.child_item_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChildAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.rating.text = item.rating.toString()
        holder.discountedPrice.text = item.discountedPrice
        holder.discountedPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.price.text = item.price
        holder.itemType.text = item.itemType
        holder.itemName.text = item.itemName
        Picasso.get().load(item.image).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position,HomeAdapter::class.java.simpleName)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemType: TextView = view.findViewById(R.id.type)
        var itemName: TextView = view.findViewById(R.id.name)
        var rating: com.emall.net.customview.MulishRegular = view.findViewById(R.id.rating_text)
        var discountedPrice: com.emall.net.customview.MulishRegular =
            view.findViewById(R.id.discounted_price)
        var price: com.emall.net.customview.MulishRegular = view.findViewById(R.id.price)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)

    }

}