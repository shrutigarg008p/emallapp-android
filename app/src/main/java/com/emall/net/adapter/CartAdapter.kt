package com.emall.net.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.getCartItems.Item
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso


class CartAdapter(
    private var cartItemList: ArrayList<Item>,
    val onItemClick: OnItemClick,
) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
    private var itemView: View? = null

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_list, parent, false)
        return MyViewHolder(itemView!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = cartItemList[position]
        itemView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        holder.itemName.text = item.name
        holder.itemPrice.text = Utility.changedCurrencyPrice(item.price)
        holder.itemCount.text = item.qty.toString()
        Picasso.get().load(item.img).placeholder(R.drawable.progress_animation).into(holder.image)
        if (item.instock == 0) {
            holder.outOfStockText.makeVisible()
        }
        holder.itemCountIncrement.setOnClickListener {
            var count = item.qty
            count++
            item.qty = count
            onItemClick.onItemClick(position, count.toString())
        }
        holder.itemCountDecrement.setOnClickListener {
            var count = item.qty
            if (count > 1) {
                count--
                item.qty = count
                onItemClick.onItemClick(position, count.toString())
            }
        }
        holder.itemDeleteContainer.setOnClickListener {
            onItemClick.onItemClick(position, "itemDelete")
        }
        holder.cartItemLayout.setOnClickListener {
            onItemClick.onItemClick(position, "itemImage")
        }
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: AppCompatTextView = view.findViewById(R.id.title)
        var itemPrice: AppCompatTextView = view.findViewById(R.id.price)
        var itemColor: AppCompatTextView = view.findViewById(R.id.color_name)
        var itemCount: AppCompatTextView = view.findViewById(R.id.count)
        var itemCountIncrement: AppCompatTextView = view.findViewById(R.id.count_increment)
        var itemCountDecrement: AppCompatTextView = view.findViewById(R.id.count_decrement)
        var outOfStockText: AppCompatTextView = view.findViewById(R.id.out_of_stock_text)
        var image: AppCompatImageView = view.findViewById(R.id.image)
        var itemDeleteContainer: ConstraintLayout = view.findViewById(R.id.item_delete_container)
        var cartItemLayout: CardView = view.findViewById(R.id.cart_item_layout)
    }
}