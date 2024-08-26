package com.emall.net.network.model.contactUsRequest

data class ContactUsRequestParam(
    val email: String,
    val name: String,
    val comment: String,
    val telephone: String
)