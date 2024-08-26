package com.emall.net.network.model.evaluationDevicesDetail

data class DATA(
    val can_evaluate: Boolean,
    val ends_at: String,
    val ends_at_str: String,
    val evaluated_amount: Int,
    val extra_notes: ExtraNotes,
    val id: Int,
    val product: Product,
    val shipping_id: Int,
    val starts_at: String,
    val status: String,
    val steps: Int,
    val updated_at: String
)