package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.evaluationOrders.DataX
import com.squareup.picasso.Picasso

internal class EvaluationOrderAdapter(
    private var evaluationOrderAdapter: ArrayList<DataX>
) :
    RecyclerView.Adapter<EvaluationOrderAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recom_order_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = evaluationOrderAdapter[position]

        when {
            item.evaluation.product.base_image.isNotEmpty() -> Picasso.get().load(item.evaluation.product.base_image).into(holder.image)
        }
        holder.status.text = item.status
        holder.date.text = item.created_at_str
        holder.orderId.text = item.id.toString()
        holder.name.text = item.evaluation.product.device_name
        holder.evaluationId.text = item.evaluation.id.toString()
        holder.brandName.text = item.evaluation.product.brand
        holder.modelName.text = item.evaluation.product.model
        holder.color.text = item.evaluation.product.variant
        holder.idHeading.text = holder.itemView.resources.getString(R.string.evaluation_id)
    }

    override fun getItemCount(): Int {
        return evaluationOrderAdapter.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.image)
        var status: TextView = view.findViewById(R.id.item_status)
        var date: TextView = view.findViewById(R.id.item_message)
        var orderId: TextView = view.findViewById(R.id.order_id)
        var name: TextView = view.findViewById(R.id.item_name)
        var evaluationId: TextView = view.findViewById(R.id.item_id)
        var idHeading: TextView = view.findViewById(R.id.textView16)
        var brandName: TextView = view.findViewById(R.id.item_brand)
        var modelName: TextView = view.findViewById(R.id.item_model)
        var color: TextView = view.findViewById(R.id.item_color)
    }
}