package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.model.SimilarProductsItem
import com.squareup.picasso.Picasso

internal class SimilarProductsAdapter(
    private var itemList: List<SimilarProductsItem>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<SimilarProductsAdapter.MyViewHolder>() {
    private var isAddedToWishlist = false;

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.similar_products_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        Picasso.get().load(item.image).placeholder(R.drawable.progress_animation).into(holder.image)
        holder.title.text = item.title
        holder.price.text = item.price
        holder.image.setOnClickListener {
        }
        holder.itemAddRemove.setOnClickListener {
            when (isAddedToWishlist) {
                false -> {
                    isAddedToWishlist = true
                    holder.itemAddRemove.setImageResource(R.drawable.ic_heart_outline)
                }
                else -> {
                    isAddedToWishlist = false
                    holder.itemAddRemove.setImageResource(R.drawable.ic_heart)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val itemAddRemove: ImageView = view.findViewById(R.id.item_add_remove)
        val price: TextView = view.findViewById(R.id.price)
        val title: TextView = view.findViewById(R.id.title)
    }
}