package com.emall.net.network.model.addToCartRequest.configurableProducts

data class CartItem(
    val sku: String,
    val qty: Int,
    val quote_id: String,
    val product_option: ProductOption
)