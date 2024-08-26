package com.emall.net.network.model.changePasswordRequest

data class ChangePasswordRequestParam(
    val customer_id: Int,
    val newpassword: String
)