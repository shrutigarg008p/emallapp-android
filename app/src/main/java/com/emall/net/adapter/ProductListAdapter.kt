package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.ProductListData
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

class ProductListAdapter(
    mydataset: List<ProductListData>,
    onItemClick: OnItemClick,
    activity: FragmentActivity,
) :
    RecyclerView.Adapter<ProductListAdapter.MyViewHolder?>() {
    var mydataset: List<ProductListData> = mydataset
    val onItemClick: OnItemClick = onItemClick
    val activity: FragmentActivity = activity

    var itemView: View? = null
    var selctedwishlist: ArrayList<Int> = ArrayList()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item_english, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (mydataset[position].old_price > 0) {
            holder.oldPrice.makeVisible()
        } else {
            holder.oldPrice.makeGone()
        }
        holder.oldPrice.text = Utility.changedCurrencyPrice(mydataset[position].old_price)
        holder.newPrice.text = Utility.changedCurrencyPrice(mydataset[position].price)
        holder.productname.text = mydataset[position].name.toString()
        try {
            Glide.with(holder.iv.context)
                .load(mydataset[position].img)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20))).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.progress_animation)
                .placeholder(R.drawable.progress_animation).override(1000, 1500)
                .into(holder.iv)
        } catch (e: Exception) {
        }

        holder.singleproductclick.setOnClickListener {
            onItemClick.onItemClick(position, "productDetail")
        }

        when (activity) {
            is SellerActivity -> {
                if ((activity as SellerActivity).wishListItemsId.contains(mydataset[position].id.toString())) {
                    addedToWishList(holder, position)
                } else {
                    notAddedToWishList(holder, position)
                }
            }
            else -> {
                if ((activity as BuyerActivity).wishListItemsId.contains(mydataset[position].id.toString())) {
                    addedToWishList(holder, position)
                } else {
                    notAddedToWishList(holder, position)
                }
            }
        }
    }

    private fun notAddedToWishList(holder: MyViewHolder, position: Int) {
        holder.heartbtn.setImageResource(R.drawable.ic_heart_outline)
        holder.productListAddToWishlist.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "add")
        })
    }

    private fun addedToWishList(holder: MyViewHolder, position: Int) {
        holder.heartbtn.setImageResource(R.drawable.ic_heart)
        holder.productListAddToWishlist.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "remove")
        })
    }

    override fun onViewRecycled(@NonNull holder: MyViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.iv.context).clear(holder.iv)
    }

    override fun getItemCount(): Int {
        return mydataset.size
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView? = null
        var year: TextView? = null
        var genre: TextView? = null
        var productname: TextView
        var oldPrice: TextView
        var newPrice: TextView
        var iv: ImageView
        var heartbtn: ImageView
        var singleproductclick: LinearLayout
        var productListAddToWishlist: RelativeLayout

        init {
            iv = view.findViewById(R.id.product_list_image)
            productname = view.findViewById(R.id.productname)
            oldPrice = view.findViewById(R.id.oldprice)
            newPrice = view.findViewById(R.id.newprice)
            singleproductclick = view.findViewById(R.id.singleproduct)
            heartbtn = view.findViewById(R.id.heartbtn)
            productListAddToWishlist = view.findViewById(R.id.productListAddToWishlist)
        }
    }

}