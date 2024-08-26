package com.emall.net.network.model.dashboard

data class DataHotSale(
    val category_name: String,
    val created: String,
    val id: String,
    val image: String,
    val name: String,
    val old_price: Double,
    val price: Double,
    val sku: String
)