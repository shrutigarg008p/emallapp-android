package com.emall.net.model

data class RaiseAnIssueQueryParams(
    val item_id: Int,
    val item_type: String,
    val title: String,
    val description: String,
    val issue_type: Int,
)