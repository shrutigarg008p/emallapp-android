package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.biddingDetails.Bidding
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.google.android.material.button.MaterialButton

internal class BiddingAdapter(
    private var biddingList: ArrayList<Bidding>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<BiddingAdapter.MyViewHolder>() {

    private var actionType: String? = ""
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bidding_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = biddingList[position]
        holder.biddingDate.text = item.selected_at_str
        holder.bidPrice.text = item.amount
        holder.personName.text = "${item.selected_by.first_name} ${item.selected_by.last_name}"
        holder.acceptBidButton.text = item.action.action
        holder.rejectBtn.makeGone()
        if(PreferenceHelper.readBoolean("biddingDate")!!){
            holder.acceptBidButton.makeVisible()
        }else{
            holder.acceptBidButton.makeGone()
        }

        actionType = item.action.message
        holder.processShipment.text = actionType

        if(item.action.action.isEmpty()) {
            holder.acceptBidButton.makeGone()
            if(item.action.message.equals("Payment Complete",true)){
                holder.acceptBidButton.makeVisible()
                holder.acceptBidButton.text = item.action.message
            }
        }else {
            holder.acceptBidButton.makeVisible()
        }

        holder.acceptBidButton.setOnClickListener {
            when {
                actionType!!.equals("accept bidding", true) ->
                    itemClick.onItemClick(position, "accept bidding")
                actionType!!.equals("Waiting For The Bidder To Acknowledge",true) -> {
                }
                actionType!!.equals("Please update your bank details",true) ->
                    itemClick.onItemClick(position,"add bank details")
                actionType!!.equals("Proceed To Pay",true)->
                    itemClick.onItemClick(position,"stc payment")
                actionType!!.equals("Process For Shipment",true) ->
                    itemClick.onItemClick(position,"add address")
            }

        }
    }

    override fun getItemCount(): Int {
        return biddingList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var constraintLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout)
        var biddingDate: TextView = view.findViewById(R.id.auction_date)
        var personName: TextView = view.findViewById(R.id.person_name)
        var bidPrice: TextView = view.findViewById(R.id.bid_price)
        var processShipment: TextView = view.findViewById(R.id.process_shipment_text_view)
        var acceptBidButton: MaterialButton = view.findViewById(R.id.accept_bid_btn)
        var rejectBtn: MaterialButton = view.findViewById(R.id.reject_bid_btn)
    }
}