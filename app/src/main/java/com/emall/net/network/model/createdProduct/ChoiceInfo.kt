package com.emall.net.network.model.createdProduct

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChoiceInfo(
    val id: Int,
    val value: String
) : Parcelable