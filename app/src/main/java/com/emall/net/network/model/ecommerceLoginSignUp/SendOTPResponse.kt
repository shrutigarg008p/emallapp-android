package com.emall.net.network.model.ecommerceLoginSignUp

data class SendOTPResponse(
    val success: Boolean,
    val resend: String,
)