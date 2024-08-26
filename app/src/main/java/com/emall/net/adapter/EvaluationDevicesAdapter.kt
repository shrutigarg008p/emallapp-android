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
import com.emall.net.network.model.evaluationDevices.DataX
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

internal class EvaluationDevicesAdapter(
    private var itemList: ArrayList<DataX>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<EvaluationDevicesAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.auction_devices_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.product.device_name
        holder.evaluationId.text = item.id.toString()
        holder.brandName.text = item.product.brand
        holder.modelName.text = item.product.model
        holder.color.text = item.product.variant
        holder.storage.makeGone()
        holder.storageHeading.makeGone()
        holder.ram.makeGone()
        holder.ramHeading.makeGone()
        holder.status.text = item.action_status
        holder.currentBidMessage.text = item.action_status_msg

        when {
            item.is_wishlist -> holder.favoriteIcon.setImageResource(R.drawable.selected_star_icon)
            else -> holder.favoriteIcon.setImageResource(R.drawable.star_icon)
        }


        if (item.product.base_image.isNotEmpty())
            Picasso.get().load(item.product.base_image).into(holder.image)

        if (item.action_status.equals("expired", true) || item.action_status.equals("closed", true)
            || item.action_status.equals(
                "completed",
                true
            ) || item.action_status.equals("Time Over", true)
        ) {
            holder.message.text = item.ends_at_str
            holder.raiseIssueBtn.makeGone()
            holder.chatWithSellerContainer.makeGone()
            holder.reEvaluateBtn.makeGone()
        } else if(item.action_status.equals("evaluate",true) || item.action_status.equals("evaluated",true) ) {
            holder.message.text = item.ends_at_str
            holder.raiseIssueBtn.makeGone()
            holder.chatWithSellerContainer.makeGone()
            holder.reEvaluateBtn.makeGone()
        }else{
            holder.message.text = item.status
            holder.raiseIssueBtn.makeVisible()
            holder.chatWithSellerContainer.makeVisible()
            holder.reEvaluateBtn.makeVisible()
        }
        holder.addToWishListCardView.setOnClickListener {
            itemClick.onItemClick(position,"add/remove")
            when {
                item.is_wishlist -> holder.favoriteIcon.setImageResource(R.drawable.selected_star_icon)
                else -> holder.favoriteIcon.setImageResource(R.drawable.star_icon)
            }
        }

        holder.status.setOnClickListener {
            if (item.action_status.equals(
                    "Evaluate",
                    true
                ) || item.action_status.equals("Evaluated", true)
            ) {
                itemClick.onItemClick(position,"evaluate")
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.item_name)
        var evaluationId: TextView = view.findViewById(R.id.item_id)
        var IdHeading: TextView = view.findViewById(R.id.textView16)
        var storageHeading: TextView = view.findViewById(R.id.textView45)
        var ramHeading: TextView = view.findViewById(R.id.textView47)
        var brandName: TextView = view.findViewById(R.id.item_brand)
        var modelName: TextView = view.findViewById(R.id.item_model)
        var color: TextView = view.findViewById(R.id.item_color)
        var storage: TextView = view.findViewById(R.id.item_storage)
        var ram: TextView = view.findViewById(R.id.item_ram)
        var status: TextView = view.findViewById(R.id.item_status)
        var message: TextView = view.findViewById(R.id.item_message)
        var reEvaluateBtn: TextView = view.findViewById(R.id.item_action_status)
        var currentBidMessage: TextView = view.findViewById(R.id.current_bid)
        var favoriteIcon: ImageView = view.findViewById(R.id.item_add_remove)
        var raiseIssueBtn: TextView = view.findViewById(R.id.raise_issue_btn)
        var chatWithSellerBtn: TextView = view.findViewById(R.id.chat_with_seller_btn)
        var chatWithSellerContainer: ConstraintLayout =
            view.findViewById(R.id.chatWithSellerContainer)
        var image: AppCompatImageView = view.findViewById(R.id.image)
        var addToWishListCardView: CardView = view.findViewById(R.id.item_add_remove_container)
    }
}