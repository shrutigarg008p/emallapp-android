package com.emall.net.network.model.updateCartItems

data class UpdateCartItem(
    val item_id: String,
    val qty: Int,
    val quote_id: String,
    val sku: String
)