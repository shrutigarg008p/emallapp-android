package com.emall.net.network.model.login

data class UserDetails(
    val email: String,
    val first_name: String,
    val id: Int,
    val last_name: String,
    val phone: String,
    val phone_verified_at: String,
    val email_verified_at: String,
    val type: String
)