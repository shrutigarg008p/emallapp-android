package com.emall.net.network.model.getProfileResponse

data class GetProfileResponseData(
    val active: String,
    val country_code: String,
    val email: String,
    val fname: String,
    val id: String,
    val lname: String,
    val mob: String,
    val newsletter: Boolean,
    val image_data: String
)