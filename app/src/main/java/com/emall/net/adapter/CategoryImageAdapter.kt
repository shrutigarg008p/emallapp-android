package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.CategoryImageList
import com.squareup.picasso.Picasso

internal class CategoryImageAdapter(
    private var categoryImageList: ArrayList<CategoryImageList>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<CategoryImageAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_image_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = categoryImageList[position]
        Picasso.get().load(item.image).placeholder(R.drawable.progress_animation).into(holder.image)
        holder.image.setOnClickListener {
            itemClick.onItemClick(position, "categoryImageAdapter")
        }
    }

    override fun getItemCount(): Int {
        return categoryImageList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: AppCompatImageView = view.findViewById(R.id.image)
    }
}