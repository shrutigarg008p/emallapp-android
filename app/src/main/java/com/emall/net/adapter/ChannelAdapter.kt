package com.emall.net.adapter

import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.ItemClick
import com.emall.net.network.model.chat.channelList.Channel

internal class ChannelAdapter(
    private var issueList: ArrayList<Channel>,
    val itemClick: ItemClick,
) :
    RecyclerView.Adapter<ChannelAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = issueList[position]

        holder.itemDate.text = item.created_at_str
        holder.participants.text = item.participants

        holder.chatBtn.setOnClickListener {
            itemClick.itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return issueList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemDate: TextView = view.findViewById(R.id.item_date)
        val participants: TextView = view.findViewById(R.id.participants_text_view)
        val chatBtn: Button = view.findViewById(R.id.chat_btn)
    }
}
