package com.emall.net.network.model.setCurrencyToQuoteRequest

data class SetCurrencyToQuoteRequestParam(
    val quote_id: Int,
    val currency_code: String,
)