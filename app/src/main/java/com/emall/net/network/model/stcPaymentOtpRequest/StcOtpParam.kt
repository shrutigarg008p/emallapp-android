package com.emall.net.network.model.stcPaymentOtpRequest

data class StcOtpParam(
    val order_id: String,
    val amount: String,
    val stcmobile: String
)