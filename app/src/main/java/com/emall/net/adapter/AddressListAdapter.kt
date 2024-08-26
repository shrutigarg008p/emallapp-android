package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.addressList.AddressListData
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper

internal class AddressListAdapter(
    private var addressList: ArrayList<AddressListData>,
    private val itemClick: ItemClick
) :
    RecyclerView.Adapter<AddressListAdapter.MyViewHolder>() {
    private var lastSelectedPosition = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.address_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = addressList[position]
        holder.userName.text = "${item.firstname} ${item.lastname}"
        holder.userAddress.text = "${item.street} ${item.city} ${item.country}"

        when {
            PreferenceHelper.readString(Constants.SELECTED_ADDRESS)!! == item.address_id -> {
                lastSelectedPosition = position
                holder.addressIcon.setImageResource(R.drawable.radio_button_checked)
            }
            else -> {
                holder.addressIcon.setImageResource(R.drawable.radio_button_unchecked)
            }
        }

        holder.addressIconContainer.setOnClickListener {
            val previousItem = lastSelectedPosition
            lastSelectedPosition = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var userName: TextView = view.findViewById(R.id.user_name)
        var userAddress: TextView = view.findViewById(R.id.user_address)
        var addressIcon: AppCompatImageView = view.findViewById(R.id.address_icon)
        var addressIconContainer: ConstraintLayout = view.findViewById(R.id.address_icon_container)
    }
}