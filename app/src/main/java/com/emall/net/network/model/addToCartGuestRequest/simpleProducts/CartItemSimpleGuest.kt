package com.emall.net.network.model.addToCartGuestRequest.simpleProducts

data class CartItemSimpleGuest(
    val product_type: String,
    val qty: Int,
    val quote_id: String,
    val sku: String
)