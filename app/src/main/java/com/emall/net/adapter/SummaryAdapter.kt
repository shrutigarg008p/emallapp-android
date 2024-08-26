package com.emall.net.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.model.QuestionAnswer
import com.emall.net.R


internal class SummaryAdapter(private var summaryList: List<QuestionAnswer>) :
    RecyclerView.Adapter<SummaryAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.summary_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = summaryList[position]
        when {
            item.answer.equals(holder.itemView.resources.getString(R.string.Yes),true) -> holder.itemQuestionAnswer.text = Html.fromHtml(item.question + "<font color=\"#979797\">" + "   True" + "</font>")
            item.answer.equals(holder.itemView.resources.getString(R.string.no),true) -> holder.itemQuestionAnswer.text = Html.fromHtml(item.question + "<font color=\"#979797\">" + "   False"+ "</font>")
            else -> holder.itemQuestionAnswer.text = Html.fromHtml(item.question + "<font color=\"#979797\">" + "   "+item.answer + "</font>")
        }
    }

    override fun getItemCount(): Int {
        return summaryList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemQuestionAnswer: TextView = view.findViewById(R.id.item_question_answer)
    }
}