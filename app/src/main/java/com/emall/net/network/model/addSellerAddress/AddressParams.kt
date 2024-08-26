package com.emall.net.network.model.addSellerAddress

data class AddressParams(
    val address_1: String = "",
    val address_2: String = "",
    val city: String = "",
    val state_code: String = "",
    val country_code: String = "",
    val zip: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val phone: String = "",
    val phone_code: String = ""
)