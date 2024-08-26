package com.emall.net.network.model.auctionDevicesWon

data class AuctionDevicesWon(
    val action_button: ActionButton,
    val chat_with_seller: ChatWithSeller,
    val ends_at: String,
    val ends_at_str: String,
    val extra_notes: ExtraNotes,
    val id: Int,
    val product: Product,
    val raise_issue: RaiseIssue,
    val shipping_id: Int,
    val starts_at: String,
    val starts_at_str: String,
    val status: String,
    val steps: Int
)