package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.ItemList

internal class HomeAdapter(private var itemList: List<ItemList>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        val data = item.data[position]
        holder.heading.text = item.heading
        holder.heading2.text = item.heading2

        val childLayoutManger =
            LinearLayoutManager(holder.innerLayout.context, LinearLayoutManager.HORIZONTAL, false)
        holder.innerLayout.apply {
            layoutManager = childLayoutManger
            adapter = ChildAdapter(item.data, itemClick)
            setRecycledViewPool(recycledViewPool)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var heading: com.emall.net.customview.MulishRegular = view.findViewById(R.id.heading)
        var heading2: com.emall.net.customview.MulishRegular = view.findViewById(R.id.heading2)
        var innerLayout: RecyclerView = view.findViewById(R.id.inner_layout)
    }
}