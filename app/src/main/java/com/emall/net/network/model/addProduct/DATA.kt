package com.emall.net.network.model.addProduct

data class DATA(
    val product: Product,
    val recomm: Recomm,
    val submitted_for: String
)