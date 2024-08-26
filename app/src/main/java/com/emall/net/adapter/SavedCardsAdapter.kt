package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.model.CardItemDetails

internal class SavedCardsAdapter(
    private var itemList: List<CardItemDetails>, val itemClick: ItemClick
) :
    RecyclerView.Adapter<SavedCardsAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_cards_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.cardName
        holder.number.text = item.cardNumber
        holder.date.text = item.expiryDate
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.owner_name)
        var number: TextView = view.findViewById(R.id.card_number)
        var date: TextView = view.findViewById(R.id.exp_date)
    }
}