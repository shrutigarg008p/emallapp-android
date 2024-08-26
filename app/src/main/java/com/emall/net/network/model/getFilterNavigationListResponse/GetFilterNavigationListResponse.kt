package com.emall.net.network.model.getFilterNavigationListResponse

data class GetFilterNavigationListResponse(
    val data: ArrayList<GetFilterNavigationListResponseData>,
    val msg: String,
    val page: Int,
    val product_count: Int,
)