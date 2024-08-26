package com.emall.net.network.model.bankList

data class BankListResponseData(
    val DATA: ArrayList<BankList>,
    val MESSAGE: String,
    val STATUS: Int
)