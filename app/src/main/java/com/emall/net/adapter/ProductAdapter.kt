package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.products.ProductList
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

internal class ProductAdapter(
    private var productItemList: ArrayList<ProductList>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = productItemList[position]
        holder.itemName.text = item.name
        holder.productId.text = item.id.toString()
        holder.submittedFor.text = item.submited_for
        holder.time.text = item.created_at_str
        if(item.base_image.isNotEmpty()) {
            Picasso.get().load(item.base_image).into(holder.image)
        }
        holder.viewBtn.setOnClickListener {
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return productItemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: AppCompatTextView = view.findViewById(R.id.item_name)
        var productId: AppCompatTextView = view.findViewById(R.id.product_id)
        var submittedFor: AppCompatTextView = view.findViewById(R.id.submitted_for)
        var time: AppCompatTextView = view.findViewById(R.id.time)
        var image: AppCompatImageView = view.findViewById(R.id.bid_image)
        var viewBtn: MaterialButton = view.findViewById(R.id.view_btn)
    }
}