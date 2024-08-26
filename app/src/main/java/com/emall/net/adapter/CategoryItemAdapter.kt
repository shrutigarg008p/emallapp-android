package com.emall.net.adapter

import android.view.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible


internal class CategoryItemAdapter(
    private var categoryItemList: ArrayList<String>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<CategoryItemAdapter.MyViewHolder>() {
    private var selectedItem: Int = 0

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.drawer_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = categoryItemList[position]
        holder.title.text = item
        when (position) {
            categoryItemList.size -> holder.view.makeGone()
            else -> holder.view.makeVisible()
        }

        when (selectedItem) {
            position -> {
                holder.title.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.selected_color
                    )
                )
                holder.image.setImageResource(R.drawable.tick_icon_colored)
            }
            else -> {
                holder.title.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.black
                    )
                )
                holder.image.setImageResource(R.drawable.tick_icon_grey)
            }
        }
        holder.itemView.setOnClickListener {
            val previousItem = selectedItem
            selectedItem = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
            itemClick.onItemClick(position, item)
        }
    }

    override fun getItemCount(): Int {
        return categoryItemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: AppCompatTextView = view.findViewById(R.id.drawer_item)
        var image: AppCompatImageView = view.findViewById(R.id.image)
        var view: View = view.findViewById(R.id.view)
    }
}