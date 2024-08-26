package com.emall.net.network.model.auctionDetail

data class Product(
    val additional_images: List<String>,
    val attributes: List<Attribute>,
    val base_image: String,
    val brand: String,
    val device_name: String,
    val id: Int,
    val model: String,
    val name: String,
    val qna: List<QuestionAnswer>,
    val serial_no: String,
    val serial_no_img: String,
    val small_image: String,
    val submit_type: String,
    val thumbnail_image: String,
    val variant: String
)