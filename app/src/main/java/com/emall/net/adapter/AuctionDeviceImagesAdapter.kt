package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.squareup.picasso.Picasso

internal class AuctionDeviceImagesAdapter(private var imageList: List<String>) :
    RecyclerView.Adapter<AuctionDeviceImagesAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = imageList[position]
        if(item.isNotEmpty())
            Picasso.get().load(item).into(holder.image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: AppCompatImageView = view.findViewById(R.id.image)
    }
}