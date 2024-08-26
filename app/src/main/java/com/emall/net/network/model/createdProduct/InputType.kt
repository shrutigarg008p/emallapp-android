package com.emall.net.network.model.createdProduct

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InputType(
    val id: Int,
    val title: String,
    val type: String
) : Parcelable