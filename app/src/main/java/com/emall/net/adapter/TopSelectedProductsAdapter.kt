package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.dashboard.DataTopTenSelected
import com.squareup.picasso.Picasso

class TopSelectedProductsAdapter(
    private val data: ArrayList<DataTopTenSelected>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<TopSelectedProductsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.top_selected_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.itemName.text = item.name
        Picasso.get().load(item.image).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position, "top selected")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: TextView = view.findViewById(R.id.name)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)
    }

}