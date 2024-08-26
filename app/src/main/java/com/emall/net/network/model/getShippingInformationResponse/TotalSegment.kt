package com.emall.net.network.model.getShippingInformationResponse

data class TotalSegment(
    val area: String,
    val code: String,
    val extension_attributes: ExtensionAttributesX,
    val title: String,
    val value: Double,
)