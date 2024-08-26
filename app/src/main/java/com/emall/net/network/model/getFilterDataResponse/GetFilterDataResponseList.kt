package com.emall.net.network.model.getFilterDataResponse



data class GetFilterDataResponseList  (
    val code: String,
    val data: ArrayList<GetFilterDataResponseListData>,
    val name: String
)