package com.emall.net.network.model.trackShipments

data class DataX(
    val bill_sticker_pdf: String,
    val booking_ref: String,
    val created_at: String,
    val events_raw: Any,
    val id: Int,
    val ship_from: Int,
    val ship_from_str: String,
    val ship_to: Int,
    val ship_to_str: String,
    val status: String,
    val updated_at: String,
    val user_id: Int,
    val waybill_ref: String
)