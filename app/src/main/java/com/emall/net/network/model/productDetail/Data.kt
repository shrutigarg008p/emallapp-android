package com.emall.net.network.model.productDetail

data class Data(
    val color_img: String,
    val color_name: String,
    val color_value: String,
    val description: String,
    val image: ArrayList<String>,
    val name: String,
    val old_price: Double,
    val price: Double,
    val product_id: String,
    val seller_id: String,
    val seller_name: String,
    val short_description: String,
    val sku: String,
    val stock: Boolean,
    val type: String
)