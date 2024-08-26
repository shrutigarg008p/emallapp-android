package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.auctionDevicesWon.AuctionDevicesWon
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class AuctionDevicesWonAdapter(
    private var auctionDevicesWonList: ArrayList<AuctionDevicesWon>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<AuctionDevicesWonAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.auction_devices_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = auctionDevicesWonList[position]
        holder.name.text = item.product.device_name
        holder.evaluationId.text = item.id.toString()
        holder.brandName.text = item.product.brand
        holder.modelName.text = item.product.model
        holder.color.text = item.product.variant
        holder.idHeading.text = holder.itemView.resources.getString(R.string.auction_id)
        holder.storage.makeGone()
        holder.storageHeading.makeGone()
        holder.ram.makeGone()
        holder.ramHeading.makeGone()
//        holder.isNew.makeGone()
        holder.status.makeGone()
        holder.raiseIssueBtn.text = item.raise_issue.text
        holder.currentBid.text = item.extra_notes.note1
        holder.mainStatus.text = item.action_button.text
//        holder.expiryTime.text = item.ends_at_str

        if (item.action_button.text.equals("Proceed To Payment", true)) {
            holder.raiseIssueBtn.makeVisible()
            holder.chatWithSellerContainer.makeVisible()
//            holder.expiryTime.makeGone()
        } else {
            holder.raiseIssueBtn.makeGone()
            holder.chatWithSellerContainer.makeGone()
//            holder.expiryTime.makeGone()
        }

        holder.raiseIssueBtn.setOnClickListener {
            when {
                holder.raiseIssueBtn.text.toString().equals("Issue Open",true) -> {
                    itemClick.onItemClick(position,"view issue")
                }
                holder.raiseIssueBtn.text.toString().equals("Raise An Issue",true) -> {
                    itemClick.onItemClick(position, "raiseIssue")
                }
            }
        }

        holder.favoriteIcon.setOnClickListener {
            when (holder.favoriteIcon.drawable.constantState) {
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_outline_favorite_border_24
                )?.constantState -> holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
                else -> holder.favoriteIcon.setImageResource(R.drawable.ic_outline_favorite_border_24)
            }
        }

        holder.mainStatus.setOnClickListener {
            when {
                item.action_button.text.equals("Acknowledge Seller",true) -> itemClick.onItemClick(position,"address")
                item.action_button.text.equals("Proceed to payment",true) -> itemClick.onItemClick(position,"offlinePayment")
            }
        }
    }

    override fun getItemCount(): Int {
        return auctionDevicesWonList.size
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
        var raiseIssueBtn: TextView = view.findViewById(R.id.raise_issue_btn)
        var chatWithSellerBtn: TextView = view.findViewById(R.id.chat_with_seller_btn)
//        var expiryTime: TextView = view.findViewById(R.id.expiry_time)
//        var bidHeading: TextView = view.findViewById(R.id.current_highest_bid_text_view)
        var currentBid: TextView = view.findViewById(R.id.current_bid)
        var favoriteIcon: ImageView = view.findViewById(R.id.item_add_remove)
        var chatWithSellerContainer: ConstraintLayout = view.findViewById(R.id.chatWithSellerContainer)

        fun updateData(backgroundColor: Int, content: String, isVisible: Boolean) {
            mainStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
            status.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
            message.text = content
            if (isVisible) {
                message.visibility = View.VISIBLE
                status.visibility = View.VISIBLE
            } else {
                message.visibility = View.GONE
                status.visibility = View.GONE
            }
        }
    }
}
