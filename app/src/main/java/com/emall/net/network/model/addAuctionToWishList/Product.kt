package com.emall.net.network.model.addAuctionToWishList

data class Product(
    val additional_images: ArrayList<String>,
    val base_image: String,
    val brand: String,
    val description: String,
    val device_name: String,
    val id: Int,
    val model: String,
    val name: String,
    val serial_no_img: String,
    val short_description: String,
    val small_image: String,
    val submit_type: String,
    val thumbnail_image: String,
    val variant: String,
    val weight: Double
)