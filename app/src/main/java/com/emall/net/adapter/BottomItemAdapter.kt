package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.BottomItemList
import com.squareup.picasso.Picasso

class BottomItemAdapter(private val data: ArrayList<BottomItemList>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<BottomItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_item_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.heading.text = item.heading
        holder.subHeading.text = item.subHeading
        Picasso.get().load(item.icon).into(holder.image)
        holder.constrainLayout.setBackgroundResource(item.backgroundImage)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position, "bottom")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var heading: TextView = view.findViewById(R.id.item_name)
        var subHeading: TextView = view.findViewById(R.id.item_price)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)
        var constrainLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout)

    }

}