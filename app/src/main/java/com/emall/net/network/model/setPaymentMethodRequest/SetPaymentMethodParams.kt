package com.emall.net.network.model.setPaymentMethodRequest

data class SetPaymentMethodParams(
    val paymentMethod: PaymentMethod,
    val billing_address: BillingAddress
)