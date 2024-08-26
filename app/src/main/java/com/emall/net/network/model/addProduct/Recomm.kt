package com.emall.net.network.model.addProduct

data class Recomm(
    val created_at: String,
    val ends_at: String,
    val id: Int,
    val product_id: Int,
    val status: Int,
    val updated_at: String
)