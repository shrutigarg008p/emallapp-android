package com.emall.net.network.model.orderListResponse

data class OrderListResponseProducts(
    val color: Object,
    val id: String,
    val image: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val size: Object,
    val sku: String,
    val total: Double,
    val type: String,
)