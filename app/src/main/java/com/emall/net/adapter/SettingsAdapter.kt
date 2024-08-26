package com.emall.net.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.emall.net.R
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.SettingsItemList
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.setBottomMargin
import com.emall.net.utils.Utility.setTopMargin
import java.util.*


internal class SettingsAdapter(
    private var settingsItemList: ArrayList<SettingsItemList>,
    val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<SettingsAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = settingsItemList[position]
        val id = item.id
        holder.settingsIcon.setImageResource(item.icon)
        holder.settingsItemTitle.text = item.title
        if (item.subTitle.isNotEmpty()) {
            holder.settingsItemSubTitle.makeVisible()
            if (id.equals("country", true)) {
                holder.settingsItemSubTitle.text = item.subTitle + PreferenceHelper.readString(
                    Constants.SELECTED_COUNTRY_NAME)
            } else {
                holder.settingsItemSubTitle.text = item.subTitle
            }
        } else {
            holder.settingsItemSubTitle.makeGone()
        }

        when (id) {
            "notification" -> {
                holder.settingsArrowIcon.makeGone()
                holder.notificationSwitch.makeVisible()
                holder.settingsFollowUsLayout.makeGone()
            }
            "followUs" -> {
                holder.settingsArrowIcon.makeVisible()
                holder.notificationSwitch.makeGone()
                holder.settingsFollowUsLayout.makeVisible()
                holder.settingsItemListLayout.setTopMargin(100)
            }
            else -> {
                holder.settingsArrowIcon.makeVisible()
                holder.notificationSwitch.makeGone()
                holder.settingsFollowUsLayout.makeGone()
            }
        }

        if (position == settingsItemList.size - 1) {
            holder.settingsItemListLayout.setBottomMargin(100)
        }

        holder.settingsItemListLayout.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, id)
        })

        holder.facebookIcon.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "facebook")
        })
        holder.twitterIcon.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "twitter")
        })
        holder.instagramIcon.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(position, "instagram")
        })
    }


    override fun getItemCount(): Int {
        return settingsItemList.size
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var settingsIcon: ImageView = view.findViewById(R.id.settings_icon)
        var settingsArrowIcon: ImageView = view.findViewById(R.id.settings_arrow_icon)
        var facebookIcon: ImageView = view.findViewById(R.id.facebook_icon)
        var twitterIcon: ImageView = view.findViewById(R.id.twitter_icon)
        var instagramIcon: ImageView = view.findViewById(R.id.instagram_icon)
        var settingsItemTitle: TextView = view.findViewById(R.id.settings_item_title)
        var settingsItemSubTitle: TextView = view.findViewById(R.id.settings_item_sub_title)
        var settingsFollowUsLayout: ConstraintLayout =
            view.findViewById(R.id.settings_follow_us_layout)
        var settingsItemListLayout: ConstraintLayout =
            view.findViewById(R.id.settings_item_list_layout)
        var notificationSwitch: Switch = view.findViewById(R.id.notification_switch)

    }
}