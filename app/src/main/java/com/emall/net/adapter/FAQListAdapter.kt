package com.emall.net.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.model.FaqData
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible

internal class FAQListAdapter(private var faqList: ArrayList<FaqData>) :
    RecyclerView.Adapter<FAQListAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = faqList[position]
        holder.question.text = item.question
        holder.answer.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(item.answer, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(item.answer)
        }
        holder.answer.makeGone()
        holder.answerBtn.setOnClickListener {

            if (holder.answer.isVisible) {
                holder.answerBtn.setImageResource(R.drawable.ic_faq_down)
                holder.answer.makeGone()
            } else {
                holder.answerBtn.setImageResource(R.drawable.ic_faq_up)
                holder.answer.makeVisible()
            }
        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.findViewById(R.id.question)
        val answer: TextView = view.findViewById(R.id.answer)
        val answerBtn: AppCompatImageView = view.findViewById(R.id.answer_btn)
    }
}