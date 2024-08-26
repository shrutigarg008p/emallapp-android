package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.sellerUserAddress.DataX
import com.emall.net.utils.Constants.DELETE
import com.emall.net.utils.Constants.EDIT
import com.emall.net.utils.Constants.PICKUP
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton

internal class SavedAddressAdapter(
    private var itemList: List<DataX>, private val selectedItemClick: OnItemClick
) :
    RecyclerView.Adapter<SavedAddressAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_address_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.address.text = "${item.address_1} ${item.address_2}"

        holder.editBtn.setOnClickListener {
            selectedItemClick.onItemClick(position, EDIT)
        }

        holder.deleteBtn.setOnClickListener {
            selectedItemClick.onItemClick(position, DELETE)
        }

        /* if (PreferenceHelper.readBoolean(COMES_FROM_PAYMENT)!! )
             holder.processForPickUpBtn.visibility = View.VISIBLE
         else
             holder.processForPickUpBtn.visibility = View.GONE
 */
        holder.itemView.setOnClickListener {
            holder.address.isSelected = true
        }

/*
        holder.processForPickUpBtn.setOnClickListener {
            selectedItemClick.onItemClick(position, PICKUP)
        }
*/

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var address_type: TextView = view.findViewById(R.id.address_type)
        var address: MaterialRadioButton = view.findViewById(R.id.address)
        var editBtn: MaterialButton = view.findViewById(R.id.edit_btn)
        var deleteBtn: MaterialButton = view.findViewById(R.id.delete_btn)
//        var processForPickUpBtn: MaterialButton = view.findViewById(R.id.process_for_pickup_btn)

    }
}