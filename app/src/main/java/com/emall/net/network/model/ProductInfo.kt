package com.emall.net.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductInfo(
    val id: Int,
    val title: String,
    val image: String
) : Parcelable