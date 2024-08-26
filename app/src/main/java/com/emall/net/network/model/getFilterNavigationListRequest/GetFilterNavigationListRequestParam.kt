package com.emall.net.network.model.getFilterNavigationListRequest

import com.emall.net.network.model.getFilterNavigationResponse.GetFilterNavigationResponseData

data class GetFilterNavigationListRequestParam(
    val cat_id: Int,
    val type: String,
    val order_by: String,
    val sort_order: String,
    val page: Int,
    val product_count: Int,
    val data: ArrayList<GetFilterNavigationResponseData>,
)