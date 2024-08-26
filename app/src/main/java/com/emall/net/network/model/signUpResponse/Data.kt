package com.emall.net.network.model.signUpResponse

data class Data(
    val confirmation: Any,
    val created_at: String,
    val created_in: String,
    val default_billing: Any,
    val default_shipping: Any,
    val device_used: String,
    val disable_auto_group_change: String,
    val dob: Any,
    val email: String,
    val entity_id: String,
    val failures_num: String,
    val first_failure: Any,
    val firstname: String,
    val gender: Any,
    val group_id: String,
    val increment_id: Any,
    val is_active: String,
    val lastname: String,
    val lock_expires: Any,
    val middlename: Any,
    val mobilenumber: String,
    val password_hash: String,
    val prefix: Any,
    val rp_token: String,
    val rp_token_created_at: String,
    val store_id: String,
    val suffix: Any,
    val taxvat: Any,
    val updated_at: String,
    val website_id: String
)