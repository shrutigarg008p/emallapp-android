package com.emall.net.network.model.getShippingInformationRequest

data class AddressInformation(
    val billing_address: BillingAddress,
    val shipping_address: ShippingAddress,
    val shipping_carrier_code: String,
    val shipping_method_code: String
)