package com.example.newsaz.data.model.newsmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class NewsListModel @Inject constructor(
    val id:Int,
    val date: Int,
    val title: String,
    @SerializedName("title_image_medium")
    val image: String,
    @SerializedName("excerpt")
    val source: String,
    @SerializedName("category_title")
    val category: String,
    val link: String,
    @SerializedName("category_id")
    val categoryId: Int
):Parcelable