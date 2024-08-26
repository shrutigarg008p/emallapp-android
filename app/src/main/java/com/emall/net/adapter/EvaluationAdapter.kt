package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.evaluationDetail.DataX
import com.google.android.material.button.MaterialButton

internal class EvaluationAdapter(
    private var evaluationItemList: ArrayList<DataX>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<EvaluationAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.evaluation_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = evaluationItemList[position]
        holder.time.text = item.ends_at_str
        holder.status.text = item.status
        if(item.status.equals("open",true)){
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.vibrant_green_pressed))
            holder.statusDot.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.vibrant_green_pressed))
        }else
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
        holder.statusDot.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
        holder.id.text = item.id.toString()
        holder.date.text = item.starts_at
        holder.viewOfferButton.setOnClickListener {
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return evaluationItemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView = view.findViewById(R.id.evaluation_date)
        var time: TextView = view.findViewById(R.id.evaluation_time)
        var status: TextView = view.findViewById(R.id.status)
        var statusDot: TextView = view.findViewById(R.id.statusDot)
        var id: TextView = view.findViewById(R.id.evaluation_id)
        var viewOfferButton: MaterialButton = view.findViewById(R.id.view_offer_btn)
    }
}
