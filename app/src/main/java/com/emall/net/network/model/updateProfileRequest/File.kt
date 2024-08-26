package com.emall.net.network.model.updateProfileRequest

data class File(
    val base64_encoded_data: String,
    val name: String,
    val type: String
)