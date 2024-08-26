package com.emall.net.network.model.auctionDevices

data class DATAX(
    val bid_live_str: String,
    val btn_link_str: String,
    val ends_at: String,
    val ends_at_str: String,
    val id: Int,
    val is_wishlist: Boolean,
    val product: Product,
    val starts_at: String,
    val starts_at_str: String,
    val status: String,
    val status_str: String,
    val steps: Int
)