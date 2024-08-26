package com.emall.net.adapter

import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.trackShipments.DataX
import com.emall.net.utils.Constants.WAY_BILL
import java.util.*

internal class TrackShipmentAdapter(
    private var paymentList: ArrayList<DataX>,
    val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<TrackShipmentAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_shipment_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = paymentList[position]
        holder.itemStatus.text = item.status
        holder.shipmentDate.text = item.updated_at
        holder.shipmentFrom.text = item.ship_from_str
        holder.shipmentTo.text = item.ship_to_str
        holder.orderId.text = item.id.toString()
        holder.wayBillNumber.text = item.waybill_ref
        holder.trackBtn.setOnClickListener{
            onItemClick.onItemClick(position,"track")
        }
        holder.showBtn.setOnClickListener{
            onItemClick.onItemClick(position,WAY_BILL)
        }
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemStatus: TextView = view.findViewById(R.id.item_status)
        var shipmentDate: TextView = view.findViewById(R.id.shipment_date)
        var orderId: TextView = view.findViewById(R.id.order_id)
        var shipmentFrom: TextView = view.findViewById(R.id.from)
        var shipmentTo: TextView = view.findViewById(R.id.to)
        var wayBillNumber: TextView = view.findViewById(R.id.way_bill)
        var trackBtn: TextView = view.findViewById(R.id.track_btn)
        var showBtn: TextView = view.findViewById(R.id.show_btn)
    }
}