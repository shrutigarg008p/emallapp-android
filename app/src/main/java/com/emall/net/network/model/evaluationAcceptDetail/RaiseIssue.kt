package com.emall.net.network.model.evaluationAcceptDetail

data class RaiseIssue(
    val action: String,
    val message: List<String>,
    val text: String,
    val type: String,
    val type_text: String
)