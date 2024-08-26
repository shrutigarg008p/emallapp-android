package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.dashboard.DataMidBannerFour
import com.squareup.picasso.Picasso

class PopularSearchVerticalAdapter(private val data: ArrayList<DataMidBannerFour>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<PopularSearchVerticalAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.vertical_child_item_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.itemType.text = item.alt_img
        holder.itemName.text = item.caption
        Picasso.get().load(item.image).into(holder.image)

        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position,"top sales")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemType: TextView = view.findViewById(R.id.type)
        var itemName: TextView = view.findViewById(R.id.name)
        var nextBtn: AppCompatImageView = view.findViewById(R.id.next_btn_image)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)

    }

}