package com.emall.net.network.model.getFilterNavigationListRequest

data class GetFilterNavigationListRequestParamData(
    val code: String,
    val name: String,
    val data: ArrayList<GetFilterNavigationListRequestParamDataList>,

    )