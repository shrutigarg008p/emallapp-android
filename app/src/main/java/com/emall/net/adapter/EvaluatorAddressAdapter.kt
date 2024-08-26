package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.evaluationUserAddress.Address
import com.emall.net.utils.Constants
import com.emall.net.utils.Constants.PICKUP
import com.emall.net.utils.PreferenceHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton

internal class EvaluatorAddressAdapter(
    private var itemList: List<Address>, private val selectedItemClick: OnItemClick
) :
    RecyclerView.Adapter<EvaluatorAddressAdapter.MyViewHolder>() {

    private var lastSelectedPosition = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_address_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.address.text = "${item.address_1} ${item.address_2}"
        holder.addressType.text = "${holder.itemView.resources.getString(R.string.address)} ${(position + 1)}"
        holder.address.isChecked = lastSelectedPosition == position

        holder.editBtn.setOnClickListener {
            selectedItemClick.onItemClick(position, Constants.EDIT)
        }

        holder.deleteBtn.setOnClickListener {
            selectedItemClick.onItemClick(position, Constants.DELETE)
        }

        holder.address.setOnClickListener {
            lastSelectedPosition = position
            notifyItemRangeChanged(0, itemList.size)
            selectedItemClick.onItemClick(position, PICKUP)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var addressType: TextView = view.findViewById(R.id.address_type)
        var address: MaterialRadioButton = view.findViewById(R.id.address)
        var editBtn: AppCompatImageView = view.findViewById(R.id.edit_btn)
        var deleteBtn: AppCompatImageView = view.findViewById(R.id.delete_btn)
    }
}