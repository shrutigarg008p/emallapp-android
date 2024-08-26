package com.emall.net.network.model.searchResponse

data class SearchResponseData(
    val created: String,
    val id: String,
    val img: String,
    val name: String,
    val old_price: Double,
    val price: Double,
    val sku: String
)