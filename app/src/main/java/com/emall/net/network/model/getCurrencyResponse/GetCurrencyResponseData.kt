package com.emall.net.network.model.getCurrencyResponse

data class GetCurrencyResponseData(
    val available_currency_codes: ArrayList<String>,
    val base_currency_code: String,
    val base_currency_symbol: String,
    val default_display_currency_code: String,
    val default_display_currency_symbol: String,
    val exchange_rates: ArrayList<ExchangeRate>
)