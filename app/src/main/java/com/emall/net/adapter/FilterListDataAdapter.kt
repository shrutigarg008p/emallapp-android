package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponseListData

internal class FilterListDataAdapter(
    private var filterListData: ArrayList<GetFilterDataResponseListData>,
    private val storageList: ArrayList<String>,
    private val ramList: ArrayList<String>,
    private val colorList: ArrayList<String>,
    private val sizeList: ArrayList<String>,
    private val onItemClick: OnItemClick,
) :
    RecyclerView.Adapter<FilterListDataAdapter.MyViewHolder>() {
    private var lastSelectedPosition = 0

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_list_data_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filterListData[position]
        holder.itemTitle.text = item.display

        when {
            storageList.contains(item.value) -> {
                itemSelected(holder)
            }
            ramList.contains(item.value) -> {
                itemSelected(holder)
            }
            colorList.contains(item.value) -> {
                itemSelected(holder)
            }
            sizeList.contains(item.value) -> {
                itemSelected(holder)
            }
            else -> {
                itemUnselected(holder)
            }
        }

        holder.itemContainer.setOnClickListener {
            onItemClick.onItemClick(position, "filterListData")
        }
    }

    private fun itemSelected(holder: MyViewHolder) {
        holder.itemTitle.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.selected_color
            )
        )
        holder.checkboxImage.setImageResource(R.drawable.checked_square)
    }


    private fun itemUnselected(holder: FilterListDataAdapter.MyViewHolder) {
        holder.itemTitle.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.black
            )
        )
        holder.checkboxImage.setImageResource(R.drawable.unchecked_square)
    }

    override fun getItemCount(): Int {
        return filterListData.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemContainer: ConstraintLayout = view.findViewById(R.id.item_container)
        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var checkboxImage: AppCompatImageView = view.findViewById(R.id.checkbox_image)
    }
}