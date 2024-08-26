package com.emall.net.network.model.ecommerceLoginSignUp

import android.net.Uri

data class SellProductData(
    var serialNumber: String = "",
    var leftImage: Uri? = null,
    var rightImage: Uri? = null,
    var topImage: Uri? = null,
    var bottomImage: Uri? = null,
    var frontImage: Uri? = null,
    var backImage: Uri? = null,
)