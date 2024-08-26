package com.emall.net.network.model.getIssuesList

data class IssueListData(
    val actions: Actions,
    val auction_id: Int,
    val closed_at: String,
    val closed_by: String,
    val description: String,
    val evaluation_id: Int,
    val id: Int,
    val number: String,
    val raised_at: String,
    val raised_by: String,
    val status: Int,
    val status_text: String,
    val title: String,
    val type: Int,
    val type_text: String
)