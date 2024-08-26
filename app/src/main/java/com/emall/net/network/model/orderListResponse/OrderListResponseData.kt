package com.emall.net.network.model.orderListResponse

data class OrderListResponseData(
    val canreturn: Int,
    val created_at: String,
    val currency: String,
    val grand_total: Double,
    val increment_id: String,
    val order_id: String,
    val products: ArrayList<OrderListResponseProducts>,
    val shipping_amount: Double,
    val status: String,
    val status_code: String,
    val tracking_url: String,
)