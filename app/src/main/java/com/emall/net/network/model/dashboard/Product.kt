package com.emall.net.network.model.dashboard

data class Product(
    val category_name: String,
    val created: String,
    val id: String,
    val image: String,
    val name: String,
    val old_price: Int,
    val price: Int,
    val sku: String
)