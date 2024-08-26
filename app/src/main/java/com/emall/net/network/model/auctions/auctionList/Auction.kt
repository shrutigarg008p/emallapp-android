package com.emall.net.network.model.auctions.auctionList

data class Auction(
    val ends_at: String = "",
    val ends_at_str: String = "",
    val id: Int = 0,
    val at_step: Int = 0,
    val in_progress: Boolean = false,
    val starts_at: String = "",
    val starts_at_str: String = "",
    val status: String = "",
    val is_fee_paid: Boolean= false
)