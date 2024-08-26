package com.emall.net.network.model.evaluationAcceptDetail

data class ChatWithBidderX(
    val action: String,
    val message: List<String>,
    val text: String,
    val user_id: Int
)