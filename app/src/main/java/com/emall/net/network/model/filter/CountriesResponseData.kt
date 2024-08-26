package com.emall.net.network.model.filter

data class CountriesResponseData(
    val DATA: ArrayList<CountriesData>,
    val MESSAGE: String,
    val STATUS: Int
)