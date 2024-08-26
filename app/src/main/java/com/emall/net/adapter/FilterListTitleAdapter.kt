package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponseList
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class FilterListTitleAdapter(
    private var filterData: ArrayList<GetFilterDataResponseList>,
    private val storageList: ArrayList<String>,
    private val ramList: ArrayList<String>,
    private val colorList: ArrayList<String>,
    private val sizeList: ArrayList<String>,
    private val onItemClick: OnItemClick,
) :
    RecyclerView.Adapter<FilterListTitleAdapter.MyViewHolder>() {
    private var lastSelectedPosition = 0
    private var count: Int = 0

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_list_title_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filterData[position]
        holder.itemTitle.text = item.name
        count = when {
            item.code.equals("storage", true) -> {
                storageList.size
            }
            item.code.equals("ram", true) -> {
                ramList.size
            }
            item.code.equals("color", true) -> {
                colorList.size
            }
            else -> {
                sizeList.size
            }
        }
        if (count > 0) {
            holder.itemCount.makeVisible()
            holder.itemCount.text = "($count)"
        } else {
            holder.itemCount.makeGone()
        }
        when (lastSelectedPosition) {
            position -> {
                holder.itemTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.selected_color
                    )
                )
                holder.itemCount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.selected_color
                    )
                )
                holder.itemTitleContainer.setBackgroundResource(R.color.white)
            }
            else -> {
                holder.itemTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.black
                    )
                )
                holder.itemCount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.black
                    )
                )
                holder.itemTitleContainer.setBackgroundResource(R.color.light_gray)
            }
        }


        holder.itemTitleContainer.setOnClickListener {
            val previousItem = lastSelectedPosition
            lastSelectedPosition = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            onItemClick.onItemClick(position, "filterListTitle")
        }
    }

    override fun getItemCount(): Int {
        return filterData.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTitleContainer: ConstraintLayout = view.findViewById(R.id.item_title_container)
        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var itemCount: TextView = view.findViewById(R.id.item_count)
    }
}