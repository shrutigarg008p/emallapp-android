package com.emall.net.network.model.cartRetainRequest

data class CartRetainParamsData(
    val cart_id: String,
    val devicetype: String,
    val order_id: String
)