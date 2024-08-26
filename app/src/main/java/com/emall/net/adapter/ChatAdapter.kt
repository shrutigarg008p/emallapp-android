package com.emall.net.adapter

import android.view.*
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.network.model.chat.viewChannelMessages.Message
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class ChatAdapter(
    private var messageList: ArrayList<Message>
) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = messageList[position]
        when {
            item.message.is_mine -> {
                holder.senderLayout.makeVisible()
                holder.receiverLayout.makeGone()
                holder.senderName.text = item.user.name
                holder.senderMessage.text = item.message.text
                holder.senderTime.text = item.message.created_at_str
            }
            else -> {
                holder.senderLayout.makeGone()
                holder.receiverLayout.makeVisible()
                holder.receiverMessage.text = item.message.text
                holder.receiverTime.text = item.message.created_at_str
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderName: TextView = view.findViewById(R.id.sender_name)
        val senderMessage: TextView = view.findViewById(R.id.sender_message)
        val receiverMessage: TextView = view.findViewById(R.id.receiver_message)
        val senderTime: TextView = view.findViewById(R.id.sender_time)
        val receiverTime: TextView = view.findViewById(R.id.receiver_time)
        val senderLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)
        val receiverLayout: ConstraintLayout = view.findViewById(R.id.constraint_layout_2)
    }
}