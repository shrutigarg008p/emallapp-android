package com.emall.net.network.model.updateAddressRequest

data class UpdateAddressParamData(
    val address_id: Int,
    val firstname: String,
    val lastname: String,
    val country_id: String,
    val city: String,
    val mobilenumber: String,
    val street: String,
    val latitude: String,
    val longitude: String,
    val postcode: String,
)