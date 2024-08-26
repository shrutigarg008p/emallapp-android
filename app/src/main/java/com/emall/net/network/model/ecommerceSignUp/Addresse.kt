package com.emall.net.network.model.ecommerceSignUp

data class Addresse(
    val city: String,
    val country_id: String,
    val customer_id: Int,
    val default_billing: Boolean,
    val default_shipping: Boolean,
    val firstname: String,
    val id: Int,
    val lastname: String,
    val postcode: String,
    val region: Region,
    val region_id: Int,
    val street: List<String>,
    val telephone: String
)