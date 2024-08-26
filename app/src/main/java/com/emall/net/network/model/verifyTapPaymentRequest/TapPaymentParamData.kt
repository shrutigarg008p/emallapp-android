package com.emall.net.network.model.verifyTapPaymentRequest

data class TapPaymentParamData(
    val order_id: String,
    val charge_id: String,
    val source_id:String
)