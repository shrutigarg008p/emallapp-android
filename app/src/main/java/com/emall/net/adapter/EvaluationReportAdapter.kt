package com.emall.net.adapter

import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.model.ReportData
import java.util.*

internal class EvaluationReportAdapter(private var reportList: ArrayList<ReportData>) :
    RecyclerView.Adapter<EvaluationReportAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = reportList[position]
        if (position == 0) {
            holder.deviceId.text = holder.itemView.resources.getString(R.string.id)
            holder.deviceDate.text = item.date
            holder.deviceName.text = item.name
            holder.deviceStatus.text = item.status
        } else {
            holder.deviceId.text = item.id.toString()
            holder.deviceName.text = item.name
            holder.deviceDate.text = item.date
            holder.deviceStatus.text = item.status
        }
        if (position % 2 == 0) {
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
        return reportList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var deviceId: TextView = view.findViewById(R.id.item_id)
        var deviceDate: TextView = view.findViewById(R.id.date)
        var deviceName: TextView = view.findViewById(R.id.device)
        var deviceStatus: TextView = view.findViewById(R.id.status)
        val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout)
    }
}