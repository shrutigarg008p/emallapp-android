package com.emall.net.network.model.guestToken

data class GuestTokenResponse(
    val mask_key: String,
    val msg: String,
    val quote_id: Int
)