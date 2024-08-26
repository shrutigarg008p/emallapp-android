package com.emall.net.network.model.auctions.createNewAuctionForTap

data class CreateNewAuctionTapResponseData(
    val DATA: AuctionInfo,
    val MESSAGE: String,
    val STATUS: Int
)