package com.emall.net.network.model.addAddressResponse

data class AddAddressData(
    val address_id: String,
    val city: String,
    val country: String,
    val country_id: String,
    val firstname: String,
    val lastname: String,
    val postcode: String,
    val street: String,
    val telephone: String
)