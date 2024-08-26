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
import com.emall.net.network.model.evaluationAcceptDetail.Evaluation
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.google.android.material.button.MaterialButton
import com.google.gson.internal.LinkedTreeMap
import org.json.JSONObject

internal class EvaluationAcceptAdapter(
    private var evaluationsList: ArrayList<Evaluation>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<EvaluationAcceptAdapter.MyViewHolder>() {

    private var actionType: String? = ""
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bidding_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = evaluationsList[position]
/*
        if(item.re_evaluation_status[0].message.isNotEmpty())
            holder.biddingDate.text = item.re_evaluation_status[0].message.toString()
        else
*/
        holder.biddingDate.text = item.selected_at_str
        holder.bidPrice.text = item.amount
        holder.personName.text = "${item.selected_by.first_name} ${item.selected_by.last_name}"
        if(PreferenceHelper.readBoolean("evaluationDate")!!){
            holder.acceptBidButton.makeVisible()
            holder.biddingDate.makeVisible()
        }else{
            holder.acceptBidButton.makeGone()
            holder.biddingDate.makeGone()
        }

        actionType = item.action.message

        if(actionType.equals("Accept Evaluation",true)){
            holder.acceptBidButton.text = "Accept"
        }else{
            holder.acceptBidButton.text = item.action.action
        }
        holder.processShipment.text = actionType

        if(item.action.action.isEmpty()) {
            holder.acceptBidButton.makeGone()
            holder.rejectBidBtn.makeGone()
        }else {
            holder.acceptBidButton.makeVisible()
            holder.acceptBidButton.makeVisible()
            holder.rejectBidBtn.makeVisible()
        }

        if(item.re_evaluation_status.toString().isNotEmpty()) {
            when(item.re_evaluation_status){
                is LinkedTreeMap<*, *> -> {
                    val object1 = item.re_evaluation_status as LinkedTreeMap<String,String>
                    holder.acceptBidButton.text = "Accept"
                    holder.rejectBidBtn.makeVisible()
                    holder.acceptBidButton.makeVisible()
                    actionType = "reEvaluate"

                    val messageArray = object1["message"] as ArrayList<String>
                    if(messageArray[0].equals("You accepted the re-evaluation",true)){
                        holder.rejectBidBtn.makeGone()
                        holder.acceptBidButton.makeGone()
                        holder.biddingDate.makeVisible()
                        holder.biddingDate.text  = messageArray[0]
                    }
                }else ->{
                holder.rejectBidBtn.makeGone()
                }
            }
        }else{

        }

        holder.acceptBidButton.setOnClickListener {
            when{
                actionType!!.equals("Accept Evaluation", true) ->
                    itemClick.onItemClick(position, "Accept Evaluation")
                actionType!!.equals("Waiting For Acknowledge",true) -> {

                }
                actionType!!.equals("Update Bank Details",true) ->
                    itemClick.onItemClick(position,"add bank details")
                actionType!!.equals("Proceed To Pay",true)->
                    itemClick.onItemClick(position,"stc payment")
                actionType!!.equals("Process For Shipment",true) ->
                    itemClick.onItemClick(position,"add address")
                actionType!!.equals("reEvaluate",true) ->{
                    itemClick.onItemClick(position, "Accept Re-Evaluation")
                }
            }
        }

        holder.rejectBidBtn.setOnClickListener{
            itemClick.onItemClick(position,"reject")
        }
    }

    override fun getItemCount(): Int {
        return evaluationsList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var constraintLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout)
        var biddingDate: TextView = view.findViewById(R.id.auction_date)
        var personName: TextView = view.findViewById(R.id.person_name)
        var bidPrice: TextView = view.findViewById(R.id.bid_price)
        var processShipment: TextView = view.findViewById(R.id.process_shipment_text_view)
        var acceptBidButton: MaterialButton = view.findViewById(R.id.accept_bid_btn)
        var rejectBidBtn: MaterialButton = view.findViewById(R.id.reject_bid_btn)
    }
}