package com.emall.net.network.model.getIssueDetail

data class DATA(
    val actions: Actions,
    val auction_id: Any,
    val closed_at: Any,
    val closed_by: Any,
    val description: String,
    val evaluation_id: Int,
    val id: Int,
    val number: String,
    val raised_at: Any,
    val raised_by: String,
    val status: Int,
    val status_text: String,
    val title: String,
    val type: Int,
    val type_text: String
)