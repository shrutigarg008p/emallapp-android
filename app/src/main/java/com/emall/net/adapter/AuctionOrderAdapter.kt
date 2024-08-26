package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.auctionOrders.DataX
import com.squareup.picasso.Picasso

internal class AuctionOrderAdapter(
    private var auctionOrderList: ArrayList<DataX>
) :
    RecyclerView.Adapter<AuctionOrderAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recom_order_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = auctionOrderList[position]

        when {
            item.auction.product.base_image.isNotEmpty() -> Picasso.get().load(item.auction.product.base_image).into(holder.image)
        }
        holder.status.text = item.status
        holder.date.text = item.created_at_str
        holder.orderId.text = item.id.toString()
        holder.name.text = item.auction.product.device_name
        holder.evaluationId.text = item.auction.id.toString()
        holder.brandName.text = item.auction.product.brand
        holder.modelName.text = item.auction.product.model
        holder.color.text = item.auction.product.variant
        holder.idHeading.text = holder.itemView.resources.getString(R.string.auction_id)
    }

    override fun getItemCount(): Int {
        return auctionOrderList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.image)
        var status: TextView = view.findViewById(R.id.item_status)
        var date: TextView = view.findViewById(R.id.item_message)
        var orderId: TextView = view.findViewById(R.id.order_id)
        var name: TextView = view.findViewById(R.id.item_name)
        var evaluationId: TextView = view.findViewById(R.id.item_id)
        var idHeading: TextView = view.findViewById(R.id.textView16)
        var brandName: TextView = view.findViewById(R.id.item_brand)
        var modelName: TextView = view.findViewById(R.id.item_model)
        var color: TextView = view.findViewById(R.id.item_color)
    }
}