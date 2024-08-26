package com.emall.net.network.model.evaluationAcceptDetail

data class Product(
    val additional_images: ArrayList<String>,
    val base_image: String,
    val created_at: String,
    val created_at_str: String,
    val description: String,
    val id: Int,
    val name: String,
    val serial_no: String,
    val serial_no_img: String,
    val short_description: String,
    val small_image: String,
    val submited_for: String,
    val thumbnail_image: String
)