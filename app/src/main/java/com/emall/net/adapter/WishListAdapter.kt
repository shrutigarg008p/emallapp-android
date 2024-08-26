package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.wishlistResponse.Data
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

internal class WishListAdapter(
    private var wishListItems: ArrayList<Data>,
    val onItemClick: OnItemClick,
) :
    RecyclerView.Adapter<WishListAdapter.MyViewHolder>() {


    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.wish_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = wishListItems[position]
        holder.itemName.text = item.name
        if (item.old_price > 0) {
            holder.itemOldPrice.makeVisible()
            holder.itemOldPrice.text = Utility.changedCurrencyPrice(item.old_price)
        } else {
            holder.itemOldPrice.makeGone()
        }

        if (item.instock) {
            holder.AddToCartButton.setBackgroundResource(R.drawable.ic_charcoal_black_gradient_btn_color);
            holder.AddToCartButton.setOnClickListener {
                onItemClick.onItemClick(position, "addToCart")
            }
        } else {
            holder.AddToCartButton.setBackgroundResource(R.drawable.ic_gray_gradient);
        }
        holder.itemNewPrice.text = Utility.changedCurrencyPrice(item.price)
        Picasso.get().load(item.img).placeholder(R.drawable.progress_animation)
            .into(holder.wishlistImage)

        holder.wishlistDeleteContainer.setOnClickListener {
            onItemClick.onItemClick(position, "itemDelete")
        }

        holder.wishlistCardLayout.setOnClickListener {
            onItemClick.onItemClick(position, "itemImage")
        }
    }

    override fun getItemCount(): Int {
        return wishListItems.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var wishlistImage: ImageView = view.findViewById(R.id.wishlist_image)
        var itemName: TextView = view.findViewById(R.id.item_name)
        var itemOldPrice: TextView = view.findViewById(R.id.item_old_price)
        var itemNewPrice: TextView = view.findViewById(R.id.item_new_price)
        var wishlistDeleteContainer: ConstraintLayout =
            view.findViewById(R.id.wishlist_delete_container)
        var AddToCartButton: AppCompatButton = view.findViewById(R.id.add_to_cart_btn)
        var wishlistCardLayout: CardView = view.findViewById(R.id.wishlist_card_layout)
    }
}