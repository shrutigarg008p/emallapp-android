package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.orderListResponse.OrderListResponseData
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

class OrderListAdapter(
    private var requireActivity: FragmentActivity,
    private var orderList: ArrayList<OrderListResponseData>,
    val onItemClick: OnItemClick,
) :
    RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {
    private var itemView: View? = null
    private lateinit var orderChildItemAdapter: OrderChildItemAdapter
    private var expandedPosition = -1

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_item, parent, false)
        return MyViewHolder(itemView!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = orderList[position]
        val isExpanded = position == expandedPosition
        holder.orderItemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.orderArrowImage.rotation = if (isExpanded) {
            180.0f
        } else {
            0.0f
        }
        holder.itemView.isActivated = isExpanded
        holder.orderContainer.setOnClickListener(View.OnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyDataSetChanged()
        })
        holder.orderItemsRecyclerView.layoutManager = LinearLayoutManager(requireActivity)
        orderChildItemAdapter = OrderChildItemAdapter(item.products)
        holder.orderItemsRecyclerView.adapter = orderChildItemAdapter
        orderChildItemAdapter.notifyDataSetChanged()

        holder.orderId.text =
            "${holder.itemView.context.getString(R.string.order_id)} ${item.increment_id}"
        holder.orderAmount.text = Utility.changedCurrencyPrice(item.grand_total)
        holder.orderDateTime.text = item.created_at
        holder.orderStatus.text = item.status
        if (item.status_code.equals("pending", true) || item.status_code.equals("processing",
                true)
        ) {
            holder.orderStatus.setTextColor(requireActivity.resources.getColor(R.color.pending))
            holder.cancelOrderButton.makeVisible()
        } else if (item.status_code.equals("complete", true)) {
            holder.orderStatus.setTextColor(requireActivity.resources.getColor(R.color.success))
            holder.cancelOrderButton.makeGone()
        } else {
            holder.orderStatus.setTextColor(requireActivity.resources.getColor(R.color.cancelled))
            holder.cancelOrderButton.makeGone()
        }

        holder.cancelOrderButton.setOnClickListener {
            onItemClick.onItemClick(position, "cancel")
        }
        holder.reorderButton.setOnClickListener {
            onItemClick.onItemClick(position, "reorder")
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderContainer: ConstraintLayout = view.findViewById(R.id.order_container)
        var orderId: TextView = view.findViewById(R.id.order_id)
        var orderAmount: TextView = view.findViewById(R.id.order_amount)
        var orderStatus: TextView = view.findViewById(R.id.order_status)
        var orderDateTime: TextView = view.findViewById(R.id.order_date_time)
        var orderArrowImage: ImageView = view.findViewById(R.id.order_arrow_image)
        var reorderButton: AppCompatButton = view.findViewById(R.id.reorder_button)
        var cancelOrderButton: AppCompatButton = view.findViewById(R.id.cancel_order_button)
        var orderItemsRecyclerView: RecyclerView =
            view.findViewById(R.id.order_items_recycler_view)
    }
}