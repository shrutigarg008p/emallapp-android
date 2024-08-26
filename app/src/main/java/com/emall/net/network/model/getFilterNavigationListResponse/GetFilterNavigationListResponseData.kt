package com.emall.net.network.model.getFilterNavigationListResponse

data class GetFilterNavigationListResponseData(
    val created: String,
    val id: String,
    val img: String,
    val name: String,
    val old_price: Int,
    val price: Int,
    val sku: String,
    val type: String
)