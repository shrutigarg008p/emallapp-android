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
import com.emall.net.network.model.evaluationDevicesWon.EvaluationDevicesWon
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class EvaluationDevicesWonAdapter(
    private var evaluationDevicesWonList: ArrayList<EvaluationDevicesWon>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<EvaluationDevicesWonAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.auction_devices_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = evaluationDevicesWonList[position]
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
        holder.currentBid.text = item.extra_notes.note1
        holder.actionStatus.text = item.action_button.text
        holder.raiseIssueBtn.text = item.raise_issue.text
        holder.message.text = item.re_evaluate.text
        holder.message.makeVisible()

        if(item.re_evaluate.text.equals("Return Device",true)){
            holder.actionStatus.text = item.re_evaluate.text
            holder.message.makeGone()
        }

        if (item.action_button.text.equals("Proceed To Pay", true) || item.re_evaluate.text.equals("Re-Evaluated",true)) {
            holder.raiseIssueBtn.makeVisible()
            holder.chatWithSellerContainer.makeVisible()
        } else {
            holder.raiseIssueBtn.makeGone()
            holder.chatWithSellerContainer.makeGone()
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
                )?.constantState,
                -> holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
                else -> holder.favoriteIcon.setImageResource(R.drawable.ic_outline_favorite_border_24)
            }
        }


        holder.actionStatus.setOnClickListener {
            when {
                holder.actionStatus.text.toString().equals("Acknowledge Seller", true) -> itemClick.onItemClick(
                    position,
                    "address")
                holder.actionStatus.text.toString().equals("Proceed to pay", true) -> itemClick.onItemClick(
                    position,
                    "offlinePayment")
                holder.actionStatus.text.toString().equals("Return Device",true) ->itemClick.onItemClick(
                    position,"return device"
                )
            }
        }
        holder.message.setOnClickListener {
            when{
                item.re_evaluate.text.equals("Re-Evaluate",true) ->
                    itemClick.onItemClick(position,"re_evaluate")
            }
        }
    }

    override fun getItemCount(): Int {
        return evaluationDevicesWonList.size
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

        var actionStatus: TextView = view.findViewById(R.id.item_action_status)
        var raiseIssueBtn: TextView = view.findViewById(R.id.raise_issue_btn)
        var chatWithSellerBtn: TextView = view.findViewById(R.id.chat_with_seller_btn)
        var chatWithSellerContainer: ConstraintLayout = view.findViewById(R.id.chatWithSellerContainer)
        var currentBid: TextView = view.findViewById(R.id.current_bid)
        var favoriteIcon: ImageView = view.findViewById(R.id.item_add_remove)
    }
}
