package com.emall.net.network.model.categoryList

data class Data(
    val id: String,
    val name: String,
    val promotionals: ArrayList<Promotional>,
    val subcategory: ArrayList<Subcategory>
)