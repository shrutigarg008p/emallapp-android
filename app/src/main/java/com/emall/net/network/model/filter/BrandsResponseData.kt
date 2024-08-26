package com.emall.net.network.model.filter

import com.emall.net.network.model.ProductInfo

data class BrandsResponseData(
    val DATA: ArrayList<ProductInfo>,
    val MESSAGE: String,
    val STATUS: Int
)