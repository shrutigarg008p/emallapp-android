package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.getShippingInformationResponse.TotalSegment
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone

internal class CheckOutAdapter(
    private var totalSegment: ArrayList<TotalSegment>,
) :
    RecyclerView.Adapter<CheckOutAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkout_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = totalSegment[position]
        if (item.title.equals("Tax", true) || item.value.toInt() == 0) {
            holder.checkoutTitle.makeGone()
            holder.checkoutValue.makeGone()
        }
        holder.checkoutTitle.text = item.title
        holder.checkoutValue.text =
            "${PreferenceHelper.readString(Constants.SELECTED_CURRENCY)} ${item.value.toInt()}"
    }

    override fun getItemCount(): Int {
        return totalSegment.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkoutTitle: TextView = view.findViewById(R.id.checkout_title)
        var checkoutValue: TextView = view.findViewById(R.id.checkout_value)
    }
}