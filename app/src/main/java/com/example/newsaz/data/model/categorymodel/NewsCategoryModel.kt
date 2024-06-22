package com.example.newsaz.data.model.categorymodel

import javax.inject.Inject

data class NewsCategoryModel @Inject constructor(
    val title: String,
    val params: Params
)