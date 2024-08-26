package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.*
import com.emall.net.network.model.auctionDevices.DATAX
import com.emall.net.utils.Utility.makeGone
import com.squareup.picasso.Picasso

internal class AuctionDevicesAdapter(
    private var auctionDevicesList: ArrayList<DATAX>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<AuctionDevicesAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.auction_devices_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = auctionDevicesList[position]
        holder.name.text = item.product.device_name
        holder.evaluationId.text = item.id.toString()
        holder.brandName.text = item.product.brand
        holder.modelName.text = item.product.model
        holder.color.text = item.product.variant
        holder.storage.makeGone()
        holder.storageHeading.makeGone()
        holder.ram.makeGone()
        holder.ramHeading.makeGone()
        holder.status.makeGone()
        holder.message.makeGone()
        holder.currentBid.text = item.bid_live_str
        holder.mainStatus.text = item.status_str
        holder.idHeading.text = holder.itemView.resources.getString(R.string.auction_id)
//        holder.expiryTime.text = item.ends_at_str
        holder.mainStatus.text = item.btn_link_str
        holder.raiseIssueBtn.makeGone()
        holder.chatWithSellerContainer.makeGone()
        if(item.product.base_image.isNotEmpty())
            Picasso.get().load(item.product.base_image).into(holder.image)

        when {
            item.is_wishlist -> holder.favoriteIcon.setImageResource(R.drawable.selected_star_icon)
            else -> holder.favoriteIcon.setImageResource(R.drawable.star_icon)
        }

//        holder.isNew.text = if(item.is_new) "New" else ""
//        holder.mainStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_color))
//        holder.status.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.bt_blue))

       /* when {
            item.action_status.equals("expired",true) -> holder.updateData(R.color.gray,"The Evaluation has been expired. You can check other evaluation",true)
            item.action_status.equals("completed",true) -> holder.updateData(R.color.vibrant_green_pressed,"The evaluation has been completed",true)
            item.action_status.equals("evaluated",true) -> holder.updateData(R.color.bt_blue,"You have already submit the evaluated amount on this evaluation. You can do again until end of the evaluation",true)
            else -> holder.updateData(R.color.bt_blue,"Evaluation is closed",false)
        }*/


        holder.mainStatus.setOnClickListener {
            if(item.btn_link_str.equals("Place your bid",true) || item.btn_link_str.contains("your bid",true))
                itemClick.onItemClick(position,"bid")
        }

        holder.addToWishListCardView.setOnClickListener {
            itemClick.onItemClick(position,"add/remove")
            when {
                item.is_wishlist -> holder.favoriteIcon.setImageResource(R.drawable.selected_star_icon)
                else -> holder.favoriteIcon.setImageResource(R.drawable.star_icon)
            }
        }

    }

    override fun getItemCount(): Int {
        return auctionDevicesList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.item_name)
        var evaluationId: TextView = view.findViewById(R.id.item_id)
        var idHeading: TextView = view.findViewById(R.id.textView16)
        var storageHeading: TextView = view.findViewById(R.id.textView45)
        var ramHeading: TextView = view.findViewById(R.id.textView47)
        var brandName: TextView = view.findViewById(R.id.item_brand)
        var modelName: TextView = view.findViewById(R.id.item_model)
        var color: TextView = view.findViewById(R.id.item_color)
        var storage: TextView = view.findViewById(R.id.item_storage)
        var ram: TextView = view.findViewById(R.id.item_ram)
        var status: TextView = view.findViewById(R.id.item_status)
        var message: TextView = view.findViewById(R.id.item_message)
//        var isNew: TextView = view.findViewById(R.id.item_is_new)
        var mainStatus: TextView = view.findViewById(R.id.item_action_status)
//        var expiryTime: TextView = view.findViewById(R.id.expiry_time)
//        var bidHeading: TextView = view.findViewById(R.id.current_highest_bid_text_view)
        var currentBid: TextView = view.findViewById(R.id.current_bid)
        var favoriteIcon: ImageView = view.findViewById(R.id.item_add_remove)
        var raiseIssueBtn: TextView = view.findViewById(R.id.raise_issue_btn)
        var chatWithSellerBtn: TextView = view.findViewById(R.id.chat_with_seller_btn)
        var chatWithSellerContainer: ConstraintLayout = view.findViewById(R.id.chatWithSellerContainer)
        var image : AppCompatImageView = view.findViewById(R.id.image)
        var addToWishListCardView : CardView = view.findViewById(R.id.item_add_remove_container)


        fun updateData(backgroundColor: Int, content: String, isVisible: Boolean) {
            mainStatus.setBackgroundColor(ContextCompat.getColor(itemView.context,backgroundColor))
            status.setBackgroundColor(ContextCompat.getColor(itemView.context,backgroundColor))
            message.text = content
            if(isVisible) {
                message.visibility = View.VISIBLE
                status.visibility = View.VISIBLE
            }else{
                message.visibility = View.GONE
                status.visibility = View.GONE
            }
        }
    }
}