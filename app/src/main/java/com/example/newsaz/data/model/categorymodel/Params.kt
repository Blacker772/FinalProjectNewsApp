package com.example.newsaz.data.model.categorymodel

import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class Params @Inject constructor(
    @SerializedName("category_id")
    val category: Int
)
