package com.emall.net.network.model.chat.channelList

data class Channel(
    val created_at: String,
    val created_at_str: String,
    val id: Int,
    val participants: String,
    val updated_at_str: String,
    val updated_at: String
)