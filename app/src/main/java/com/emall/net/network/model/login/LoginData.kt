package com.emall.net.network.model.login

data class LoginData(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val user: UserDetails
)