package com.emall.net.network.model.verifyStcPaymentOtpRequest

data class VerifyStcOtpParam(
    val OtpReference: String,
    val OtpValue: String,
    val STCPayPmtReference: String,
    val order_id: String,
    val devicetype: String
)