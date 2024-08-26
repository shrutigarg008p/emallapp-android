package com.emall.net.network.model.updateProfileRequest

data class UpdateProfileRequestParam(
    val customer_id: Int,
    val email: String,
    val firstname: String,
    val image_data: ImageData,
    val is_profilepic_changed: Boolean,
    val lastname: String,
    val mobilenumber: String,
    val newsletter: Boolean,
    val phone: String
)