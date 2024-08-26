package com.emall.net.network.model.setPaymentMethodRequest

data class BillingAddress(
    val region: String,
    val region_id: Int,
    val region_code: String,
    val country_id: String,
    val street: ArrayList<String>,
    val postcode: String,
    val city: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val telephone: String
)