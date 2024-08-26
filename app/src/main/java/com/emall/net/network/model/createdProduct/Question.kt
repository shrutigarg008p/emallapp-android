package com.emall.net.network.model.createdProduct

import android.os.Parcelable
import com.emall.net.network.model.ProductInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
    val choices: ArrayList<ChoiceInfo>? = null,
    val id: Int = 0,
    val input_type: InputType? = null,
    val subtext: String = "",
    val title: String = ""
) : Parcelable