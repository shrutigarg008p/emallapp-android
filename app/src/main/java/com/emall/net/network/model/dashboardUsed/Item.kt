package com.emall.net.network.model.dashboardUsed

data class Item(
    val entity_id: String,
    val image: String,
    val image_alt: String,
    val name: String,
    val sort_order: String,
    val status: String
)