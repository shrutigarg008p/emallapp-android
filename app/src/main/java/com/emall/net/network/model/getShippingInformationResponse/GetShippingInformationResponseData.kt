package com.emall.net.network.model.getShippingInformationResponse

data class GetShippingInformationResponseData(
    val payment_methods: List<PaymentMethod>,
    val totals: Totals
)