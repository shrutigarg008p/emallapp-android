package com.emall.net.network.model.payment.tap.tapPayment

data class TAPPaymentInfo(
    val auction_id: Int,
    val orderNumber: Int,
    val transactionRef: String
)