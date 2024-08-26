package com.emall.net.network.model.getShippingMethodsRequest

data class Address(
    val region: String,
    val region_id: Int,
    val region_code: String,
    val country_id: String,
    val street: ArrayList<String>,
    val postcode: String,
    val city: String,
    val firstname: String,
    val lastname: String,
    val customer_id: Int,
    val email: String,
    val telephone: String,
    val same_as_billing: Int,

    )