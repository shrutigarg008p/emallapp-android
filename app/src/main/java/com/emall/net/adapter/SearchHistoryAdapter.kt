package com.emall.net.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

class SearchHistoryAdapter(
    private var searchHistoryList: ArrayList<String>,
    val onItemClick: OnItemClick) :
    RecyclerView.Adapter<SearchHistoryAdapter.MyViewHolder>() {
    private var itemView: View? = null

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_history_item_list, parent, false)
        return MyViewHolder(itemView!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = searchHistoryList[position]
        holder.searchHistoryText.text = item
        holder.searchHistoryLayout.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "searchHistoryText")
        })
        if (position < searchHistoryList.size - 1) {
            holder.searchHistoryDivider.makeVisible()
        } else {
            holder.searchHistoryDivider.makeGone()
        }
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    fun filterList(filteredList: ArrayList<String>) {
        searchHistoryList = filteredList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var searchHistoryLayout: ConstraintLayout = view.findViewById(R.id.search_history_layout)
        var searchHistoryText: TextView = view.findViewById(R.id.search_history_text)
        var searchHistoryDivider: View = view.findViewById(R.id.search_history_divider)
    }


}