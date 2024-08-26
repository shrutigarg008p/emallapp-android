package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.evaluationWishList.DataX
import com.squareup.picasso.Picasso

internal class EvaluationWishListAdapter(
    private var wishListItems: ArrayList<DataX>,
    val onItemClick: ItemClick,
) :
    RecyclerView.Adapter<EvaluationWishListAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.wishlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = wishListItems[position]
        holder.itemName.text = item.product.name
        Picasso.get().load(item.product.base_image).into(holder.wishListImage)
        holder.deleteItem.setOnClickListener {
            onItemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return wishListItems.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var wishListImage: ImageView = view.findViewById(R.id.wish_list_image)
        var itemName: TextView = view.findViewById(R.id.item_name)
        var deleteItem: ConstraintLayout = view.findViewById(R.id.wish_list_delete_container)
    }
}