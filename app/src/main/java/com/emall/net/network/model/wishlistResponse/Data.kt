package com.emall.net.network.model.wishlistResponse

data class Data(
    val item_id: String,
    val type: String,
    val product_id: String,
    val name: String,
    val sku: String,
    val price: Double,
    val old_price: Double,
    val img: String,
    val instock: Boolean,
    val deal: Int,
)