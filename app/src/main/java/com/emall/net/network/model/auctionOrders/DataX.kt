package com.emall.net.network.model.auctionOrders

data class DataX(
    val auction: Auction,
    val created_at: String,
    val created_at_str: String,
    val id: Int,
    val status: String,
    val updated_at: String
)