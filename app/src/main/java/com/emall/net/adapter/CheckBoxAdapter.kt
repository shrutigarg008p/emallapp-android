package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemCheckListener
import com.google.android.material.checkbox.MaterialCheckBox

internal class CheckBoxAdapter(
    private var checkboxList: ArrayList<String>,
    val itemClick: OnItemCheckListener
) :
    RecyclerView.Adapter<CheckBoxAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkbox_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = checkboxList[position]
        holder.checkbox.text = item

//        val scale = holder.itemView.context.resources.displayMetrics.density
//        holder.checkbox.setPadding(holder.checkbox.paddingLeft + ((10.0f *scale + 0.5f ) as Int),holder.checkbox.paddingTop,
//            holder.checkbox.paddingRight,holder.checkbox.paddingBottom)
//
        holder.checkbox.setOnClickListener {
            holder.checkbox.isChecked = holder.checkbox.isChecked
            when {
                holder.checkbox.isChecked -> {
//                    holder.checkbox.buttonTintList= ColorStateList.valueOf(holder.itemView.context.getColor(R.color.selected_color))
                    itemClick.onItemCheck(position)
                }
                else -> {
//                    holder.checkbox.buttonTintList= ColorStateList.valueOf(holder.itemView.context.getColor(R.color.white))
                    itemClick.onItemUnCheck(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return checkboxList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkbox: MaterialCheckBox = view.findViewById(R.id.checkbox)
    }
}