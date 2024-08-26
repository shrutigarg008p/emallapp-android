package com.emall.net.network.model.auctions.auctionList

data class AuctionResponseData(
    val DATA: AuctionDetails,
    val MESSAGE: String,
    val STATUS: Int
)