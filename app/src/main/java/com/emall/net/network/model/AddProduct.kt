package com.emall.net.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class AddProduct(
    var categoryId: Int = 0,
    var shortDescription: String = "",
    var description: String = "",
    var weight: Double = 0.00,
    var model: String = "",
    var modelId: Int = 0,
    var brand: String = "",
    var brandId: Int = 0,
    var variant: String = "",
    var variantId: Int = 0,
    var question1: String ="",
    var question2: String ="",
    var question3: String ="",
    var serialNumberImage: String = "",
    var submitted_for: String = "",
    var serialNo : String = ""
) : Parcelable