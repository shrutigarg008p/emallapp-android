package com.emall.net.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.searchResponse.SearchResponseData
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.squareup.picasso.Picasso

class SearchAdapter(
    private var searchResponseData: ArrayList<SearchResponseData>,
    val onItemClick: OnItemClick,
    activity: FragmentActivity,
) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {
    private var itemView: View? = null
    val activity: FragmentActivity = activity

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_list, parent, false)
        return MyViewHolder(itemView!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = searchResponseData[position]
        holder.searchTitle.text = item.name
        holder.searchNewPrice.text = Utility.changedCurrencyPrice(item.price)
        if (item.old_price > 0) {
            holder.searchOldPrice.makeVisible()
            holder.searchOldPrice.text = Utility.changedCurrencyPrice(item.old_price)
        } else {
            holder.searchOldPrice.makeGone()
        }
        Picasso.get().load(item.img).placeholder(R.drawable.progress_animation)
            .into(holder.searchImage)

        holder.searchItemContainer.setOnClickListener {
            onItemClick.onItemClick(position, "productDetail")
        }
        when (activity) {
            is SellerActivity -> {
                if ((activity as SellerActivity).wishListItemsId.contains(item.id)) {
                    addedToWishList(holder, position)
                } else {
                    notAddedToWishList(holder, position)
                }
            }
            else -> {
                if ((activity as BuyerActivity).wishListItemsId.contains(item.id)) {
                    addedToWishList(holder, position)
                } else {
                    notAddedToWishList(holder, position)
                }
            }
        }
    }
    private fun notAddedToWishList(holder: MyViewHolder, position: Int) {
        holder.itemAddRemove.setImageResource(R.drawable.ic_heart_outline)
        holder.itemAddRemoveContainer.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "add")
        })
    }

    private fun addedToWishList(holder: MyViewHolder, position: Int) {
        holder.itemAddRemove.setImageResource(R.drawable.ic_heart)
        holder.itemAddRemoveContainer.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "remove")
        })
    }


    override fun getItemCount(): Int {
        return searchResponseData.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemAddRemoveContainer: CardView = view.findViewById(R.id.item_add_remove_container)
        var searchItemContainer: CardView = view.findViewById(R.id.search_item_container)
        var itemAddRemove: ImageView = view.findViewById(R.id.item_add_remove)
        var searchImage: ImageView = view.findViewById(R.id.search_image)
        var searchTitle: TextView = view.findViewById(R.id.search_title)
        var searchNewPrice: TextView = view.findViewById(R.id.search_new_price)
        var searchOldPrice: TextView = view.findViewById(R.id.search_old_price)
    }
}