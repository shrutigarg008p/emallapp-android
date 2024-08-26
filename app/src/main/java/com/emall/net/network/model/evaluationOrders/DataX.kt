package com.emall.net.network.model.evaluationOrders

data class DataX(
    val created_at: String,
    val created_at_str: String,
    val evaluation: Evaluation,
    val id: Int,
    val status: String,
    val updated_at: String
)