package com.emall.net.network.model.auctionDevices

data class Product(
    val additional_images: ArrayList<String>,
    val attributes: ArrayList<Attribute>,
    val base_image: String,
    val brand: String,
    val device_name: String,
    val id: Int,
    val model: String,
    val name: String,
    val serial_no: String,
    val serial_no_img: String,
    val small_image: String,
    val submit_type: String,
    val thumbnail_image: String,
    val variant: String
)