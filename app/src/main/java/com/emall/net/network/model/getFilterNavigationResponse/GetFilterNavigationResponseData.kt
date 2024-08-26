package com.emall.net.network.model.getFilterNavigationResponse

data class GetFilterNavigationResponseData(
    val code: String,
    val data: ArrayList<GetFilterNavigationResponseDataList>,
    val name: String
)