package com.emall.net.network.model.chat.viewChannelMessages

data class MessageX(
    val created_at: String,
    val created_at_str: String,
    val id: Int,
    val is_mine: Boolean,
    val text: String
)