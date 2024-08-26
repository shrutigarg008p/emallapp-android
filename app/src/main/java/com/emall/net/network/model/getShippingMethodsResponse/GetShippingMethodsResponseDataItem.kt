package com.emall.net.network.model.getShippingMethodsResponse

data class GetShippingMethodsResponseDataItem(
    val amount: Double,
    val available: Boolean,
    val base_amount: Double,
    val carrier_code: String,
    val carrier_title: String,
    val error_message: String,
    val method_code: String,
    val method_title: String,
    val price_excl_tax: Double,
    val price_incl_tax: Double,
)