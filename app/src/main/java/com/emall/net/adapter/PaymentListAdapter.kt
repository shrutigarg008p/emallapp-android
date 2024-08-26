package com.emall.net.adapter

import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.model.PaymentData
import java.util.*

internal class PaymentListAdapter(private var paymentList: ArrayList<PaymentData>) :
    RecyclerView.Adapter<PaymentListAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = paymentList[position]
        if (position == 0) {
            holder.amount.text = item.amount
            holder.paymentId.text = holder.itemView.resources.getString(R.string.id)
            holder.paymentMode.text = item.mode
            holder.productType.text = item.paymentFor
        } else {
            holder.paymentId.text = item.id.toString()
            holder.productType.text = item.paymentFor
            holder.amount.text = item.amount
            holder.paymentMode.text = item.mode.toUpperCase(Locale.getDefault())
            holder.paymentStatus.text = item.status.toUpperCase(Locale.getDefault())
        }
        if (position % 2 != 0) {
            holder.constraintLayout.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.off_white
                )
            )
        } else
            holder.constraintLayout.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var paymentId: TextView = view.findViewById(R.id.item_id)
        var productType: TextView = view.findViewById(R.id.payment_for)
        var paymentMode: TextView = view.findViewById(R.id.mode)
        var paymentStatus: TextView = view.findViewById(R.id.status)
        var amount: TextView = view.findViewById(R.id.amount)
        val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout)
    }
}