package com.shakibaenur.quoteapplication.data.model

data class Popular(
    val author: String? = "",
    val category: String? = "",
    val quote: String? = "",
    var count: Int? = 0,
    var qouteId: Int? = 0

)