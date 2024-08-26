package com.emall.net.network.model.getCartItems

data class Data(
    val cart_id: String,
    val currency_code: String,
    val gift_wrap: GiftWrap,
    val gift_wrap_fee: Double,
    val grandtotal: Double,
    val items: ArrayList<Item>,
    val subtotal: Double
)