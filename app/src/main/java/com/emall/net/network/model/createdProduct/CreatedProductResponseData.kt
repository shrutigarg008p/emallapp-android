package com.emall.net.network.model.createdProduct

data class CreatedProductResponseData(
    val DATA: ProductData,
    val MESSAGE: String,
    val STATUS: Int
)