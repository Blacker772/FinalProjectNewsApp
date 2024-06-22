package com.example.newsaz.data.model.newsmodel

import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class NewsDetailsModel @Inject constructor(
    val title: String,
    val content: String,
    @SerializedName("excerpt")
    val source: String,
    @SerializedName("date_desc")
    val date: String,
    @SerializedName("date_smart")
    val time: String,
    @SerializedName("title_image_medium")
    val image: String,
    @SerializedName("category_title")
    val category: String,
    val link: String
)