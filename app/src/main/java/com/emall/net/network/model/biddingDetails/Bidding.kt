package com.emall.net.network.model.biddingDetails

data class Bidding(
    val action: Action,
    val amount: String,
    val id: Int,
    val is_selected: Boolean,
    val selected_at: String,
    val selected_at_str: String,
    val selected_by: SelectedBy
)