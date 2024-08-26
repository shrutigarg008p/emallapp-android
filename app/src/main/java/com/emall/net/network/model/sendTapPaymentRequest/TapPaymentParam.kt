package com.emall.net.network.model.sendTapPaymentRequest

data class TapPaymentParam(
    val order_id: String,
    val amount: String,
)