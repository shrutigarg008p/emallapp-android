package com.emall.net.network.model.ecommerceLoginSignUp/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class SignUpData(

    val entity_id: Int,
    val website_id: Int,
    val email: String,
    val group_id: Int,
    val increment_id: String,
    val store_id: Int,
    val created_at: String,
    val updated_at: String,
    val is_active: Int,
    val disable_auto_group_change: Int,
    val created_in: String,
    val prefix: String,
    val firstname: String,
    val middlename: String,
    val lastname: String,
    val suffix: String,
    val dob: String,
    val password_hash: String,
    val rp_token: String,
    val rp_token_created_at: String,
    val default_billing: String,
    val default_shipping: String,
    val taxvat: String,
    val confirmation: String,
    val gender: String,
    val mobilenumber: Int,
    val failures_num: Int,
    val first_failure: String,
    val lock_expires: String,
    val device_used: String,
)