package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.model.AdditionalData

internal class AdditionalDataAdapter(
    private var additionalData: ArrayList<AdditionalData>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<AdditionalDataAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.additional_data_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = additionalData[position]
        holder.additionalInfoTitle.text = item.itemTitle
        holder.additionalInfoValue.text = item.itemValue
    }

    override fun getItemCount(): Int {
        return additionalData.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var additionalInfoTitle: TextView = view.findViewById(R.id.additional_info_title)
        var additionalInfoValue: TextView = view.findViewById(R.id.additional_info_value)
    }
}