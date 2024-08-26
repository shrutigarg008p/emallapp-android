package com.emall.net.network.model.evaluationDetail

data class DataX(
    val ends_at: String,
    val ends_at_str: String,
    val id: Int,
    val in_progress: Boolean,
    val is_fee_paid: Boolean,
    val starts_at: String,
    val starts_at_str: String,
    val status: String
)