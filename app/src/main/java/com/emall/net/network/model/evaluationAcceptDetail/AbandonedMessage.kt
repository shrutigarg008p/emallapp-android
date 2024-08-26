package com.emall.net.network.model.evaluationAcceptDetail

data class AbandonedMessage(
    val action: String,
    val chat_with_bidder: ChatWithBidderX,
    val message: List<String>,
    val note: String,
    val raise_issue: RaiseIssue,
    val text: String
)