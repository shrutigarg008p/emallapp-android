package com.emall.net.network.model.auctionDetail

data class DATA(
    val bidded_amount: String,
    val biddings: List<Bidding>,
    val can_bid: Boolean,
    val ends_at: String,
    val ends_at_str: String,
    val extra_notes: ExtraNotes,
    val id: Int,
    val product: Product,
    val starts_at: String,
    val starts_at_str: String,
    val status: String,
    val steps: Int
)