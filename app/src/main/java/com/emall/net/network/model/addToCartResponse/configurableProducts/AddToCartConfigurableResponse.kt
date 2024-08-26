package com.emall.net.network.model.addToCartResponse.configurableProducts

data class AddToCartConfigurableResponse(
    val item_id: Int,
    val name: String,
    val price: Int,
    val product_option: ProductOption,
    val product_type: String,
    val qty: Int,
    val quote_id: String,
    val sku: String
)