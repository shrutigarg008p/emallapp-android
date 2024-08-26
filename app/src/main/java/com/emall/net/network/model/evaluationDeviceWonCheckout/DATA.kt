package com.emall.net.network.model.evaluationDeviceWonCheckout

data class DATA(
    val amount: String,
    val extra_notes: ExtraNotes,
    val seller: Seller,
    val shipping_id: Int
)