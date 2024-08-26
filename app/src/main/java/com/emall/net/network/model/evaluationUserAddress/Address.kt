package com.emall.net.network.model.evaluationUserAddress

data class Address(
    val address_1: String,
    val address_2: String,
    val city: String,
    val country: String,
    val country_code: String,
    val fullname: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val phone: String,
    val phone_code: String,
    val state: String,
    val state_code: String,
    val zip: String
)