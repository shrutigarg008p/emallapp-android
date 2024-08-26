package com.emall.net.adapter

import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.categoryList.Data
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class CategoryListAdapter(
    private var itemList: List<Data>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>() {
    private var selectedItem: Int
    private var lastClickedPosition: Int

    init {
        selectedItem = 0
        lastClickedPosition = -1
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.categories_type_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.type.text = item.name
        when (selectedItem) {
            position -> {
                holder.type.setTextColor(ContextCompat.getColor(holder.itemView.context,
                    R.color.black))
                holder.divider.makeVisible()
            }
            else -> {
                holder.type.setTextColor(ContextCompat.getColor(holder.itemView.context,
                    R.color.unselected_color))
                holder.divider.makeGone()
            }
        }
        holder.itemView.setOnClickListener {
            val previousItem = selectedItem
            selectedItem = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var type: TextView = view.findViewById(R.id.type)
        var divider: View = view.findViewById(R.id.divider)
        var rootLayout: ConstraintLayout = view.findViewById(R.id.root_layout)
    }
}