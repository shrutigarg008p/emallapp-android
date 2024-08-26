package com.emall.net.network.model.createdProduct

import com.emall.net.network.model.ProductInfo

data class ProductData(
    val brands: ArrayList<ProductInfo>,
    val category: ProductInfo,
    val questions: ArrayList<Question>
)