package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.ImageItem
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

internal class ColorAdapter(private var imageList: List<ImageItem>, val onItemClick: OnItemClick) :
    RecyclerView.Adapter<ColorAdapter.MyViewHolder>() {
    private var selectedItem: Int
    private var lastClickedPosition: Int

    init {
        selectedItem = 0
        lastClickedPosition = -1
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = imageList[position]
        if (selectedItem == position) {
            holder.divider.makeVisible()
        } else {
            holder.divider.makeGone()
        }
        if (item.image != "") {
            Picasso.get().load(item.image).into(holder.image)
        } else {
            Picasso.get().load("https://images.pexels.com/videos/3045163/free-video-3045163.jpg")
                .into(holder.image)
        }

        if (imageList.size > 1) {
            holder.image.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = position
                notifyItemChanged(previousItem)
                notifyItemChanged(position)
                onItemClick.onItemClick(position, "colors")
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: AppCompatImageView = view.findViewById(R.id.image)
        var divider: View = view.findViewById(R.id.divider)
    }
}