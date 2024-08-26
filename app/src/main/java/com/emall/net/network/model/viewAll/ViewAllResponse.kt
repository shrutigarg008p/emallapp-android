package com.emall.net.network.model.viewAll

import com.emall.net.network.model.ProductListData

data class ViewAllResponse(
    val response : String,
    val data : ArrayList<ProductListData>,
)