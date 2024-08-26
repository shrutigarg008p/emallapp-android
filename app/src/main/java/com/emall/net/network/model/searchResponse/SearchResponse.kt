package com.emall.net.network.model.searchResponse

data class SearchResponse(
    val data: ArrayList<SearchResponseData>,
    val page: Int,
    val response: String
)