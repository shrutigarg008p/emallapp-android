package com.emall.net.network.model.payment.tap.tapPayment

data class TapPaymentResponseData(
    val DATA: TAPPaymentInfo,
    val MESSAGE: String,
    val STATUS: Int
)