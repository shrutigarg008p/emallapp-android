package com.emall.net.network.model.stcPaymentOtpResponse

data class StcPaymentOtpResponseData(
    val OtpReference: String,
    val STCPayPmtReference: String,
    val `data`: List<Any>,
    val msg: String,
    val order_id: String,
    val stcmobile: String
)