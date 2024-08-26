package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.model.getIssuesList.IssueListData
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class IssueAdapter(
    private var issueList: ArrayList<IssueListData>,
    val itemClick: OnItemClick,
) :
    RecyclerView.Adapter<IssueAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.resolution_center_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = issueList[position]
        holder.issueCount.text = """${(position + 1)}."""
        holder.issueStatus.text = item.status_text
        holder.chatWithAdmin.text = item.actions.admin_chat.text
        holder.chatWithSeller.text = item.actions.user_chat.text
        holder.issueNumber.text = item.number
        holder.itemId.text = item.evaluation_id.toString()
        holder.itemType.text = item.type_text
        holder.itemRaisedAt.text = item.raised_at
        holder.itemRaisedBy.text = item.raised_by

        if (item.status_text.contains("Close", true)) {
            holder.closeBtn.makeGone()
            holder.chatWithSeller.makeGone()
            holder.chatWithAdmin.makeGone()
        } else {
            holder.closeBtn.makeVisible()
            holder.chatWithSeller.makeVisible()
            holder.chatWithAdmin.makeVisible()
        }

        holder.itemView.setOnClickListener {
            itemClick.onItemClick(position, "issue detail")
        }

        holder.chatWithAdmin.setOnClickListener {
            itemClick.onItemClick(position, "seller chat")
        }

        holder.chatWithAdmin.setOnClickListener {
            itemClick.onItemClick(position, "admin chat")
        }

        holder.closeBtn.setOnClickListener {
            itemClick.onItemClick(position, "close issue")
        }
    }

    override fun getItemCount(): Int {
        return issueList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val issueCount: AppCompatTextView = view.findViewById(R.id.issue_count)
        val issueStatus: Button = view.findViewById(R.id.issue_status)
        val chatWithAdmin: Button = view.findViewById(R.id.chat_with_admin_btn)
        val chatWithSeller: Button = view.findViewById(R.id.chat_with_seller_btn)
        val issueNumber: TextView = view.findViewById(R.id.issue_id_number)
        val itemId: TextView = view.findViewById(R.id.item_id)
        val itemType: TextView = view.findViewById(R.id.item_type)
        val itemRaisedBy: TextView = view.findViewById(R.id.item_raised_by)
        val itemRaisedAt: TextView = view.findViewById(R.id.item_raised_at)
        val closeBtn: ConstraintLayout = view.findViewById(R.id.close_btn_container)
    }
}
