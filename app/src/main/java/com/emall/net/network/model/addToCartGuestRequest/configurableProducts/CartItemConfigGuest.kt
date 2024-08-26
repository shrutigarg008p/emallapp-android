package com.emall.net.network.model.addToCartGuestRequest.configurableProducts

data class CartItemConfigGuest(
    val sku: String,
    val qty: Int,
    val quote_id: String,
    val product_option: ProductOptionConfigGuest
)