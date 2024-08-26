package com.emall.net.network.model.evaluationUserAddress

data class AddressRequestBodyParams(
    val fullname: String? ="",
    val address_1: String? = "",
    val address_2: String? = "",
    val city: String? = "",
    val country_code: String? = "",
    val state_code: String? ="",
    val zip : String? = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val phone: String? = "",
    val phone_code: String? =""
)