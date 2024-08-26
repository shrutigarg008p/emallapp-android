package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.DrawerItemList
import com.squareup.picasso.Picasso

internal class DrawerAdapter(private var drawerList: List<DrawerItemList>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<DrawerAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.drawer_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = drawerList[position]
        holder.title.text = item.title
        Picasso.get().load(item.image).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position,"drawerAdapter")
        }
    }

    override fun getItemCount(): Int {
        return drawerList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: AppCompatTextView = view.findViewById(R.id.drawer_item)
        var image : AppCompatImageView = view.findViewById(R.id.image)
    }
}