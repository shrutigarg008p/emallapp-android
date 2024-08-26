package com.emall.net.network.model.auctionDeviceWonCheckout

data class DATA(
    val amount: Int,
    val extra_notes: ExtraNotes,
    val seller: Seller,
    val shipping_id: Int
)