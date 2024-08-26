package com.emall.net.network.model.evaluationAcceptDetail

data class ReEvaluationStatus(
    val action: String,
    val message: List<String>,
    val re_evaluation_amt: String,
    val re_evaluation_reason: String
)