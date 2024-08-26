package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.google.android.material.radiobutton.MaterialRadioButton

internal class RadioButtonAdapter(
    private var radioButtonsList: ArrayList<String>,
    val onItemClick: ItemClick,
) :
    RecyclerView.Adapter<RadioButtonAdapter.MyViewHolder>() {

    private var lastSelectedPosition = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.radio_btn_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = radioButtonsList[position]
        holder.radioBtn.text = item

        holder.radioBtn.isChecked = lastSelectedPosition == position
        holder.radioBtn.setOnClickListener {
            lastSelectedPosition = position
            notifyItemRangeChanged(0, radioButtonsList.size)
            onItemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return radioButtonsList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var radioBtn: MaterialRadioButton = view.findViewById(R.id.radio_button)
    }
}