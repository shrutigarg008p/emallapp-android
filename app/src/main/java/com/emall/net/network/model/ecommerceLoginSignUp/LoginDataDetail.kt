package com.emall.net.network.model.ecommerceLoginSignUp


data class LoginDataDetail(

	val id: String,
	val email: String,
	val fname: String,
	val lname: String,
	val country_code: String,
	val mob: String,
	val active: String,
	val wallet_active: String,
	val password: String,
	val login_type: String,
)