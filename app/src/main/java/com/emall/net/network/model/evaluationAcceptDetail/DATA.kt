package com.emall.net.network.model.evaluationAcceptDetail

data class DATA(
    val abandoned_message: Any,
    val current_status: String,
    val ends_at: String,
    val ends_at_str: String,
    val evaluations: ArrayList<Evaluation>,
    val extra_notes: ExtraNotes,
    val fee_payment: String,
    val id: Int,
    val in_progress: Boolean,
    val is_fee_paid: Boolean,
    val is_product_delivered: Boolean,
    val is_product_paid: Boolean,
    val is_user_absher_verified: Boolean,
    val order: Order,
    val product: Product,
    val shipment: Shipment,
    val starts_at: String,
    val starts_at_str: String,
    val status: String,
    val status_reason: Any
)