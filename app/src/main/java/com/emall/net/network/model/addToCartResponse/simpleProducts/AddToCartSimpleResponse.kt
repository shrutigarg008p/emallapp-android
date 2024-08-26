package com.emall.net.network.model.addToCartResponse.simpleProducts

data class AddToCartSimpleResponse(
    val item_id: Int,
    val name: String,
    val price: Int,
    val product_type: String,
    val qty: Int,
    val quote_id: String,
    val sku: String
)