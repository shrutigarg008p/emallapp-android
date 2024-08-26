package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.ProductInfo
import com.emall.net.listeners.ItemClick
import com.squareup.picasso.Picasso

internal class SellYourDeviceAdapter(
    private var itemList: List<ProductInfo>,
    private val itemCheck: ItemClick
) :
    RecyclerView.Adapter<SellYourDeviceAdapter.MyViewHolder>() {

    private var selectedItem: Int = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sell_your_device_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.type.text = item.title
        Picasso.get().load(item.image).into(holder.image)
        holder.imageView.visibility = View.GONE
        holder.type.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.black))
        if(selectedItem == position){
            holder.imageView.visibility = View.VISIBLE
            holder.type.setTextColor(ContextCompat.getColor(holder.imageView.context,R.color.selected_color))
        }
         holder.itemView.setOnClickListener {
             val previousItem = selectedItem
             selectedItem = position
             notifyItemChanged(previousItem)
             notifyItemChanged(position)
             itemCheck.itemClick(position)
         }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var type: AppCompatTextView = view.findViewById(R.id.heading)
        var imageView: AppCompatImageView = view.findViewById(R.id.checkBox)
        var image: AppCompatImageView = view.findViewById(R.id.image)
    }
}