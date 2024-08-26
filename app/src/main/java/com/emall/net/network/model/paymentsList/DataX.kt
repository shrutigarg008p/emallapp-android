package com.emall.net.network.model.paymentsList

data class DataX(
    val action: String,
    val amount: String,
    val created_at: String,
    val currency: String,
    val deleted_at: Any,
    val id: Int,
    val notes: Any,
    val offline_payment_slip: Any,
    val payment_gateway: String,
    val remote_id: String,
    val remote_response_raw: String,
    val status: String,
    val txn_ref: String,
    val updated_at: String,
    val user_id: Int
)