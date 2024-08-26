package com.emall.net.network.model.evaluationWishList

data class DataX(
    val action_status: String,
    val action_status_msg: String,
    val ends_at: String,
    val ends_at_str: String,
    val id: Int,
    val is_new: Boolean,
    val is_wishlist: Boolean,
    val product: Product,
    val shipping_id: Int,
    val starts_at: String,
    val status: String,
    val steps: Int,
    val updated_at: String
)