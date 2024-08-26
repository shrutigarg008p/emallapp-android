package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.orderListResponse.OrderListResponseProducts
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

class OrderChildItemAdapter(
    private var products: ArrayList<OrderListResponseProducts>,
) :
    RecyclerView.Adapter<OrderChildItemAdapter.MyViewHolder>() {
    private var itemView: View? = null

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_child_item, parent, false)
        return MyViewHolder(itemView!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = products[position]
        if (product.image.isNotEmpty()) {
            Picasso.get().load(product.image).placeholder(R.drawable.progress_animation)
                .into(holder.orderItemImage)
        } else {
            Picasso.get()
                .load("https://www.cnet.com/a/img/7xCHS3atDpS1DIu4Nd-_b9334ic=/940x0/2021/07/26/46c8b420-7494-40a5-9048-c17973358512/sunset-gold-iphone-13-render.png")
                .placeholder(R.drawable.progress_animation)
                .into(holder.orderItemImage)
        }

        holder.orderItemTitle.text = product.name
        holder.orderItemPrice.text = Utility.changedCurrencyPrice(product.price)
        if (product.size.toString() != "" && product.size.toString() != "false") {
            holder.orderItemSize.makeVisible()
            holder.sizeView.makeVisible()
            holder.orderItemSize.text =
                "${holder.itemView.context.getString(R.string.size)} ${product.size}"
        } else {
            holder.orderItemSize.makeGone()
            holder.sizeView.makeGone()
        }
        if (product.color.toString() != "" && product.color.toString() != "false") {
            holder.orderItemColor.makeVisible()
            holder.colorView.makeVisible()
            holder.orderItemColor.text =
                "${holder.itemView.context.getString(R.string.color)} ${product.color}"
        } else {
            holder.orderItemColor.makeGone()
            holder.colorView.makeGone()
        }
        if (product.quantity != 0) {
            holder.orderItemQuantity.makeVisible()
            holder.orderItemQuantity.text =
                "${holder.itemView.context.getString(R.string.qty)} ${product.quantity}"
        } else {
            holder.orderItemQuantity.makeGone()
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderItemImage: ImageView = view.findViewById(R.id.order_item_image)
        var orderItemTitle: TextView = view.findViewById(R.id.order_item_title)
        var orderItemPrice: TextView = view.findViewById(R.id.order_item_price)
        var orderItemSize: TextView = view.findViewById(R.id.order_item_size)
        var orderItemColor: TextView = view.findViewById(R.id.order_item_color)
        var orderItemQuantity: TextView = view.findViewById(R.id.order_item_quantity)
        var sizeView: View = view.findViewById(R.id.size_view)
        var colorView: View = view.findViewById(R.id.color_view)

    }
}