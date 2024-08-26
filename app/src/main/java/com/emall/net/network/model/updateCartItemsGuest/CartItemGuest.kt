package com.emall.net.network.model.updateCartItemsGuest

data class CartItemGuest(
    val item_id: String,
    val qty: String,
    val quote_id: String,
    val sku: String
)