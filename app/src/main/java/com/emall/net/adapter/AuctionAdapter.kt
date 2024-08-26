package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.auctions.auctionList.Auction
import com.google.android.material.button.MaterialButton

internal class AuctionAdapter(
    private var auctionItemList: ArrayList<Auction>,
    val onItemClick: ItemClick
) :
    RecyclerView.Adapter<AuctionAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.auction_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = auctionItemList[position]
        holder.auctionTime.text = item.ends_at_str
        holder.auctionStatus.text = item.status
        holder.auctionId.text = item.id.toString()
        holder.auctionDate.text = item.starts_at

        holder.viewBiddingButton.setOnClickListener {
            onItemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return auctionItemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var auctionDate: TextView = view.findViewById(R.id.auction_date)
        var auctionTime: TextView = view.findViewById(R.id.auction_time)
        var auctionStatus: TextView = view.findViewById(R.id.status)
        var auctionId: TextView = view.findViewById(R.id.auction_id)
        var viewBiddingButton: MaterialButton = view.findViewById(R.id.view_bidding_btn)
    }
}