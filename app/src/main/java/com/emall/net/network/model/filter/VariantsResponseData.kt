package com.emall.net.network.model.filter

data class VariantsResponseData(
    val DATA: ArrayList<VariantsData>,
    val MESSAGE: String,
    val STATUS: Int
)