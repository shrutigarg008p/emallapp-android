package com.emall.net.network.model.verifyStcPaymentOtpResponse

data class VerifyStcPaymentOtpResponseData(
    val data: List<Any>,
    val msg: String,
    val order_id: String
)