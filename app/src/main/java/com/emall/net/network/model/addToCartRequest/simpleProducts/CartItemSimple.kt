package com.emall.net.network.model.addToCartRequest.simpleProducts

data class CartItemSimple(
    val sku: String,
    val qty: Int,
    val quote_id: String

)