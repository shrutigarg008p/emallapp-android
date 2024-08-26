package com.emall.net.network.model.orderListResponse

data class OrderListResponse(
    val data: ArrayList<OrderListResponseData>,
    val response: String
)