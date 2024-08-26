package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.ItemData
import com.squareup.picasso.Picasso

internal class ProductSellerAdapter(
    private var itemList: List<ItemData>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<ProductSellerAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_seller_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList[position]
        holder.rating.text = data.rating.toString()
        holder.discountedPrice.text = data.discountedPrice
        holder.price.text = data.price
        holder.itemType.text = data.itemType
        holder.itemName.text = data.itemName
        Picasso.get().load(data.image).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position, HomeAdapter::class.java.simpleName)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemType: TextView = view.findViewById(R.id.type)
        var itemName: TextView = view.findViewById(R.id.name)
        var rating: com.emall.net.customview.MulishRegular = view.findViewById(R.id.rating_text)
        var discountedPrice: com.emall.net.customview.MulishRegular =
            view.findViewById(R.id.discounted_price)
        var price: com.emall.net.customview.MulishRegular = view.findViewById(R.id.price)
        var image: AppCompatImageView = view.findViewById(R.id.item_image)

    }
}