package com.emall.net.network.model.addressList

data class AddressListData(
    val address_id: String,
    val city: String,
    val country: String,
    val country_id: String,
    val default_billing: Int,
    val default_shipping: Int,
    val firstname: String,
    val lastname: String,
    val latitude: Any,
    val longitude: Any,
    val postcode: String,
    val street: String,
    val telephone: String
)