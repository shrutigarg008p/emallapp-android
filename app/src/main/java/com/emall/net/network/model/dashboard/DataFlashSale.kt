package com.emall.net.network.model.dashboard

data class DataFlashSale(
    val products: ArrayList<ProductX>,
    val sale_timer: Int,
    val text_one: String,
    val text_two: String
)