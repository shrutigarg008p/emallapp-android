package com.emall.net.network.model.updateBankDetails

data class BankRequestParams(val bank_name: String ="",val phone_number: String = "",val iban: String = "",val stc_pay_wallet: String= "")