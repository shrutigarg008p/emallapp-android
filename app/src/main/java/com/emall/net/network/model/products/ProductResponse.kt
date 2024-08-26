package com.emall.net.network.model.products

data class ProductResponse(
    val DATA: ProductData,
    val MESSAGE: String,
    val STATUS: Int
)