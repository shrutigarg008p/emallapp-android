package com.emall.net.network.model.auctionDevicesWon

data class Issue(
    val id: Int,
    val number: String,
    val type:Int,
    val type_str: String,
    val raised_by: String,
    val closed_by: String,
    val status: Int,
    val status_str: String,
    val title: String,
    val description: String
)
