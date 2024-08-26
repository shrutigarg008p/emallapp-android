package com.emall.net.network.model.filter

data class CountriesData(
    val capital: String,
    val currency: String,
    val id: Int,
    val iso2: String,
    val iso3: String,
    val name: String,
    val phone_code: String,
    val region: String,
    val subregion: String,
    val timezones: String
)