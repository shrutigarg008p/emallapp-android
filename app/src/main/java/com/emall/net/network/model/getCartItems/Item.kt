package com.emall.net.network.model.getCartItems

data class Item(
    val id: String,
    val img: String,
    val instock: Int,
    val name: String,
    val old_price: Double,
    val optionss: Optionss,
    val price: Double,
    val product_id: String,
    var qty: Int,
    val sku: String
)