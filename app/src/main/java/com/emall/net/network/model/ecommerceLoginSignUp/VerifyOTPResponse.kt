package com.emall.net.network.model.ecommerceLoginSignUp


data class VerifyOTPResponse(

	val success: Boolean,
	val otp: Int,
	val customerId: Int,
	val url: String,
)