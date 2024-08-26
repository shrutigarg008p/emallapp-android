package com.emall.net.network.model.login

data class LoginResponse(
    val DATA: LoginData,
    val MESSAGE: String,
    val STATUS: Int
)