package com.emall.net.network.model.ecommerceSignUp

data class CreateAccountResponse(
    val addresses: List<Addresse>,
    val created_at: String,
    val created_in: String,
    val custom_attributes: List<CustomAttribute>,
    val default_billing: String,
    val default_shipping: String,
    val disable_auto_group_change: Int,
    val email: String,
    val extension_attributes: ExtensionAttributes,
    val firstname: String,
    val group_id: Int,
    val id: Int,
    val lastname: String,
    val store_id: Int,
    val updated_at: String,
    val website_id: Int
)