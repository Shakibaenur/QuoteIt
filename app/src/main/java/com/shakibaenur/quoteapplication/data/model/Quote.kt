package com.shakibaenur.quoteapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quote(
    var id:Int?=-1,
    val author: String? = "",
    val category: String? = "",
    val quote: String? = "",
    var isLiked: Boolean? = false
) : Parcelable