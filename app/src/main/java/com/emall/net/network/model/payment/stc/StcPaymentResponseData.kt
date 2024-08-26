package com.emall.net.network.model.payment.stc

data class StcPaymentResponseData(
    val DATA: STCPaymentInfo,
    val MESSAGE: String,
    val STATUS: Int
)